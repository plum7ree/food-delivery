package com.example.commondata.domain.aggregate;

import com.example.commondata.domain.aggregate.valueobject.*;
import com.example.commondata.domain.events.order.OrderStatus;
import jakarta.persistence.Transient;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

// https://www.baeldung.com/lombok-builder-inheritance
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends AggregateRoot<PaymentId> {
    private OrderId orderId;
    private CallerId callerId;
    private CalleeId calleeId;
    private Money price;
    private UUID sagaId;
    @Transient
    private String paymentKey;
    private OrderStatus status;
}