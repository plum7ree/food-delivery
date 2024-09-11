package com.example.commondata.domain.events.order;

import com.example.commondata.domain.events.DomainEvent;
import com.example.commondata.domain.events.OrderSaga;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

public sealed interface OrderEvent extends DomainEvent, OrderSaga {

    /*
        Intentionally using primitive wrapper types to keep things simple
     */

    @Builder
    record OrderCreated(UUID orderId,
                        UUID productId,
                        UUID customerId,
                        Integer quantity,
                        Integer unitPrice,
                        Integer totalAmount,
                        Instant createdAt) implements OrderEvent {
    }

    @Builder
    record OrderApprovedByRestaurant(UUID orderId,
                                     Instant createdAt) implements OrderEvent {
    }


    @Builder
    record OrderRejectedByRestaurant(UUID orderId,
                                     Instant createdAt) implements OrderEvent {
    }

    @Builder
    record OrderCompleted(UUID orderId,
                          Instant createdAt) implements OrderEvent {
    }

}
