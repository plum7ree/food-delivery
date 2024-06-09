package com.example.user;


import com.example.user.data.dto.*;
import com.example.user.data.repository.RestaurantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"eureka.client.enabled=false"})
public class SellerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    public void testRegisterRestaurant() throws Exception {
        // Given
        // Create a session
        MvcResult createSessionResult = mockMvc.perform(post("/api/seller/create-session"))
                .andExpect(status().isOk())
                .andReturn();

        String sessionId = createSessionResult.getResponse().getContentAsString();


        List<MenuDto> menuDtoList = new ArrayList<>();

        // 첫 번째 메뉴 menu --< optiongroup --< option
        List<OptionDto> optionDtoList1 = Arrays.asList(
                OptionDto.builder().name("치즈 추가").cost(BigInteger.valueOf(500)).build(),
                OptionDto.builder().name("베이컨 추가").cost(BigInteger.valueOf(1000)).build()
        );
        List<OptionGroupDto> optionGroupDtoList1 = Arrays.asList(
                OptionGroupDto.builder().maxSelectNumber(10).isNecessary(false).optionDtoList(optionDtoList1).build()
        );
        MenuDto menuDto1 = MenuDto.builder()
                .name("햄버거 세트")
                .description("햄버거, 감자튀김, 음료가 포함된 세트 메뉴입니다.")
                .pictureUrl("https://example.com/hamburger_set.jpg")
                .optionGroupDtoList(optionGroupDtoList1)
                .build();

        // 두 번째 메뉴
        List<OptionDto> optionDtoList2 = Arrays.asList(
                OptionDto.builder().name("아메리카노").cost(BigInteger.valueOf(1500)).build(),
                OptionDto.builder().name("카페라떼").cost(BigInteger.valueOf(2000)).build()
        );
        List<OptionGroupDto> optionGroupDtoList2 = Arrays.asList(
                OptionGroupDto.builder().maxSelectNumber(10).isNecessary(true).optionDtoList(optionDtoList2).build()
        );
        MenuDto menuDto2 = MenuDto.builder()
                .name("음료 선택")
                .description("음료를 선택하세요.")
                .pictureUrl("https://example.com/drinks.jpg")
                .optionGroupDtoList(optionGroupDtoList2)
                .build();

        // 세 번째 메뉴
        MenuDto menuDto3 = MenuDto.builder()
                .name("감자튀김")
                .description("바삭한 감자튀김입니다.")
                .pictureUrl("https://example.com/french_fries.jpg")
                .optionGroupDtoList(new ArrayList<>())
                .build();

        menuDtoList.add(menuDto1);
        menuDtoList.add(menuDto2);
        menuDtoList.add(menuDto3);


        // Prepare restaurant data
        RestaurantDto restaurantDto = RestaurantDto.builder()
                .sessionId(sessionId)
                .name("Test Restaurant")
                .type(RestaurantTypeEnum.KOREAN)
                .openTime(LocalTime.of(10, 0))
                .closeTime(LocalTime.of(22, 0))
                .pictureUrl1("http://example.com/picture1.jpg")
                .pictureUrl2("http://example.com/picture2.jpg")
                .menuDtoList(menuDtoList)
                .build();

        // When
        // Register the restaurant
        mockMvc.perform(post("/api/seller/register/restaurant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restaurantDto)))
                .andExpect(status().isOk());

        // Then
        // Verify that the restaurant is saved in the repository
        List<RestaurantDto> restaurants = restaurantRepository.findAll();
        assertThat(restaurants).hasSize(1);

        var savedRestaurant = restaurants.get(0);
        assertThat(savedRestaurant.getName()).isEqualTo("Test Restaurant");
        assertThat(savedRestaurant.getType()).isEqualTo(RestaurantTypeEnum.KOREAN);
        assertThat(savedRestaurant.getOpenTime()).isEqualTo(LocalTime.of(10, 0));
        assertThat(savedRestaurant.getCloseTime()).isEqualTo(LocalTime.of(22, 0));
        assertThat(savedRestaurant.getPictureUrl1()).isEqualTo("http://example.com/picture1.jpg");
        assertThat(savedRestaurant.getPictureUrl2()).isEqualTo("http://example.com/picture2.jpg");
    }
}