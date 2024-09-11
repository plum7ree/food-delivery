package com.example.eatsorderapplication.messaging.processor;

import com.example.commondata.domain.events.notification.NotificationEvent;
import com.example.commondata.domain.events.order.OrderEvent;
import com.example.eatsorderapplication.messaging.mapper.MessageDtoMapper;
import com.example.eatsorderapplication.service.OrderService;
import com.example.eatsorderdomain.data.domainentity.Order;
import com.example.eatsorderdomain.data.mapper.DtoDataMapper;
import com.example.kafka.avro.model.NotificationType;
import com.example.kafka.avro.model.OrderDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class OrderEventProcessorImpl implements OrderEventProcessor<NotificationEvent> {

    private final OrderService orderService;

    @Override
    public Mono<NotificationEvent> handle(OrderEvent.OrderCreated event) {
        return null;

    }


    @Override
    public Mono<NotificationEvent> handle(OrderEvent.OrderApprovedByRestaurant event) {
        return this.orderService.completeRestaurantApproval(event.orderId())
            .map(MessageDtoMapper::toNotificationCreated)
            .doOnNext(e -> log.info("to NotificationCreated{}", e));
//            .transform(exceptionHandler(event));
    }

    @Override
    public Mono<NotificationEvent> handle(OrderEvent.OrderRejectedByRestaurant event) {
        return null;
    }

    @Override
    public Mono<NotificationEvent> handle(OrderEvent.OrderCompleted event) {
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
