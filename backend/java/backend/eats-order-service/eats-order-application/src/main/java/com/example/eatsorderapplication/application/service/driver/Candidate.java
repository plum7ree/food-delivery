package com.example.eatsorderapplication.application.service.driver;

import com.example.commondata.dto.order.UserOrderAddressDto;
import com.example.eatsorderapplication.application.dto.DriverDetailsDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
public class Candidate {
    @NonNull
    UserOrderAddressDto userOrderAddress;
    @NonNull
    DriverDetailsDto driver;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Candidate that = (Candidate) o;
        return userOrderAddress.userId().equals(that.getUserOrderAddress().userId()) &&
            userOrderAddress.orderId().equals(that.getUserOrderAddress().orderId()) &&
            driver.getDriverId().equals(that.getDriver().getDriverId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            userOrderAddress.userId(),
            userOrderAddress.orderId(),
            driver.getDriverId()
        );
    }
}
