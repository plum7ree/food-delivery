package com.example.eatsorderapplication.data.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverDetailsDto {
    String driverId;
    double lat;
    double lon;

}
