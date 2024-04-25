package com.example.user.data.dto;

import com.example.user.data.entity.Menu;
import com.example.user.data.entity.Option;
import com.example.user.data.entity.OptionGroup;
import com.example.user.data.entity.Restaurant;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

public class Converter {
    public static RestaurantDto convertRestaurantEntityToDtoWithoutMenu(Restaurant restaurant) {
        return RestaurantDto.builder().id(restaurant.getId().toString())
                .name(restaurant.getName())
                .pictureUrl1(restaurant.getPictureUrl1())
                .pictureUrl2(restaurant.getPictureUrl2())
                .openTime(restaurant.getOpenTime())
                .closeTime(restaurant.getCloseTime())
                .type(restaurant.getType().toString()).build();
    }

}
