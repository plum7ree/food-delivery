package com.example.user.data.dto;

import com.example.user.data.entity.Restaurant;

public class Converter {
    public static RestaurantDto convertRestaurantEntityToDtoWithoutMenu(Restaurant restaurant) {
        return RestaurantDto.builder().id(restaurant.getId())
                .name(restaurant.getName())
                .pictureUrl1(restaurant.getPictureUrl1())
                .pictureUrl2(restaurant.getPictureUrl2())
                .openTime(restaurant.getOpenTime())
                .closeTime(restaurant.getCloseTime())
                .type(restaurant.getType()).build();
    }

}
