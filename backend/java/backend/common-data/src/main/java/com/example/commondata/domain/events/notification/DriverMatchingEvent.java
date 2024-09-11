package com.example.commondata.domain.events.notification;

import com.example.commondata.domain.events.DomainEvent;
import com.example.commondata.domain.events.OrderSaga;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

public sealed interface DriverMatchingEvent extends DomainEvent, OrderSaga {

    @Builder
    record DriverMatchingCreated(
        UUID orderId,
        UUID userId,
        NotificationType notificationType,
        String message,
        OrderDetails orderDetails,
        DriverDetails driverDetails,
        Instant createdAt) implements DriverMatchingEvent {
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
