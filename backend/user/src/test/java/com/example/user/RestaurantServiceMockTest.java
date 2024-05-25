package com.example.user;

import com.example.user.data.dto.MenuDto;
import com.example.user.data.dto.RestaurantDto;
import com.example.user.data.dto.RestaurantTypeEnum;
import com.example.user.data.entity.Restaurant;
import com.example.user.data.repository.RestaurantRepository;
import com.example.user.service.RestaurantService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RestaurantServiceMockTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    public RestaurantServiceMockTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void saveRestaurantTest() {
        // 가짜 데이터 생성
        RestaurantDto restaurantDto = new RestaurantDto();
        restaurantDto.setName("Burger King");
        restaurantDto.setType(RestaurantTypeEnum.BURGER);
        restaurantDto.setOpenTime(LocalTime.of(10, 0));
        restaurantDto.setCloseTime(LocalTime.of(22, 0));

        // 메뉴 데이터 생성
        List<MenuDto> menuDtoList = new ArrayList<>();
        MenuDto menuDto1 = new MenuDto();
        menuDto1.setName("Pasta");
        menuDto1.setDescription("Spaghetti with tomato sauce");
        menuDto1.setPictureUrl("https://example.com/pasta.jpg");
        menuDto1.setPrice(new BigInteger("10"));

        MenuDto menuDto2 = new MenuDto();
        menuDto2.setName("Pizza");
        menuDto2.setDescription("Margherita Pizza");
        menuDto2.setPictureUrl("https://example.com/pizza.jpg");
        menuDto2.setPrice(new BigInteger("12"));

        menuDtoList.add(menuDto1);
        menuDtoList.add(menuDto2);

        restaurantDto.setMenuDtoList(menuDtoList);

        // 가짜 Repository 행동 설정
        when(restaurantRepository.save(any())).thenReturn(new Restaurant());

        // 테스트할 메소드 호출
        String savedRestaurantId = restaurantService.saveRestaurantAndAllChildren(restaurantDto);

        // 저장된 레스토랑 ID가 null이 아닌지 확인
        assertEquals(savedRestaurantId, "0");
    }
}
