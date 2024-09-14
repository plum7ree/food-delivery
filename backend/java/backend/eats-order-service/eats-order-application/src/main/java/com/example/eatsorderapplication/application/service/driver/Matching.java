package com.example.eatsorderapplication.application.service.driver;

import com.example.commondata.dto.order.AddressDto;
import com.example.commondata.dto.order.UserAddressDto;
import com.example.eatsorderapplication.application.dto.DriverDetailsDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class Matching {
    UUID orderId;
    @NonNull
    UserAddressDto address;
    @NonNull
    DriverDetailsDto driver;
}
