package com.example.commondata.domain.aggregate;

import com.example.commondata.domain.aggregate.valueobject.CallId;
import com.example.commondata.domain.aggregate.valueobject.Money;
import com.example.commondata.domain.aggregate.valueobject.PaymentId;
import com.example.commondata.domain.aggregate.valueobject.UserId;
import com.example.kafka.avro.model.Status;
import jakarta.persistence.Transient;
import lombok.*;
import lombok.experimental.SuperBuilder;

// https://www.baeldung.com/lombok-builder-inheritance
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends AggregateRoot<PaymentId> {
    private CallId callId;
    private UserId callerId;
    private UserId calleeId;
    private Money price;
    private String type;
    private String orderId;
    @Transient
    private String paymentKey;
    private Status status;
}