package com.example.user.controller;

import com.example.user.data.dto.RestaurantDto;
import com.example.user.data.dto.RestaurantTypeEnum;
import com.example.user.data.entity.Comment;
import com.example.user.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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


    @GetMapping("/restaurants")
    public ResponseEntity<Page<RestaurantDto>> getRestaurantsByType(
        @RequestParam(value = "type") String type,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            var restaurantListPage = restaurantService.findByType(RestaurantTypeEnum.valueOf(type), pageable);
            return ResponseEntity.ok().body(restaurantListPage);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
