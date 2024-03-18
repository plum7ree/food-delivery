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
    Float lat;
    Float lon;
    String edgeId; //TODO change to osmId or add nodeId/POI?
    String oldEdgeId;
    String driverId;
}
