package com.example.route.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressSearchRequestDto {
    AddressDto start;
    AddressDto dest;
    AddressDto curr;
}
