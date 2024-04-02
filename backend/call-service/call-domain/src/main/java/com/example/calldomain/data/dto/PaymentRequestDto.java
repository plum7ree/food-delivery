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
public class PaymentRequestDto {
    private String type;
    private String orderId;
    private String amount;
    private String paymentKey;
}
