package com.example.commondata.domain.events.notification;

import com.example.commondata.domain.events.DomainEvent;
import com.example.commondata.domain.events.OrderSaga;
import com.example.commondata.domain.events.order.OrderEvent;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

public sealed interface NotificationEvent extends DomainEvent, OrderSaga {

    @Builder
    record NotificationCreated(
        UUID orderId,
        UUID userId,
        NotificationType notificationType,
        String message,
        OrderDetails orderDetails,
        DriverDetails driverDetails,
        Instant createdAt) implements NotificationEvent {
    }

    enum NotificationType {
        ORDER_APPROVED,
        DRIVER_MATCHED,
        ORDER_CANCELLED,
        DRIVER_ARRIVED
    }

    @Builder
    record OrderDetails(
        UUID orderId,
        Integer totalAmount) {
    }

    @Builder
    record DriverDetails(
        String driverId,
        double lat,
        double lon) {
    }
}
