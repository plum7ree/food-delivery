package com.example.eatsorderdomain.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto {
    private String id;
    private String sagaId;
    private String callId;
    private String paymentId;
    private String driverId;
    private BigDecimal price;
    private Instant createdAt;
    private OrderStatus status;
    private String failureMessages;

}
