package com.example.eatsorderapplication.messaging.config;

import com.example.commondata.message.MessageConverter;
import com.example.eatsorderapplication.messaging.processor.OrderEventProcessor;
import com.example.kafka.avro.model.DriverMatchingEvent;
import com.example.kafka.avro.model.OrderEvent;
import com.example.kafka.avro.model.RestaurantApprovalNotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Objects;
import java.util.function.Function;

@Slf4j
@Configuration
public class Processors {


    private final OrderEventProcessor<RestaurantApprovalNotificationEvent> userNotificationEventProcessor;
    private final OrderEventProcessor<DriverMatchingEvent> driverMatchingEventProcessor;


    public Processors(OrderEventProcessor<RestaurantApprovalNotificationEvent> userNotificationEventProcessor, OrderEventProcessor<DriverMatchingEvent> driverMatchingEventProcessor) {
        this.userNotificationEventProcessor = userNotificationEventProcessor;
        this.driverMatchingEventProcessor = driverMatchingEventProcessor;

    }

    @Bean
    public Function<Flux<Message<OrderEvent>>,
        Tuple2<Flux<Message<RestaurantApprovalNotificationEvent>>, Flux<Message<DriverMatchingEvent>>>
        > restaurantApprovalResponseProcessor() {
        return flux -> {
            // 두 개의 개별 Flux 생성
            Flux<Tuple2<Message<RestaurantApprovalNotificationEvent>, Message<DriverMatchingEvent>>> processedFlux =
                flux.map(MessageConverter::toRecord)
                    .filter(Objects::nonNull)  // null 값 필터링
                    .doOnNext(r -> log.info("approval event received {}", r.message()))
                    .flatMap(r -> Mono.zip(
                        userNotificationEventProcessor.process(r.message()),  // 첫 번째 이벤트 프로세서
                        driverMatchingEventProcessor.process(r.message())    // 두 번째 이벤트 프로세서
                    ).map(tuple -> {
                        // 각각의 결과를 Message로 변환
                        Message<RestaurantApprovalNotificationEvent> notificationMessage = toMessage(tuple.getT1());
                        Message<DriverMatchingEvent> driverMatchingMessage = toMessage(tuple.getT2());
                        return Tuples.of(notificationMessage, driverMatchingMessage);
                    }));

            // 각각의 Flux로 분리
            Flux<Message<RestaurantApprovalNotificationEvent>> notificationFlux = processedFlux.map(Tuple2::getT1);
            Flux<Message<DriverMatchingEvent>> driverMatchingFlux = processedFlux.map(Tuple2::getT2);

            return Tuples.of(notificationFlux, driverMatchingFlux);  // 두 개의 Flux를 Tuple2로 반환
        };
    }


    private Message<DriverMatchingEvent> toMessage(DriverMatchingEvent event) {
        return MessageBuilder.withPayload(event)
            .setHeader(KafkaHeaders.KEY, event.getCorrelationId().toString()) // Order ID를 key로 사용
            .build();
    }

    private Message<RestaurantApprovalNotificationEvent> toMessage(RestaurantApprovalNotificationEvent event) {
        return MessageBuilder.withPayload(event)
            .setHeader(KafkaHeaders.KEY, event.getCorrelationId().toString()) // Order ID를 key로 사용
            .build();
    }
}


