package com.example.eatssearch.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RouteRequestDto {
    private Double startLat;
    private Double startLon;
    private Double destLat;
    private Double destLon;
    private AddressDto startAddress;
    private AddressDto destAddress;
}
