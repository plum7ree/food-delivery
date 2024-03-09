package com.example.driver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    float lat;
    float lon;
    String edgeId;
    String oldEdgeId;
    String driverId;
}
