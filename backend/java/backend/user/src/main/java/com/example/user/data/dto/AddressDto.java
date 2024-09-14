package com.example.user.data.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class AddressDto {
    @NotNull
    @JsonProperty
    private final String id;
    @NotNull
    @JsonProperty
    private String userId;
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
    @NotNull
    @JsonProperty
    private Double lat;
    @NotNull
    @JsonProperty
    private Double lon;

}
