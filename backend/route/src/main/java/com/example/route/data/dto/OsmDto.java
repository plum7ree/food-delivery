package com.example.route.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OsmDto {
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