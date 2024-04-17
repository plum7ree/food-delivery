package com.example.user.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDto {
    String id;
    String ownerId;
    String name;
    String type;

    LocalTime openTime;
    LocalTime closeTime;


}
