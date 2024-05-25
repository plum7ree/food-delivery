package com.example.user;

import com.example.user.data.dto.*;
import com.example.user.data.repository.MenuRepository;
import com.example.user.data.repository.RestaurantRepository;
import com.example.user.service.RestaurantService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(
//    connection = EmbeddedDatabaseConnection.H2,
        replace = AutoConfigureTestDatabase.Replace.NONE // application.yml 것 사용하려면 필수다.
)
@DirtiesContext
@Slf4j
class RestaurantServiceDatabaseTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private MenuRepository menuRepository;

    @Test
    void saveRestaurantTest() {
        // 테스트용 데이터베이스에 저장할 데이터 생성

        RestaurantDto restaurantDto = new RestaurantDto();
        restaurantDto.setUserId("13f0c8f0-eec0-4169-ad9f-e8bb408a5325");
        restaurantDto.setName("Burger King");
        restaurantDto.setType(RestaurantTypeEnum.BURGER);
        restaurantDto.setOpenTime(LocalTime.of(10, 0));
        restaurantDto.setCloseTime(LocalTime.of(22, 0));

        List<MenuDto> menuDtoList = new ArrayList<>();
        MenuDto menuDto1 = new MenuDto();
        menuDto1.setName("Pasta");
        menuDto1.setDescription("Spaghetti with tomato sauce");
        menuDto1.setPictureUrl("https://example.com/pasta.jpg");
        menuDto1.setPrice(new BigInteger("10"));

        AtomicReference<Integer> count = new AtomicReference<>(0);
        List<OptionGroupDto> optionGroupDtoList1 = new ArrayList<>();

        OptionGroupDto optionGroupDto1 = new OptionGroupDto();
        optionGroupDto1.setDescription("optionGroup" + count);
        optionGroupDto1.setNecessary(true);
        optionGroupDto1.setMaxSelectNumber(count.get());
        count.getAndSet(count.get() + 1);

        List<OptionDto> optionDtoList1 = new ArrayList<>();
        OptionDto optionDto1 = new OptionDto();
        optionDto1.setName("optionDto" + count.get());
        optionDto1.setCost(BigInteger.valueOf((count.get() * 100)));
        count.getAndSet(count.get() + 1);

        // option -> optionList
        optionDtoList1.add(optionDto1);
        // optionList -> optionGroup
        optionGroupDto1.setOptionDtoList(optionDtoList1);
        // optionGroup -> optionGroupList
        optionGroupDtoList1.add(optionGroupDto1);
        // optionGroupList -> menu
        menuDto1.setOptionGroupDtoList(optionGroupDtoList1);
        // menu -> menuList
        menuDtoList.add(menuDto1);


        MenuDto menuDto2 = new MenuDto();
        menuDto2.setName("Pizza");
        menuDto2.setDescription("Margherita Pizza");
        menuDto2.setPictureUrl("https://example.com/pizza.jpg");
        menuDto2.setPrice(new BigInteger("12"));

        menuDtoList.add(menuDto2);

        restaurantDto.setMenuDtoList(menuDtoList);

        // 테스트용 데이터베이스에 데이터 저장
        String savedRestaurantId = restaurantService.saveRestaurantAndAllChildren(restaurantDto);

        // make sure it is saved into real db.

        log.info("savedRestauratId: {}", savedRestaurantId);
        // 테스트용 데이터베이스에서 저장된 데이터 가져오기
        RestaurantDto savedRestaurant = restaurantRepository.findById(UUID.fromString(savedRestaurantId))
                // Option<T> 를 null 혹은 T 로 리턴!
                .orElse(null);
        var savedMenuDtoList = restaurantService.findMenuAndAllChildrenByRestaurantId(savedRestaurant.getId().toString())
                .orElse(null);

        // 저장된 레스토랑이 null이 아닌지 확인
        assert savedRestaurant != null;
        assert savedMenuDtoList != null;
        assertEquals(savedRestaurant.getName(), "Burger King");
        assertEquals(savedRestaurant.getType(), RestaurantTypeEnum.BURGER);
        assertEquals(savedRestaurant.getOpenTime(), LocalTime.of(10, 0));
        assertEquals(savedRestaurant.getCloseTime(), LocalTime.of(22, 0));
        assertEquals(savedMenuDtoList.size(), 2);
        savedMenuDtoList.stream().forEach(savedMenuDto -> {
            assert savedMenuDto.getRestaurantId() != null;
        });

        savedMenuDtoList = menuRepository.findByRestaurantIdNoDtoProjection(savedRestaurantId).orElse(null);
        assert savedMenuDtoList != null;
        savedMenuDtoList.stream().forEach(savedMenu -> {
            log.info("restaurant Id {}", savedMenu.getRestaurantId());

        });
    }
}