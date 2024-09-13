package com.example.eatsorderapplication.messaging.processor;

import com.example.eatsorderapplication.application.service.OrderService;
import com.example.kafka.avro.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class OrderEventProcessorToNotificationImpl implements OrderEventProcessor<RestaurantApprovalNotificationEvent> {

    private final OrderService orderService;

    @Override
    public Mono<RestaurantApprovalNotificationEvent> handle(OrderCreated event) {
        return null;

    }


    @Override
    public Mono<RestaurantApprovalNotificationEvent> handle(OrderApprovedByRestaurant event) {
        log.info("approved by restaurant handle called");
        return this.orderService.findById(UUID.fromString(event.getOrderId().toString()))
            .flatMap(o ->
                Mono.just(RestaurantApprovalNotificationEvent.newBuilder()
                    .setCorrelationId(event.getOrderId())
                    .setUserId(o.getCallerId().toString())
                    .setMessage("order approved")
                    .setCreatedAt(Instant.now())
                    .build()));
//        return this.orderService.completeRestaurantApproval(event.orderId())
//            .map(MessageDtoMapper::toNotificationCreated)
//            .doOnNext(e -> log.info("to NotificationCreated{}", e));
//            .transform(exceptionHandler(event));
//        return Mono.just(UserNotificationEvent.newBuilder()
//            .setCorrelationId(event.getOrderId())
//            .setMessage("order approved")
//            .setCreatedAt(Instant.now())
//            .build());
    }

    @Override
    public Mono<RestaurantApprovalNotificationEvent> handle(OrderRejectedByRestaurant event) {
        return null;
    }

    @Override
    public Mono<RestaurantApprovalNotificationEvent> handle(OrderCompleted event) {
        return null;
    }


//    // Order를 처리하는 로직
//    @Transactional
//    public Mono<void> processRestaurantApproval(com.example.kafka.avro.model.RequestAvroModel message) {
//        Order order = DtoDataMapper.requestAvroToOrder(message);
//        completeOrder(order, UUID.fromString(message.getSagaId().toString()));
//
//    }


//    // Kafka Sender 역할
//    public void createNotificationEvent(Order order) {
//        com.example.kafka.avro.model.NotificationAvroModel message = com.example.kafka.avro.model.NotificationAvroModel.newBuilder()
//            .setUserId(order.getCallerId().getValue())
//            .setNotificationType(NotificationType.ORDER_APPROVED)
//            .setOrderDetails(OrderDetails.newBuilder()
//                .setOrderId(order.getId().getValue())
//                .setTotalAmount(decimalConversion.toBytes(order.getPrice().getAmount(),
//                    com.example.kafka.avro.model.RequestAvroModel.getClassSchema().getField("price").schema(),
//                    com.example.kafka.avro.model.RequestAvroModel.getClassSchema().getField("price").schema().getLogicalType())).build())
//            .setMessage("restaurant approved!").build();
//
//  }

}