package com.example.user.service;


import com.example.user.data.dto.RestaurantDto;
import com.example.user.data.dto.RestaurantTypeEnum;
import com.example.user.data.entity.Menu;
import com.example.user.data.entity.Restaurant;
import com.example.user.data.repository.RestaurantRepository;
import com.netflix.discovery.converters.Auto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RestaurantService {


    @Autowired
    RestaurantRepository restaurantRepository;

    @Transactional
    public String save(RestaurantDto restaurantDto) {


        // keyname 은 보안상 중요하므로, 서버에서 관리해야함. 유저에게 실제 keyname 이 노출되어서는 안됨.
        // presigned url 로 항상 변환해서 리턴.
        var menuDtoList = restaurantDto.getMenuDtoList();

        var restaurantEntity = Restaurant.builder()
                .name(restaurantDto.getName())
                .type(RestaurantTypeEnum.valueOf(restaurantDto.getType()))
                .openTime(restaurantDto.getOpenTime())
                .closeTime(restaurantDto.getCloseTime())
//                .pictureUrl1("picture")
                .build();

        if (menuDtoList != null) {
            // Menu 엔티티 생성 및 Restaurant 엔티티에 추가
            List<Menu> menuList = menuDtoList.stream()
                    .map(menuDto -> Menu.builder()
                            .name(menuDto.getName())
                            .description(menuDto.getDescription())
                            .pictureUrl(menuDto.getPictureUrl())
                            .price(BigInteger.valueOf(Long.parseLong(menuDto.getPrice())))
                            .restaurant(restaurantEntity)
//                            .optionGroupList(menuDto.getOptionGroupDtoList().stream()
//                                    .map(optionGroupDto -> OptionGroup.builder()
//                                            .description(optionGroupDto.getDescription())
//                                            .maxSelectNumber(optionGroupDto.getMaxSelectNumber())
//                                            .isNecessary(optionGroupDto.isNecessary())
//                                            //TODO set menu
//                                            .options(optionGroupDto.getOptionDtoList().stream()
//                                                    .map(optionDto -> Option.builder()
//                                                            .name(optionDto.getName())
//                                                            .cost(BigInteger.valueOf(Long.parseLong(optionDto.getCost())))
//                                                            .build())
//                                                    .collect(Collectors.toList()))
//                                            .build())
//                                    .collect(Collectors.toList()))
                            .build())
                    .collect(Collectors.toList());

            restaurantEntity.setMenuList(menuList);
        }
        log.info("registerRestaurant save: {} menuSize: {} ", restaurantEntity.getName(), restaurantEntity.getMenuList().size());
        Restaurant restaurant;
        String id;
        try {
            restaurant = restaurantRepository.save(restaurantEntity);
            log.info(restaurant.toString());
            id = restaurant.getId().toString();
        } catch (Exception e) {
            return null;
        }

        return id;
    }
}
