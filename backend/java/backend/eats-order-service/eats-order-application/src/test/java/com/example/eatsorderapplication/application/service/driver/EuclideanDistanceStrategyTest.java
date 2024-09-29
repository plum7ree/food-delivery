package com.example.eatsorderapplication.application.service.driver;


import com.example.commondata.dto.order.AddressDto;
import com.example.commondata.dto.order.UserOrderAddressDto;
import com.example.eatsorderapplication.application.dto.DriverDetailsDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.redisson.api.RedissonReactiveClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


class EuclideanDistanceStrategyTest {

    @Mock
    private RedissonReactiveClient redissonClient;

    @Mock
    private ObjectMapper objectMapper;

    private com.example.eatsorderapplication.service.driver.EuclideanDistanceStrategy strategy;

    @BeforeEach
    void init() {
        strategy = new com.example.eatsorderapplication.service.driver.EuclideanDistanceStrategy();
    }

    @Test
    void testMatch() {
        // Given
        DriverDetailsDto driver1 = new DriverDetailsDto("driver1", 37.0001, 128.0001);
        DriverDetailsDto driver2 = new DriverDetailsDto("driver2", 37.0002, 128.0002);
        Set<DriverDetailsDto> drivers = new HashSet<>(List.of(driver1, driver2));

        UserOrderAddressDto user1 = new UserOrderAddressDto(
            "orderId1",
            "userId1",
            AddressDto.builder()
                .id("addressId1")
                .lat(37.0000)
                .lon(128.0000)
                .street("street1")
                .city("city1")
                .postalCode("12345")
                .build()
        );
        Set<UserOrderAddressDto> users = new HashSet<>(List.of(user1));

        // When
        Mono<List<Matching>> result = strategy.match(users, drivers);

        // Then
        StepVerifier.create(result)
            .expectNextMatches(matchings -> {
                if (matchings.size() != 1) return false;

                // Check if each user is matched with the closest driver
                boolean correctMatching1 = matchings.stream()
                    .anyMatch(m -> m.getDriver().getDriverId().equals("driver1") &&
                        m.getUserOrderAddress().userId().equals("userId1"));

                return correctMatching1;
            })
            .verifyComplete();
    }

    @Test
    void testMatch2() {
        // Given
        DriverDetailsDto driver1 = new DriverDetailsDto("driver1", 37.0001, 128.0001);
        DriverDetailsDto driver2 = new DriverDetailsDto("driver2", 37.0005, 128.0005);
        DriverDetailsDto driver3 = new DriverDetailsDto("driver3", 37.0010, 128.0010);
        Set<DriverDetailsDto> drivers = new HashSet<>(List.of(driver1, driver2, driver3));

        UserOrderAddressDto user1 = new UserOrderAddressDto(
            "orderId1",
            "userId1",
            AddressDto.builder()
                .id("addressId1")
                .lat(37.0000)
                .lon(128.0000)
                .street("street1")
                .city("city1")
                .postalCode("12345")
                .build()
        );
        UserOrderAddressDto user2 = new UserOrderAddressDto(
            "orderId2",
            "userId2",
            AddressDto.builder()
                .id("addressId2")
                .lat(37.0004)
                .lon(128.0004)
                .street("street2")
                .city("city2")
                .postalCode("67890")
                .build()
        );
        UserOrderAddressDto user3 = new UserOrderAddressDto(
            "orderId3",
            "userId3",
            AddressDto.builder()
                .id("addressId3")
                .lon(128.0009)
                .lat(37.0009)
                .street("street3")
                .city("city3")
                .postalCode("67890")
                .build()
        );
        Set<UserOrderAddressDto> users = new HashSet<>(List.of(user1, user2, user3));

        // When
        Mono<List<Matching>> result = strategy.match(users, drivers);

        // Then
        StepVerifier.create(result)
            .expectNextMatches(matchings -> {
                if (matchings.size() != 3) return false;

                // Check if each user is matched with the closest driver
                boolean correctMatching1 = matchings.stream()
                    .anyMatch(m -> m.getDriver().getDriverId().equals("driver1") &&
                        m.getUserOrderAddress().userId().equals("userId1"));
                boolean correctMatching2 = matchings.stream()
                    .anyMatch(m -> m.getDriver().getDriverId().equals("driver2") &&
                        m.getUserOrderAddress().userId().equals("userId2"));
                boolean correctMatching3 = matchings.stream()
                    .anyMatch(m -> m.getDriver().getDriverId().equals("driver3") &&
                        m.getUserOrderAddress().userId().equals("userId3"));
                return correctMatching1 && correctMatching2 && correctMatching3;
            })
            .verifyComplete();
    }

}