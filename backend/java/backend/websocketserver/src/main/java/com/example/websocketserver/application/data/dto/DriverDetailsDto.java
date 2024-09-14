package com.example.websocketserver.application.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverDetailsDto {
    String driverId;
    double lat;
    double lon;

}
