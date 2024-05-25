package com.example.calldomain.data.dto;

import com.example.kafka.avro.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class PaymentResponseDto {
    private String id;
    private String sagaId;
    private String callId;
    private String paymentId;
    private String driverId;
    private BigDecimal price;
    private Instant createdAt;
    private PaymentStatus paymentStatus;
    private String failureMessages;
}
