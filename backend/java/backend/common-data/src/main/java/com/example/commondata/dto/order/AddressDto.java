package com.example.commondata.dto.order;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AddressDto {
    @NotNull
    @JsonProperty
    private final String id;
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
