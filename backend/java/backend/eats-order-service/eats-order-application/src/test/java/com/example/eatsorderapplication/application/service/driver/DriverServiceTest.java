package com.example.eatsorderapplication.application.service.driver;

import com.example.commondata.dto.order.AddressDto;
import com.example.commondata.dto.order.UserOrderAddressDto;
import com.example.eatsorderapplication.application.dto.DriverDetailsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.redisson.api.RGeoReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.eatsorderapplication.application.service.driver.DriverService.DRIVER_GEO_KEY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class DriverServiceTest {

    @Mock
    private RedissonReactiveClient redissonReactiveClient;

    @Mock
    private RGeoReactive<DriverDetailsDto> geoReactive;

    @InjectMocks
    private DriverService driverService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redissonReactiveClient.getGeo(eq(DRIVER_GEO_KEY), any(TypedJsonJacksonCodec.class)))
            .thenReturn((RGeoReactive) geoReactive); // casting 안하면 에러뜸. getGeo 가 RGeoReactive<Object> 를 리턴해서.
    }

    @Test
    void testGetNearbyDriversFromUsers_Success() {
        // Given
        UserOrderAddressDto userAddress1 = new UserOrderAddressDto(
            "orderId1",
            "userId1",
            AddressDto.builder()
                .id("addressId1")
                .lon(128.0000d)
                .lat(37.0000d)
                .street("street")
                .city("city")
                .postalCode("postalCode")
                .build()
        );
        DriverDetailsDto driver1 = new DriverDetailsDto("driver1", 128.0001, 37.0000);
        DriverDetailsDto driver2 = new DriverDetailsDto("driver2", 128.0002, 37.0000);

        when(geoReactive.search(any())).thenReturn(Mono.just(List.of(driver1, driver2)));

        // When
        Mono<Tuple3<Set<DriverDetailsDto>, Set<UserOrderAddressDto>, Set<UserOrderAddressDto>>> result =
            driverService.getNearbyDriversFromUsers(List.of(userAddress1));

        // Then
        StepVerifier.create(result)
            .expectNextMatches(tuple -> {
                Set<DriverDetailsDto> drivers = tuple.getT1();
                Set<UserOrderAddressDto> successLocations = tuple.getT2();
                Set<UserOrderAddressDto> failedLocations = tuple.getT3();

                return drivers.contains(driver1) &&
                    drivers.contains(driver2) &&
                    successLocations.contains(userAddress1) &&
                    failedLocations.isEmpty();
            })
            .verifyComplete();
    }


    @Test
    void testGetNearbyDriversFromUsers_Failure() {
        // Given
        UserOrderAddressDto userAddress1 = new UserOrderAddressDto(
            "orderId1",
            "userId1",
            AddressDto.builder()
                .id("addressId1")
                .lon(128.0000d)
                .lat(37.0000d)
                .street("street")
                .city("city")
                .postalCode("postalCode")
                .build()
        );
        DriverDetailsDto driver1 = new DriverDetailsDto("driver1", 128.0001, 37.0000);
        DriverDetailsDto driver2 = new DriverDetailsDto("driver2", 128.0002, 37.0000);

        when(geoReactive.search(any())).thenReturn(Mono.error(new RuntimeException("Search failed")));

        // When
        Mono<Tuple3<Set<DriverDetailsDto>, Set<UserOrderAddressDto>, Set<UserOrderAddressDto>>> result =
            driverService.getNearbyDriversFromUsers(List.of(userAddress1));

        // Then
        StepVerifier.create(result)
            .expectNextMatches(tuple -> {
                Set<DriverDetailsDto> drivers = tuple.getT1();
                Set<UserOrderAddressDto> successLocations = tuple.getT2();
                Set<UserOrderAddressDto> failedLocations = tuple.getT3();

                return drivers.isEmpty() &&
                    successLocations.isEmpty() &&
                    failedLocations.contains(userAddress1);
            })
            .verifyComplete();
    }


    @Test
    void testPerformMatching() {
        // Given
        UserOrderAddressDto userAddress1 = new UserOrderAddressDto(
            "orderId1",
            "userId1",
            AddressDto.builder()
                .id("addressId1")
                .lon(128.0000d)
                .lat(37.0000d)
                .street("street")
                .city("city")
                .postalCode("postalCode")
                .build()
        );
        Set<UserOrderAddressDto> userAddresses = new HashSet<>();
        userAddresses.add(userAddress1);

        DriverDetailsDto driver1 = new DriverDetailsDto("driver1", 128.0001, 37.0000);
        DriverDetailsDto driver2 = new DriverDetailsDto("driver2", 128.0002, 37.0000);

        Set<DriverDetailsDto> drivers = new HashSet<>();
        drivers.add(driver1);
        drivers.add(driver2);


        // When
        Mono<List<Matching>> result = driverService.performMatching(Tuples.of(drivers, userAddresses));

        // Then
        StepVerifier.create(result)
            .expectNextMatches(matchingList -> matchingList.isEmpty()) // 현재 매칭 로직이 없으므로 빈 리스트 반환
            .verifyComplete();
    }
}
