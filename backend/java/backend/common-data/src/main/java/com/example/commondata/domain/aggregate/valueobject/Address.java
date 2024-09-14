package com.example.commondata.domain.aggregate.valueobject;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class Address {
    @NotNull
    @JsonProperty
    private final UUID id;
    @NotNull
    @Max(value = 50)
    @JsonProperty
    private final String street;
    @NotNull
    @Max(value = 10)
    @JsonProperty
    private final String postalCode;
    @NotNull
    @Max(value = 50)
    @JsonProperty
    private final String city;
    private final Double lat;
    private final Double lon;

}
