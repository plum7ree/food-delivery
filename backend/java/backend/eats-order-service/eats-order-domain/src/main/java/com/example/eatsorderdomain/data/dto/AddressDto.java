package com.example.eatsorderdomain.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    String id;
    String name;
    String userId;
    String osmId;
    PointDto point;
    String street;
    String city;
    String postalCode;
    Double lat;
    Double lon;
}