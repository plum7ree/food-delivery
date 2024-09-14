package com.example.eatsorderapplication.application.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverDetailsDto {
    String driverId;
    double lat;
    double lon;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DriverDetailsDto that = (DriverDetailsDto) o;
        return driverId.equals(that.driverId);
    }

    @Override
    public int hashCode() {
        return driverId.hashCode();
    }
}
