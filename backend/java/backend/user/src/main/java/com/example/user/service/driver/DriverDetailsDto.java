package com.example.user.service.driver;

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
