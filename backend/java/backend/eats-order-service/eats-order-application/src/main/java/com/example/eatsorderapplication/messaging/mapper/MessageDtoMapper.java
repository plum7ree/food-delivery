package com.example.eatsorderapplication.messaging.mapper;

import com.example.commondata.domain.events.notification.NotificationEvent;
import com.example.eatsorderdomain.data.domainentity.Order;

import java.time.Instant;
import java.util.UUID;

public class MessageDtoMapper {

    public static NotificationEvent toNotificationCreated(Order order) {
        // UUID 및 다른 필드를 변환하여 NotificationCreated 객체 생성
        return NotificationEvent.NotificationCreated.builder()
            .orderId(order.getId())
            .userId(order.getCallerId())  // Assuming CallerId is the user ID
            .notificationType(NotificationEvent.NotificationType.ORDER_APPROVED)  // You may need to adjust this
            .message("Your order has been approved!")  // Example message
            .orderDetails(NotificationEvent.OrderDetails.builder()
                .orderId(order.getId())
                .totalAmount(order.getPrice().intValue())  // Convert Double to Integer
                .build())
            .driverDetails(NotificationEvent.DriverDetails.builder()
                .driverId("driver-id-placeholder")  // Example placeholder, adjust according to your logic
                .lat(0.0)  // Example placeholder
                .lon(0.0)  // Example placeholder
                .build())
            .createdAt(Instant.now())  // Current timestamp
            .build();
    }


}
