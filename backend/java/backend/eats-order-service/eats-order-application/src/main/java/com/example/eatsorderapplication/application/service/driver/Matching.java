package com.example.eatsorderapplication.application.service.driver;

import com.example.commondata.dto.order.AddressDto;
import com.example.eatsorderapplication.application.dto.DriverDetailsDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
public class Matching {
    @NonNull
    AddressDto address;
    @NonNull
    DriverDetailsDto driver;
}
