package com.example.eatsorderapplication.data.dto;

import com.example.commondata.domain.aggregate.valueobject.CallStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class EatsOrderResponseDto {
    @NotNull
    private final UUID callTrackingId;
    @NotNull
    private final CallStatus callStatus;
    @NotNull
    private final String message;

}