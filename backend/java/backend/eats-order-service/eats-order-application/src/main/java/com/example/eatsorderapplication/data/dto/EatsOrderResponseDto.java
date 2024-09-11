package com.example.eatsorderapplication.data.dto;

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
    private final String message;

}