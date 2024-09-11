package com.example.eatsorderapplication.messaging.mapper;

import com.example.commondata.domain.events.order.OrderEvent;
import com.example.kafka.avro.model.RequestAvroModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import reactor.kafka.receiver.ReceiverOffset;

public class MessageConverter {
    private static final ObjectMapper objectMapper = new ObjectMapper();

//    public static <T> Record<T> toRecord(Message<T> message) {
//        var payload = message.getPayload();
//        var key = message.getHeaders().get(KafkaHeaders.RECEIVED_KEY, String.class);
//        var ack = message.getHeaders().get(KafkaHeaders.ACKNOWLEDGMENT, ReceiverOffset.class);
//        return new Record<>(key, payload, ack);
//    }

    public static Record<OrderEvent> toOrderEvent(Message<RequestAvroModel> message) {
        var payload = message.getPayload();
        var key = message.getHeaders().get(KafkaHeaders.RECEIVED_KEY, String.class);
        var ack = message.getHeaders().get(KafkaHeaders.ACKNOWLEDGMENT, ReceiverOffset.class);
        // order created 이벤트
        if (payload.getEventType().equals(
            OrderEvent.OrderCreated.class.getSimpleName())) {
            var orderEvent = OrderEvent.OrderCreated.builder()
                .orderId(payload.getOrderId())
                .build();
            return new Record<>(key, orderEvent, ack);
        }
        // 레스토랑 승인이벤트
        else if (payload.getEventType().equals(
            OrderEvent.OrderApprovedByRestaurant.class.getSimpleName())) {
            var orderEvent = OrderEvent.OrderApprovedByRestaurant.builder()
                .orderId(payload.getOrderId())
                .build();
            return new Record<>(key, orderEvent, ack);
        }
        // 레스토랑 주문 취소 이벤트
        else if (payload.getEventType().equals(
            OrderEvent.OrderRejectedByRestaurant.class.getSimpleName())) {
            var orderEvent = OrderEvent.OrderRejectedByRestaurant.builder()
                .orderId(payload.getOrderId())
                .build();
            return new Record<>(key, orderEvent, ack);
        }
        // order 완료 이벤트
        else if (payload.getEventType().equals(
            OrderEvent.OrderCompleted.class.getSimpleName())) {
            var orderEvent = OrderEvent.OrderCompleted.builder()
                .orderId(payload.getOrderId())
                .build();
            return new Record<>(key, orderEvent, ack);

        } else {
            return null;
        }
    }
}
