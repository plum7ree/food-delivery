package com.example.user.controller;

import com.example.user.data.dto.RestaurantDto;
import com.example.user.data.entity.Comment;
import com.example.user.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/restaurant")
@RequiredArgsConstructor
@Slf4j
public class RestaurantController {
    private final RestaurantService restaurantService;

    @GetMapping("/{restaurantId}")
    @Transactional
    public ResponseEntity<RestaurantDto> getRestaurantAndAllChildren(@PathVariable("restaurantId") String restaurantId) {
        Objects.requireNonNull(restaurantService);
        log.info("/restaurant/{restaurantId} id: {}", restaurantId);
        RestaurantDto restaurantDto = null;
        try {
            restaurantDto = restaurantService.findRestaurantById(restaurantId);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
        log.info("getRestaurant restairamtEntity: {} ", restaurantDto.toString());
        //        restaurantDto = imageService.createPresignedUrlForRestaurant(restaurantDto);

        var menuDtoList = restaurantService.findMenuAndAllChildrenByRestaurantId(restaurantId).orElse(null);
        restaurantDto.setMenuDtoList(menuDtoList);

        assert menuDtoList != null;
        log.info("getRestaurant: restaurantDto : {}", restaurantDto.toString());


        return ResponseEntity.ok(restaurantDto);
    }

}
