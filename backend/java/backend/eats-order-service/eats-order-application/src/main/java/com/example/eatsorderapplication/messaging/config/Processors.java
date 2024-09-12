package com.example.eatsorderapplication.messaging.config;

import com.example.commondata.domain.events.notification.NotificationEvent;
import com.example.eatsorderapplication.mappers.MessageToEventConverter;
import com.example.eatsorderapplication.messaging.processor.OrderEventProcessor;
import com.example.kafka.avro.model.DriverMatchingEventAvroModel;
import com.example.kafka.avro.model.NotificationAvroModel;
import com.example.kafka.avro.model.RequestAvroModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Objects;
import java.util.function.Function;

@Slf4j
@Configuration
public class Processors {


    private final OrderEventProcessor<NotificationEvent> eventProcessor;
    private final Sinks.Many<NotificationEvent> notificationEventSink = Sinks.many().multicast().onBackpressureBuffer();

    public Processors(OrderEventProcessor<NotificationEvent> eventProcessor) {

        this.eventProcessor = eventProcessor;
    }

    // Kafka Receiver 역할
//    @Bean
//    public Consumer<Message<RequestAvroModel>> restaurantApprovalInput() {
//        return message -> {
//            RequestAvroModel request = message.getPayload();
//            log.info("Received Kafka message: {}", request);
//
//            try {
//                OrderStatus orderStatus = OrderStatus.valueOf(request.getOrderStatus().name());
//                if (OrderStatus.CALLEE_APPROVED == orderStatus) {
//                    processOrder(request);
//                }
//            } catch (Exception e) {
//                log.error("Error processing message: {}", e.getMessage());
//            }
//        };
//    }
    @Bean
    public Function<Flux<Message<RequestAvroModel>>,
        Tuple2<Flux<Message<NotificationAvroModel>>, Flux<Message<DriverMatchingEventAvroModel>>>
        > restaurantApprovalResponseProcessor() {
        return flux ->
        {
            flux.map(MessageToEventConverter::toOrderEvent)
                .filter(Objects::nonNull)  // null 값 필터링
                .doOnNext(r -> log.info("approval event received {}", r.message()))
                .concatMap(r -> eventProcessor.process(r.message())
                    .doOnSuccess(e -> r.acknowledgement().acknowledge())
                )
                .doOnNext(notificationEventSink::tryEmitNext)
                .subscribe(); // 초기에 subscribe() 한번 되고 이제 계속 유지됨.

            return Tuples.of(
                notificationEventSink.asFlux().transform(toNotificationMessage()),
                notificationEventSink.asFlux().transform(toDriverMatchingMessage())
            );
        };
    }


    private Function<Flux<NotificationEvent>, Flux<Message<NotificationAvroModel>>> toNotificationMessage() {
        return flux -> flux.map(event -> {
            NotificationAvroModel model = NotificationAvroModel.newBuilder()
                .build();
            return MessageBuilder.withPayload(model)
                .setHeader("key", event.orderId().toString())
                .build();
        });
    }

    private Function<Flux<NotificationEvent>, Flux<Message<DriverMatchingEventAvroModel>>> toDriverMatchingMessage() {
        return flux -> flux.map(event -> {
            DriverMatchingEventAvroModel model = DriverMatchingEventAvroModel.newBuilder()
                .build();
            return MessageBuilder.withPayload(model)
                .setHeader("key", event.orderId().toString())
                .build();
        });
    }
}
