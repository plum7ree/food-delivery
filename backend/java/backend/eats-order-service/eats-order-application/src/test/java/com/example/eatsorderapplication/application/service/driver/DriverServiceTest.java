package com.example.eatsorderapplication.application.service.driver;

import com.example.commondata.dto.order.AddressDto;
import com.example.commondata.dto.order.UserOrderAddressDto;
import com.example.eatsorderapplication.application.dto.DriverDetailsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.redisson.api.*;
import org.redisson.api.geo.GeoSearchArgs;
import org.redisson.api.geo.GeoSearchNode;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.data.util.Pair;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.eatsorderapplication.application.service.driver.DriverService.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

class DriverServiceTest {

    @Mock
    private RedissonReactiveClient redissonReactiveClient;

    @Mock
    private RGeoReactive<String> geoReactive;

    @Mock
    private DriverMatchingStrategy strategy;

    @InjectMocks
    private DriverService driverService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redissonReactiveClient.getGeo(eq(DRIVER_GEO_KEY), any(TypedJsonJacksonCodec.class)))
            .thenReturn((RGeoReactive) geoReactive); // casting 안하면 에러뜸. getGeo 가 RGeoReactive<Object> 를 리턴해서.
    }

    @Test
    void user1driver1_and_user2driver2_matchingTest() {
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

        UserOrderAddressDto userAddress2 = new UserOrderAddressDto(
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

        when(geoReactive.searchWithPosition(any()))
            .thenReturn(
                Mono.just(
                    Map.of(
                        "driver1", new GeoPosition(128.0001, 37.0000),
                        "driver2", new GeoPosition(128.0002, 37.0000)
                    )));

        when(geoReactive.searchWithPosition(any()))
            .thenAnswer(invocation -> {
                // 인자로 받은 GeoSearchArgs를 확인해서 로직 작성
                // ref https://github.com/redisson/redisson/blob/master/redisson/src/main/java/org/redisson/RedissonGeo.java#L287
                GeoSearchArgs args = invocation.getArgument(0);
                GeoSearchNode node = (GeoSearchNode) args;
                Map<GeoSearchNode.Params, Object> params = node.getParams();


                double lon = (double) params.get(GeoSearchNode.Params.LONGITUDE);
                double lat = (double) params.get(GeoSearchNode.Params.LATITUDE);

                // userAddress1과 매칭되는 좌표일 경우
                if (lon == 128.0000d && lat == 37.0000d) {
                    return Mono.just(Map.of(
                        "driver1", new GeoPosition(128.0001, 37.0000)
                    ));
                }
                // userAddress2와 매칭되는 좌표일 경우
                else if (lon == 128.0002d && lat == 37.0000d) {
                    return Mono.just(Map.of(
                        "driver2", new GeoPosition(128.0002, 37.0000)
                    ));
                }

                // Default 빈 값 반환
                return Mono.just(Collections.emptyMap());
            });


        // When
        var result =
            driverService.getNearbyDriversFromUsers(List.of(userAddress1));

        // Then
        StepVerifier.create(result)
            .expectNextMatches(tuple -> {
                var candidates = tuple.getT1();
                Set<UserOrderAddressDto> failedLocations = tuple.getT2();

                return candidates.contains(Candidate.builder()
                    .userOrderAddress(userAddress1)
                    .driver(driver1)
                    .build()) &&
                    candidates.contains(Candidate.builder()
                        .userOrderAddress(userAddress2)
                        .driver(driver2)
                        .build()) &&
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

        when(geoReactive.searchWithPosition(any())).thenReturn(Mono.error(new RuntimeException("Search failed")));

        // When
        var result =
            driverService.getNearbyDriversFromUsers(List.of(userAddress1));

        // Then
        StepVerifier.create(result)
            .expectNextMatches(tuple -> {
                var drivers = tuple.getT1();
                Set<UserOrderAddressDto> failedLocations = tuple.getT2();

                return drivers.isEmpty() &&
                    failedLocations.contains(userAddress1);
            })
            .verifyComplete();
    }


}

class DriverServiceLockTest {
    @Mock
    private RedissonReactiveClient redissonReactiveClient;

    @Mock
    private RGeoReactive<DriverDetailsDto> geoReactive;

    @InjectMocks
    private DriverService driverService;
    @Mock
    private RLockReactive rLockReactive;

    @Mock
    private RScriptReactive rScriptReactive;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redissonReactiveClient.getGeo(eq(DRIVER_GEO_KEY), any(TypedJsonJacksonCodec.class)))
            .thenReturn((RGeoReactive) geoReactive); // casting 안하면 에러뜸. getGeo 가 RGeoReactive<Object> 를 리턴해서.
        when(redissonReactiveClient.getLock(anyString())).thenReturn(rLockReactive);
        when(redissonReactiveClient.getScript(StringCodec.INSTANCE)).thenReturn(rScriptReactive);


    }

    @Test
    @DisplayName("filterDriversWithoutLock - 일부 드라이버가 (driver4, driver5) 락을 보유한 경우 해당 드라이버 제외하고 반환")
    void filterDriverWithoutLock() {
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
        DriverDetailsDto driver3 = new DriverDetailsDto("driver3", 128.0001, 37.0000);
        DriverDetailsDto driver4 = new DriverDetailsDto("driver4", 128.0002, 37.0000);
        DriverDetailsDto driver5 = new DriverDetailsDto("driver5", 128.0001, 37.0000);


        // Lua 스크립트가 일부 드라이버의 락이 해제되었다고 응답
        when(rScriptReactive.eval(
            eq(RScript.Mode.READ_ONLY),
            anyString(),
            eq(RScript.ReturnType.MULTI),
            anyList(),
            anyList()
        )).thenReturn(Mono.just(List.of(
            "driver:lock:driver1",
            "driver:lock:driver2",
            "driver:lock:driver3")));

        var c1 = Candidate.builder()
            .userOrderAddress(userAddress1)
            .driver(driver1)
            .build();
        var c2 = Candidate.builder()
            .userOrderAddress(userAddress1)
            .driver(driver2)
            .build();
        var c3 = Candidate.builder()
            .userOrderAddress(userAddress1)
            .driver(driver3)
            .build();
        var c4 = Candidate.builder()
            .userOrderAddress(userAddress1)
            .driver(driver4)
            .build();
        var c5 = Candidate.builder()
            .userOrderAddress(userAddress1)
            .driver(driver5)
            .build();
        Set<Candidate> candidateSet = ConcurrentHashMap.newKeySet();
        candidateSet.addAll(List.of(c1, c2, c3, c4, c5));

        Set<UserOrderAddressDto> failedLocations = ConcurrentHashMap.newKeySet();

        // When
        var result = driverService.filterDriversWithoutLock(candidateSet, failedLocations);

        StepVerifier.create(result)
            .expectNextMatches(tuple2 -> {
                    var filteredCandidate = tuple2.getT1();

                    return filteredCandidate.contains(c1) &&
                        filteredCandidate.contains(c2) &&
                        filteredCandidate.contains(c3) &&
                        !filteredCandidate.contains(c4) &&
                        !filteredCandidate.contains(c5);
                }
            )
            .verifyComplete();

    }

    @Test
    @DisplayName("RLockReactive tryLock() 이 불리는지 테스트")
    void whenTryLockCalled_thenRLockReactiveShouldBeCalled() {
        Matching matching = Matching.builder()
            .userOrderAddress(UserOrderAddressDto.builder().build())
            .driver(DriverDetailsDto.builder()
                .driverId("driver1")
                .build())
            .build();

        // rLockReactive.tryLock 호출 시 모의 반환값 지정
        when(rLockReactive.tryLock(WAIT_TIME, LEASE_TIME, TIME_UNIT))
            .thenReturn(Mono.just(true));

        // driverService에서 tryLock 호출
        Mono<Matching> result = driverService.tryLock(matching);

        // RLockReactive가 불리는지 확인
        StepVerifier.create(result)
            .expectNextMatches(m -> m.getDriver().getDriverId().equals("driver1"))
            .verifyComplete();

        // rLockReactive의 tryLock이 호출되었는지 검증
        verify(rLockReactive, times(1)).tryLock(WAIT_TIME, LEASE_TIME, TIME_UNIT);
    }

    @Test
    @DisplayName("lock 실패하면 empty 반환하는가")
    void whenTryLockFailed_thenMonoEmptyReturned() {

    }

}