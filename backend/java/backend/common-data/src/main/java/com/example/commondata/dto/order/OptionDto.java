package com.example.commondata.dto.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OptionDto {
    @JsonIgnore
    @NotNull
    private UUID id;
    private String name;
    private Double cost;

}
