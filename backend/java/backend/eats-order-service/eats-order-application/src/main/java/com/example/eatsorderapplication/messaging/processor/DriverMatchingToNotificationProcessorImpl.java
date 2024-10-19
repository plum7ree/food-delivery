package com.example.eatsorderapplication.messaging.processor;

import com.example.commondata.domain.events.order.DriverMatchedStatus;
import com.example.commondata.dto.order.UserOrderAddressDto;
import com.example.commondata.message.MessageConverter;
import com.example.eatsorderapplication.application.dto.DriverDetailsDto;
import com.example.eatsorderapplication.application.service.OrderService;
import com.example.eatsorderapplication.application.service.driver.Candidate;
import com.example.eatsorderapplication.application.service.driver.DriverService;
import com.example.eatsorderapplication.application.service.driver.Matching;
import com.example.kafka.avro.model.*;
//import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@Configuration
public class DriverMatchingToNotificationProcessorImpl {

    private final Integer maxWindowCount; // 드라이버 매칭에 필요한 최대 드라이버 수
    private final Duration driverWindowMaxInterval; // 최대 기다리는 시간 (10초)

    private final DriverService driverService;
    private final OrderService orderService;

    private final ConcurrentHashMap<
        UUID,
        Tuple2<Long, Message<DriverMatchingRequestEvent>>> messageMap
        = new ConcurrentHashMap<>();

    // commitAsync 이기 때문에 commit 순서 보장을 하기 위한 테크닉.
    // 현재까지 받은 메시지중 가장 마지막 메시지만 ack 부르면 된다.
    private static AtomicLong offsetCounter = new AtomicLong(0);

    private final Sinks.Many<Tuple2<
        Set<Candidate>,
        Set<UserOrderAddressDto>>> getNearbyDriversSink;

    private final Sinks.Many<Matching> matchedResultSink;

    public DriverMatchingToNotificationProcessorImpl(
        @Qualifier("maxWindowCount") Integer maxWindowCount,
        @Qualifier("driverWindowMaxInterval") Duration interval,
        DriverService driverService,
        OrderService orderService,
        @Qualifier("nearbySink") Sinks.Many<Tuple2<
            Set<Candidate>,
            Set<UserOrderAddressDto>>>
            getNearByDriversSink,
        @Qualifier("matchedResult") Sinks.Many<Matching>
            matchedResultSink) {
        this.maxWindowCount = maxWindowCount;
        this.driverWindowMaxInterval = interval;
        this.driverService = driverService;
        this.orderService = orderService;
        this.getNearbyDriversSink = getNearByDriversSink;
        this.matchedResultSink = matchedResultSink;
    }

    /**
     * TODO 1. geo.search failed 난 것들은 kafka failed queue 로 잘 가는지.
     *  2. 지역별 파티셔닝
     *  3. 카프카에서 중복된 메시지 다시 읽는 다면 멱등성 체크.
     *
     * @return
     */
    @Bean
    public Consumer<Flux<Message<DriverMatchingRequestEvent>>> driverMatchingRequestListener() {
        return flux -> flux
            .map(m -> {
                var tuple2 = Tuples.of(offsetCounter.incrementAndGet(), m);
                messageMap.put(UUID.fromString(m.getPayload().getCorrelationId().toString()), tuple2);
                return m;
            })
            .map(MessageConverter::toRecord)
//            .doOnNext(event -> log.info("Received DriverMatchingRequestEvent: {}", event.message().toString()))
            .flatMap(record -> orderService.findUserAddressDtoByOrderId(
                UUID.fromString(record.message().getCorrelationId().toString())))
            .windowTimeout(maxWindowCount, driverWindowMaxInterval) // maxDriverCount 만큼의 드라이버가 모이거나, interval 이 지나면 매칭 수행
            .flatMap(window -> window
                .collectList()
                .filter(list -> !list.isEmpty()) // 리스트가 비어 있지 않을 때만 처리
                .flatMap(driverService::getNearbyDriversFromUsers)
                .flatMap(tuple2 -> driverService.filterDriversWithoutLock(tuple2.getT1(), tuple2.getT2()))
            )

            .doOnNext(getNearbyDriversSink::tryEmitNext) // Sink에 퍼블리시
            .subscribe();

    }

    /**
     * 성공한 매칭 메시지 처리
     *
     * @return
     */
    @Bean
    public Supplier<Flux<Message<UserNotificationEvent>>> driverMatchedNotificationPublisher() {
        return () -> getNearbyDriversSink.asFlux()
            // 현재 candidate set 에는 {user1, driver1}, {user1, driver2}, {user2, driver3}
            .flatMap(tuple2 -> driverService.performMatching(tuple2.getT1()))
            .doOnNext(matchings -> log.info("matching successful: {}", matchings))
            // TODO tryLock 구현. 레디스 하나만 쓰면 동시성문제가 없으므로 여러 드라이버와 유저를 한번에 전부 안겹치게 락을 걸 수 있다.
            //  만일 클러스터를 쓴다면? 락 시도 실패하면 재시도를 해야하나?
            .flatMap(Flux::fromIterable)
            .flatMap(driverService::tryLock)
            .doOnNext(matchedResultSink::tryEmitNext)
            .doOnNext(matching -> {
                var key = UUID.fromString(matching.getUserOrderAddress().orderId());

                var count = messageMap.get(key).getT1();
                // commitAsync 이기 때문에 commit 순서 보장을 하기 위한 테크닉.
                // 현재까지 받은 메시지중 가장 마지막 메시지만 ack 부르면 된다.
                if (offsetCounter.get() <= count) {
                    var message = messageMap.get(key).getT2();
                    message.getHeaders()
                        .get(KafkaHeaders.ACKNOWLEDGMENT, ReceiverOffset.class)
                        .acknowledge();
                }

            })
            .doOnNext(matching -> {
                var key = UUID.fromString(matching.getUserOrderAddress().orderId());
                messageMap.remove(key);
            })
            .map(this::toMessage);
    }

    @Bean
    public Supplier<Flux<Message<DriverMatchedEvent>>> driverMatchedEventPublisher() {
        return () -> matchedResultSink.asFlux()
            .flatMap(matching -> {
                var addressDto = matching.getUserOrderAddress().address();
                var driverDto = matching.getDriver();
                var message = DriverMatchedEvent.newBuilder()
                    .setStatus(DriverMatchedStatus.MATCHED.name())
                    .setCorrelationId(matching.getUserOrderAddress().orderId())
                    .setUserId(matching.getUserOrderAddress().userId())
                    .setAddress(Address.newBuilder()
                        .setCity(addressDto.getCity())
                        .setPostalCode(addressDto.getPostalCode())
                        .setStreet(addressDto.getStreet())
                        .build())
                    .setDriverDetails(DriverDetails.newBuilder()
                        .setDriverId(driverDto.getDriverId())
                        .setLat(driverDto.getLat())
                        .setLon(driverDto.getLon())
                        .build())
                    .setCreatedAt(Instant.now())
                    .build();
                return Mono.just(message);
            })
            .map(this::toMessage);
    }

    /**
     * 실패한 DriverMatchingRequestEvent 카프카에 다른 토픽으로 전송
     *
     * @return
     */
    @Bean
    public Supplier<Flux<Message<DriverMatchingRequestEvent>>> failedDriverMatchingResultPublisher() {
        return () -> getNearbyDriversSink.asFlux()
            .flatMap(tuple3 -> Flux.fromIterable(tuple3.getT2()))
            .map(userOrderAddressDto -> {
                var key = UUID.fromString(userOrderAddressDto.orderId());
                var message = messageMap.get(key).getT2();
                messageMap.remove(key);
                return message;
            });
    }

    // 매칭 알고리즘 수행


    // 매칭 결과를 UserNotificationEvent로 변환
    private Message<UserNotificationEvent> toMessage(Matching matching) {
        var notificationEvent = UserNotificationEvent.newBuilder()
            .setCorrelationId(matching.getUserOrderAddress().orderId())
            .setUserId(matching.getUserOrderAddress().userId())
            // driverId
            .setMessage("")
            .setCreatedAt(Instant.now())
            .build();

        // 매칭된 결과를 notificationEvent에 설정
        return MessageBuilder.withPayload(notificationEvent)
            .setHeader(KafkaHeaders.KEY, notificationEvent.getCorrelationId().toString())
            .build();
    }

    private Message<DriverMatchingRequestEvent> toMessage(DriverMatchingRequestEvent event) {
        return MessageBuilder.withPayload(event)
            .setHeader(KafkaHeaders.KEY, event.getCorrelationId().toString()) // Order ID를 key로 사용
            .build();
    }

    private Message<DriverMatchedEvent> toMessage(DriverMatchedEvent event) {
        return MessageBuilder.withPayload(event)
            .setHeader(KafkaHeaders.KEY, event.getCorrelationId().toString()) // Order ID를 key로 사용
            .build();
    }

}
