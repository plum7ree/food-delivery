package com.example.user;

import com.example.user.data.dto.RestaurantTypeEnum;
import com.example.user.service.RestaurantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.math.BigInteger;
import java.util.*;

import com.example.user.data.repository.RestaurantRepository;
import com.example.user.data.dto.MenuDto;
import com.example.user.data.dto.RestaurantDto;
import com.example.user.data.entity.Restaurant;
import com.example.user.data.repository.RestaurantRepository;
import com.example.user.service.RestaurantService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class RestaurantServiceIntegrationTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantService restaurantService;

    @Test
    void saveRestaurantTest() {
        // 테스트용 데이터베이스에 저장할 데이터 생성
        RestaurantDto restaurantDto = new RestaurantDto();
        restaurantDto.setName("Burger King");
        restaurantDto.setType("BURGER");
        restaurantDto.setOpenTime(LocalTime.of(10, 0));
        restaurantDto.setCloseTime(LocalTime.of(22, 0));

        List<MenuDto> menuDtoList = new ArrayList<>();
        MenuDto menuDto1 = new MenuDto();
        menuDto1.setName("Pasta");
        menuDto1.setDescription("Spaghetti with tomato sauce");
        menuDto1.setPictureUrl("https://example.com/pasta.jpg");
        menuDto1.setPrice("10");

        MenuDto menuDto2 = new MenuDto();
        menuDto2.setName("Pizza");
        menuDto2.setDescription("Margherita Pizza");
        menuDto2.setPictureUrl("https://example.com/pizza.jpg");
        menuDto2.setPrice("12");

        menuDtoList.add(menuDto1);
        menuDtoList.add(menuDto2);

        restaurantDto.setMenuDtoList(menuDtoList);

        // 테스트용 데이터베이스에 데이터 저장
        String savedRestaurantId = restaurantService.save(restaurantDto);

        // 테스트용 데이터베이스에서 저장된 데이터 가져오기
        Restaurant savedRestaurant = restaurantRepository.findById(UUID.fromString(savedRestaurantId)).orElse(null);

        // 저장된 레스토랑이 null이 아닌지 확인
        assert savedRestaurant != null;
        assertEquals(savedRestaurant.getName(), "Burger King");
        assertEquals(savedRestaurant.getType(), RestaurantTypeEnum.BURGER);
        assertEquals(savedRestaurant.getOpenTime(), LocalTime.of(10, 0));
        assertEquals(savedRestaurant.getCloseTime(), LocalTime.of(22, 0));
        assertEquals(savedRestaurant.getMenuList().size(), 2);
    }
}