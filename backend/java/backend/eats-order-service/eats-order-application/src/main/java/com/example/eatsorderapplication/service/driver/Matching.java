package com.example.eatsorderapplication.service.driver;

import com.example.eatsorderapplication.data.dto.DriverDetailsDto;
import com.example.eatsorderdomain.data.dto.AddressDto;
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
