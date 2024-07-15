package com.example.user.service;


import com.example.user.data.dto.MenuDto;
import com.example.user.data.dto.OptionDto;
import com.example.user.data.dto.OptionGroupDto;
import com.example.user.data.dto.RestaurantDto;
import com.example.user.data.entity.*;
import com.example.user.data.repository.MenuRepository;
import com.example.user.data.repository.OptionGroupRepository;
import com.example.user.data.repository.OptionRepository;
import com.example.user.data.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RestaurantService {


    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    MenuRepository menuRepository;

    @Autowired
    OptionGroupRepository optionGroupRepository;

    @Autowired
    OptionRepository optionRepository;

    public RestaurantDto findRestaurantById(String restaurantId) {
        return restaurantRepository.findById(UUID.fromString(restaurantId))
                .orElseThrow(() -> new IllegalArgumentException("no repository by id"));
    }

    @Transactional
    public String saveRestaurantAndAllChildren(RestaurantDto restaurantDto) {


        // keyname 은 보안상 중요하므로, 서버에서 관리해야함. 유저에게 실제 keyname 이 노출되어서는 안됨.
        // presigned url 로 항상 변환해서 리턴.
        var menuDtoList = restaurantDto.getMenuDtoList();

        var restaurantEntity = Restaurant.builder()
                .account(Account.builder().id(UUID.fromString(restaurantDto.getUserId())).build())
                .name(restaurantDto.getName())
                .type(restaurantDto.getType())
                .openTime(restaurantDto.getOpenTime())
                .closeTime(restaurantDto.getCloseTime())
//                .pictureUrl1("picture")
                .build();
        List<Menu> menuEntityList = new ArrayList<>();
        List<OptionGroup> optionGroupEntityList = new ArrayList<>();
        List<Option> optionEntityList = new ArrayList<>();


        if (menuDtoList != null) {
            menuEntityList.addAll(menuDtoList.stream().map(menuDto -> {
                var menuEntity = Menu.builder()
                        .name(menuDto.getName())
                        .pictureUrl(menuDto.getPictureUrl())
                        .price(menuDto.getPrice())
                        .restaurant(restaurantEntity)
                        .build();

                if (menuDto.getOptionGroupDtoList() != null) {

                    optionGroupEntityList.addAll(menuDto.getOptionGroupDtoList().stream().map(optionGroupDto -> {
                        var optionGroupEntity = OptionGroup.builder()
                                .description(optionGroupDto.getDescription())
                                .maxSelectNumber(optionGroupDto.getMaxSelectNumber())
                                .isNecessary(optionGroupDto.isNecessary())
                                .menu(menuEntity)
                                .build();

                        if (optionGroupDto.getOptionDtoList() != null) {
                            optionEntityList.addAll(optionGroupDto.getOptionDtoList().stream()
                                    .map(optionDto -> Option.builder()
                                            .name(optionDto.getName())
                                            .cost(optionDto.getCost())
                                            .optionGroup(optionGroupEntity)
                                            .build())
                                    .collect(Collectors.toList()));

                        }
                        return optionGroupEntity;
                    }).collect(Collectors.toList()));
                }

                return menuEntity;
            }).collect(Collectors.toList()));
        }

        log.info("registerRestaurant save: {}  ", restaurantEntity.getName());
        Restaurant restaurant;
        String id;
        try {
            restaurant = restaurantRepository.save(restaurantEntity);
            menuRepository.saveAll(menuEntityList);
            optionGroupRepository.saveAll(optionGroupEntityList);
            optionRepository.saveAll(optionEntityList);
            log.info(restaurant.toString());
            id = restaurant.getId().toString();
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }

        return id;
    }
    @Transactional
    public Optional<List<MenuDto>> findMenuByRestaurantId(String restaurantId) {
        return menuRepository.findByRestaurantId(restaurantId);
    }

    @Transactional
    public Optional<List<MenuDto>> findMenuAndAllChildrenByRestaurantId(String restaurantId) {
        List<MenuDto> menuDtoList = menuRepository.findByRestaurantId(restaurantId).get();

        List<MenuDto> updatedMenuDtoList = menuDtoList.stream()
                .map(menuDto -> {
                    List<OptionGroupDto> optionGroupDtoList = optionGroupRepository.findByMenuId(menuDto.getId()).get();
                    menuDto.setOptionGroupDtoList(optionGroupDtoList);

                    optionGroupDtoList.forEach(optionGroupDto -> {
                        List<OptionDto> optionDtoList = optionRepository.findByOptionGroupId(optionGroupDto.getId()).get();
                        optionGroupDto.setOptionDtoList(optionDtoList);
                    });
                    return menuDto;
                })
                .collect(Collectors.toList());

        return Optional.of(updatedMenuDtoList);
    }

    public MenuDto saveMenu(MenuDto menu) {
        return menu;
    }

}
