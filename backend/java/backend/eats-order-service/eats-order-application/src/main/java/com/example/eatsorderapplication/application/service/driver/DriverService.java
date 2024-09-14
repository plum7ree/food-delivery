package com.example.eatsorderapplication.application.service.driver;

import com.example.commondata.dto.order.UserAddressDto;
import com.example.eatsorderapplication.application.dto.DriverDetailsDto;
import org.redisson.api.GeoUnit;
import org.redisson.api.RGeoReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.geo.GeoSearchArgs;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Service
public class DriverService {

    private final RedissonReactiveClient redissonReactiveClient;
    public static final String DRIVER_GEO_KEY = "drivers:geo";
    public static final String MATCHING_HSET_KEY = "matching";
    public static final Function<String, String> DRIVER_LOCK_KEY =
        _driverId -> String.format("driver:lock:", _driverId);

    public DriverService(RedissonReactiveClient redissonReactiveClient,
                         RedissonReactiveClient redissonClient) {
        this.redissonReactiveClient = redissonReactiveClient;
    }

    public Mono<Tuple3<Set<DriverDetailsDto>, Set<UserAddressDto>, Set<UserAddressDto>>> getNearbyDriversFromUsers(List<UserAddressDto> userLocations) {
        Set<DriverDetailsDto> driverSet = new HashSet<>(); // 중복을 제거할 Set
        Set<UserAddressDto> successLocations = new HashSet<>(); // 실패한 위치 저장
        Set<UserAddressDto> failedLocations = new HashSet<>(); // 실패한 위치 저장

        RGeoReactive<DriverDetailsDto> geo = redissonReactiveClient.getGeo(DRIVER_GEO_KEY, new TypedJsonJacksonCodec(DriverDetailsDto.class));

        return Flux.fromIterable(userLocations)
            .flatMap(userAddress -> {
                GeoSearchArgs searchArgs = GeoSearchArgs.from(
                        userAddress.address().getLon(),
                        userAddress.address().getLat())
                    .radius(4, GeoUnit.KILOMETERS)
                    .count(50);

                return geo.search(searchArgs)
                    .doOnNext(driverDetailsDto -> {
                        successLocations.add(userAddress);
                    })
                    .doOnError(error -> {
                        // Note: 에러가 난 geo search 와 해당 userLocation 을 기록해서
                        //  kafka consumer 에서 ack 수행 하지 말아야함.
                        failedLocations.add(userAddress);
                    })
                    .flatMapMany(Flux::fromIterable)
                    .onErrorResume(error -> Mono.empty());
            })
            .collect(() -> driverSet, Set::add) // 결과를 Set에 수집 (중복 제거)
            .map(resultSet -> Tuples.of(resultSet, successLocations, failedLocations)); // Tuple로 성공/실패 결과 반환

    }

    public Mono<List<Matching>> performMatching(Tuple2<Set<DriverDetailsDto>, Set<UserAddressDto>> orderEvents) {
        // 여기에 매칭 로직을 구현 (orderEvents를 사용하여 드라이버 매칭)

        return Mono.just(List.of()); // 매칭된 드라이버 리스트 반환 (예시)
    }
}

//    public void match() {
//        @Qualifier("euclideanDistanceStrategy") DriverMatchingStrategy strategy,
//        List<UserAddressDto> userLocations){
//            return strategy.findNearbyDrivers(userLocations);
//        }
