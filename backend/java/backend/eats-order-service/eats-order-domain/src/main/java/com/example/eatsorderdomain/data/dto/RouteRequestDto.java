package com.example.eatsorderdomain.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RouteRequestDto {
    private Double startLat;
    private Double startLon;
    private Double destLat;
    private Double destLon;
    private AddressDto startAddress;
    private AddressDto destAddress;
}
