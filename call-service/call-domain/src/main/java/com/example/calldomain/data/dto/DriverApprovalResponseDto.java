package com.example.calldomain.data.dto;

import com.example.kafka.avro.model.DriverApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class DriverApprovalResponseDto {
    private String id;
    private String sagaId;
    private String callId;
    private String driverId;
    private Instant createdAt;
    private DriverApprovalStatus driverApprovalStatus;
    private String failureMessages;
}
