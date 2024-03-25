package com.example.calldomain.data.dto;

import com.example.kafka.avro.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class DriverApprovalResponseDto {
    private String id;
    private String sagaId;
    private String orderId;
    private String driverId;
    private Instant createdAt;
    private PaymentStatus driverApprovalStatus;
    private List<String> failureMessages;
}
