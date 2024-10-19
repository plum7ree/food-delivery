package com.example.eatsorderapplication.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverDetailsWithUserAddressDto {
    String driverId;
    double lat;
    double lon;

    String userAddressId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DriverDetailsWithUserAddressDto that = (DriverDetailsWithUserAddressDto) o;
        return driverId.equals(that.driverId);
    }

    @Override
    public int hashCode() {
        return driverId.hashCode();
    }
}
