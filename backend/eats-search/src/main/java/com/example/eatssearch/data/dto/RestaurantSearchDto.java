package com.example.route.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantSearchDto {
    private String housenumber;
    private String street;
    private String city;
    private String postalCode;
    private String name;
    private String buildingType;
    private float lat;
    private float lon;
    private long osmid;

}