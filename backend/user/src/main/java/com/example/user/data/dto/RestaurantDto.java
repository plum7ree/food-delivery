package com.example.user.data.dto;

import lombok.*;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDto {
    UUID id;
    String sessionId;
    String userId;
    String name;
    RestaurantTypeEnum type;

    LocalTime openTime;
    LocalTime closeTime;

    private String pictureUrl1;
    private String pictureUrl2;

    private List<MenuDto> menuDtoList;


}
