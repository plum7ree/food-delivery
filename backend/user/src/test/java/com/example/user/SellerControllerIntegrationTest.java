//package com.example.user;
//
//import com.example.user.controller.SellerController;
//import com.example.user.data.dto.MenuDto;
//import com.example.user.data.dto.RestaurantDto;
//import com.example.user.data.entity.Restaurant;
//import com.example.user.data.entity.Account;
//
//import com.example.user.data.repository.AccountRepository;
//import com.example.user.data.repository.RestaurantRepository;
//import com.netflix.discovery.converters.Auto;
//import kafka.server.metadata.UserEntity;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
//import org.springframework.cloud.netflix.eureka.config.DiscoveryClientOptionalArgsConfiguration;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalTime;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest(properties = "eureka.client.enabled=false")
//@Transactional
////@SpringBootApplication(exclude = {
////    DiscoveryClientOptionalArgsConfiguration.class,
////    EurekaClientAutoConfiguration.class
////})
//class SellerControllerIntegrationTest {
//
//    @Autowired
//    private SellerController sellerController;
//
//    @Autowired
//    private AccountRepository accountRepository;
//
//    @Autowired
//    private RestaurantRepository restaurantRepository;
//
//
//    @Test
//    void testRegisterRestaurant() {
//
//
//        String sessionId = sellerController.createSession().getBody();
//        // Arrange
//        RestaurantDto restaurantDto = new RestaurantDto();
//        restaurantDto.setSessionId(sessionId);
//        restaurantDto.setName("Burger King");
//        restaurantDto.setType("BURGER");
//        restaurantDto.setOpenTime(LocalTime.of(10, 0));
//        restaurantDto.setCloseTime(LocalTime.of(22, 0));
//
//        MenuDto menuDto = new MenuDto();
//        menuDto.setName("Classic Burger");
//        menuDto.setDescription("Delicious classic burger");
//        menuDto.setPictureUrl("burger_url1");
//        menuDto.setPrice("10000");
//
//        restaurantDto.setMenuDtoList(List.of(menuDto));
//
//        // Act
//        ResponseEntity<String> response = sellerController.registerRestaurant(restaurantDto);
//
//        // Assert
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//
//        Optional<Restaurant> savedRestaurant = restaurantRepository.findById(UUID.fromString(response.getBody()));
//        assertThat(savedRestaurant).isPresent();
//        assertThat(savedRestaurant.get().getName()).isEqualTo("Burger King");
//        assertThat(savedRestaurant.get().getMenuList()).hasSize(1);
//        assertThat(savedRestaurant.get().getMenuList().get(0).getName()).isEqualTo("Classic Burger");
//    }
//}