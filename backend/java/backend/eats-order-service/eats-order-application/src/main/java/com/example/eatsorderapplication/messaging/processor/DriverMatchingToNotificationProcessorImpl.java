package com.example.eatsorderapplication.messaging.processor;

import com.example.commondata.dto.order.UserAddressDto;
import com.example.commondata.message.MessageConverter;
import com.example.eatsorderapplication.application.dto.DriverDetailsDto;
import com.example.eatsorderapplication.application.service.OrderService;
import com.example.eatsorderapplication.application.service.driver.DriverService;
import com.example.eatsorderapplication.application.service.driver.Matching;
import com.example.kafka.avro.model.DriverMatchingEvent;
import com.example.kafka.avro.model.RestaurantApprovalNotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@Configuration
public class DriverMatchingToNotificationProcessorImpl {

    private final int maxDriverCount = 5; // 드라이버 매칭에 필요한 최대 드라이버 수
    private final Duration interval = Duration.ofSeconds(10); // 최대 기다리는 시간 (10초)

    private final DriverService driverService;
    private final OrderService orderService;

    private final ConcurrentHashMap<UUID, Message<DriverMatchingEvent>> messageMap
        = new ConcurrentHashMap<>();

    private final Sinks.Many<Tuple3<
        Set<DriverDetailsDto>,
        Set<UserAddressDto>,
        Set<UserAddressDto>>> getNearByDriversSink
        = Sinks.many().multicast().onBackpressureBuffer();

    public DriverMatchingToNotificationProcessorImpl(DriverService driverService, OrderService orderService) {
        this.driverService = driverService;
        this.orderService = orderService;
    }

    @Bean
    public Consumer<Flux<Message<DriverMatchingEvent>>> driverMatchingRequestListener() {
        return flux -> flux
            .map(m -> {
                messageMap.put(UUID.fromString(m.getPayload().getUserId().toString()), m);
                return m;
            })
            .map(MessageConverter::toRecord)
            .doOnNext(event -> log.info("Received DriverMatchingEvent: {}", event.message().toString()))
            .flatMap(record -> orderService.findUserAddressDtoByOrderId(
                UUID.fromString(record.message().getCorrelationId().toString())))
            .windowTimeout(maxDriverCount, interval) // maxDriverCount 만큼의 드라이버가 모이거나, interval 이 지나면 매칭 수행
            .flatMap(window -> window
                .collectList()
                .filter(list -> !list.isEmpty()) // 리스트가 비어 있지 않을 때만 처리
                .flatMap(driverService::getNearbyDriversFromUsers))
            .doOnNext(getNearByDriversSink::tryEmitNext) // Sink에 퍼블리시
            .subscribe();

    }

    @Bean
    public Supplier<Flux<Message<RestaurantApprovalNotificationEvent>>> driverMatchingResultPublisher() {
        return () -> getNearByDriversSink.asFlux()
            .flatMap(tuple3 -> driverService.performMatching(Tuples.of(tuple3.getT1(), tuple3.getT2())))
            .flatMap(Flux::fromIterable)
            .doOnNext(matching -> {
                var key = UUID.fromString(matching.getAddress().userId());
                // 성공한 매칭 메시지 처리
                messageMap.get(key)
                    .getHeaders()
                    .get(KafkaHeaders.ACKNOWLEDGMENT, ReceiverOffset.class)
                    .acknowledge();

            })
            .doOnNext(matching -> {
                var key = UUID.fromString(matching.getAddress().userId());
                messageMap.remove(key);
            })
            .map(this::toMessage);
    }

    @Bean
    public Supplier<Flux<Message<DriverMatchingEvent>>> failedDriverMatchingResultPublisher() {
        return () -> getNearByDriversSink.asFlux()
            .flatMap(tuple3 -> Flux.fromIterable(tuple3.getT3())) // 실패한 userLocation 처리
            .map(userAddress -> {
                var key = UUID.fromString(userAddress.userId());
                var message = messageMap.get(key);
                messageMap.remove(key);
                return message;
            });
    }

    // 매칭 알고리즘 수행


    // 매칭 결과를 RestaurantApprovalNotificationEvent로 변환
    private Message<RestaurantApprovalNotificationEvent> toMessage(Matching matching) {
        var notificationEvent = RestaurantApprovalNotificationEvent.newBuilder()
            .setCorrelationId(matching.getOrderId().toString())
            .setUserId(matching.getAddress().userId())
            // driverId
            .setMessage("")
            .setCreatedAt(Instant.now())
            .build();

        // 매칭된 결과를 notificationEvent에 설정
        return MessageBuilder.withPayload(notificationEvent)
            .setHeader(KafkaHeaders.KEY, notificationEvent.getUserId().toString())
            .build();
    }

    private Message<DriverMatchingEvent> toMessage(DriverMatchingEvent event) {
        return MessageBuilder.withPayload(event)
            .setHeader(KafkaHeaders.KEY, event.getCorrelationId().toString()) // Order ID를 key로 사용
            .build();
    }

}
