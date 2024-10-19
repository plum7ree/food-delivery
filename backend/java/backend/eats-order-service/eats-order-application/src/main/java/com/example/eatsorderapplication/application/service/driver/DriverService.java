package com.example.eatsorderapplication.application.service.driver;

import com.example.commondata.dto.order.UserOrderAddressDto;
import com.example.eatsorderapplication.application.dto.DriverDetailsDto;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.GeoUnit;
import org.redisson.api.RGeoReactive;
import org.redisson.api.RLockReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.geo.GeoSearchArgs;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
@Service
public class DriverService {

    private final RedissonReactiveClient redissonReactiveClient;
    public static final String DRIVER_GEO_KEY = "drivers:geo";
    public static final String MATCHING_HSET_KEY = "matching";
    public static final Function<String, String> DRIVER_LOCK_KEY =
        _driverId -> String.format("drivers:lock:%s", _driverId);
    public final static Long WAIT_TIME = 500L;
    public final static Long LEASE_TIME = 3600L * 3L; // 배달시간 최대 3시간
    public final static TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private final DriverMatchingStrategy strategy;

    // 락이 걸려있는 드라이버를 찾기 위해 Lua 스크립트 사용
    // redis insight 에서 테스트 해보고 싶다면
    // EVAL "local result = {} for i, key in ipairs(ARGV) do if redis.call('EXISTS', key) == 0 then table.insert(result, key) end end return result" 0 drivers:lock:5f2ce8a9-5eb9-41c4-a0a5-9aebaaea44d3 drivers:lock:53a5ddce-c62b-458a-a23a-fbb833e0bf1f drivers:lock:dcd817f5-8531-469f-892e-4f795c588dad drivers:lock:383adbe4-2c2c-44cb-85ae-3f829e4f33c9
    public static final String notLockedDriverLists_luaScript = "local result = {} " +
        "for i, key in ipairs(ARGV) do " +
        "  if redis.call('EXISTS', key) == 0 then " + // key 가 exists 하지 않으면(0),
        "    table.insert(result, key) " + // result 에 key 를 추가.
        "  end " +
        "end " +
        "return result";

    public DriverService(RedissonReactiveClient redissonReactiveClient,
                         @Qualifier("euclideanDistanceStrategy")
                         DriverMatchingStrategy strategy) {
        this.redissonReactiveClient = redissonReactiveClient;
        this.strategy = strategy;
    }

    public Mono<Tuple2<
        Set<Candidate>,
        Set<UserOrderAddressDto>>>
    getNearbyDriversFromUsers(
        List<UserOrderAddressDto> userLocations) {
        Set<Candidate> candidateMatching = ConcurrentHashMap.newKeySet(); // Set {{user1, drivers1}, {user1, drivers2}, {user2, drivers3}}
        Set<UserOrderAddressDto> failedLocations = ConcurrentHashMap.newKeySet(); // 실패한 user 저장
        RGeoReactive<String> geo = redissonReactiveClient.getGeo(DRIVER_GEO_KEY, new StringCodec());

        return Flux.fromIterable(userLocations)
            .flatMap(userAddress -> {
                GeoSearchArgs searchArgs = GeoSearchArgs.from(
                        userAddress.address().getLon(),
                        userAddress.address().getLat())
                    .radius(4, GeoUnit.KILOMETERS)
                    .count(50);

                return geo.searchWithPosition(searchArgs)
                    // Map<String, GeoPosition> 타입
                    .flatMapMany(driverDetailsDto -> Flux.fromIterable(driverDetailsDto.entrySet()))
                    .flatMap(driverIdAndGeoPositionEntry -> {
                        var driverId = driverIdAndGeoPositionEntry.getKey();
                        var lat = driverIdAndGeoPositionEntry.getValue().getLatitude();
                        var lon = driverIdAndGeoPositionEntry.getValue().getLongitude();
                        var driver = DriverDetailsDto.builder()
                            .driverId(driverId)
                            .lat(lat)
                            .lon(lon)
                            .build();

                        return Mono.just(Candidate.builder()
                            .userOrderAddress(userAddress)
                            .driver(driver)
                            .build());
                    })
                    .doOnError(error -> {
                        // Note: 에러가 난 geo search 와 해당 userLocation 을 기록해서
                        //  retry 나 dlq 로 보내야함.
                        failedLocations.add(userAddress);
                    });
            })
            .collect(() -> candidateMatching, Set::add)
            .map(set -> Tuples.of(set, failedLocations));
    }


    public Mono<List<Matching>> performMatching(Set<Candidate> candidates) {
        // 여기에 매칭 로직을 구현 (orderEvents를 사용하여 드라이버 매칭)

        return strategy.match(candidates); // 매칭된 드라이버 리스트 반환 (예시)
    }

    public Mono<Tuple2<
        Set<Candidate>,
        Set<UserOrderAddressDto>>> filterDriversWithoutLock(
        Set<Candidate> candidateSet,
        Set<UserOrderAddressDto> failedLocations) {

        if (candidateSet.isEmpty()) {
            return Mono.empty();

        }

        // lock 이 걸려있지 않은 (배달중이지않은) candidate 을 쉽게 거르기 위해 map 으로 작성.
        var driverIdToCandidateMap = new ConcurrentHashMap<String, List<Candidate>>();
        candidateSet.forEach(candidate -> {
            var key = candidate.getDriver().getDriverId();
            driverIdToCandidateMap.computeIfAbsent(key, k -> new ArrayList<>()).add(candidate);
        });

        // 락 키 목록 생성
        List<String> driverLockKeys = driverIdToCandidateMap.keySet().stream()
            .map(DRIVER_LOCK_KEY)
            .toList();

        Set<Candidate> filteredCandidateSet = ConcurrentHashMap.newKeySet();

        // getBuckets 이 아니라 getLock 으로 락 여부 판단해야한다
        return Flux.fromIterable(driverLockKeys)
            .flatMap(lockKey ->
                redissonReactiveClient.getLock(lockKey)
                    .isLocked()
                    .filter(acquired -> acquired == Boolean.FALSE)
                    .map(acquired -> lockKey)  // 락이 없는 경우 lockKey 반환
            )
            .map(key -> key.toString().replace("drivers:lock:", ""))
            .collect(() -> filteredCandidateSet, (set, driverKey) ->
                Optional.ofNullable(driverIdToCandidateMap.get(driverKey))
                    .ifPresent(set::addAll)
            )
            .map(set -> Tuples.of(set, failedLocations));

    }

    public Mono<Matching> tryLock(Matching matching) {
        var driver = matching.getDriver();
        String lockKey = "drivers:lock:" + driver.getDriverId();
        RLockReactive lock = redissonReactiveClient.getLock(lockKey);
        return lock.tryLock(WAIT_TIME, LEASE_TIME, TIME_UNIT)
            .flatMap(acquired -> {
                if (!acquired) {
                    log.info("lock failed for key: {}", lockKey);
                    return Mono.empty();
                }
                return Mono.just(matching);
                // .doFinally(signalType -> lock.unlock() // 언락 불가. 배달 완료되면 해야함.
            });
    }


}

//    public void match() {
//        @Qualifier("euclideanDistanceStrategy") DriverMatchingStrategy strategy,
//        List<UserAddressDto> userLocations){
//            return strategy.findNearbyDrivers(userLocations);
//        }
