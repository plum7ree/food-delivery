package com.example.eatsorderapplication.service.driver;

import com.example.commondata.dto.order.UserOrderAddressDto;
import com.example.eatsorderapplication.application.dto.DriverDetailsDto;
import com.example.eatsorderapplication.application.service.driver.DriverMatchingStrategy;
import com.example.eatsorderapplication.application.service.driver.Matching;
import com.example.eatsorderapplication.application.service.driver.SimpleWeightedEdge;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.matching.KuhnMunkresMinimalWeightBipartitePerfectMatching;
import org.jgrapht.alg.matching.MaximumWeightBipartiteMatching;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component("euclideanDistanceStrategy")
@Slf4j
public class EuclideanDistanceStrategy implements DriverMatchingStrategy {
    private static final int MAX_NEARBY_DRIVERS = 100;
    private static final int LOCK_TIMEOUT = 10; // 10초 타임아웃

    // 적도 위경도 1도 맨하탄 거리. Minimum weighted bipartite matching 을 위해
    // maximum bipartite matching 을 이용해야하는데,
    // 그러려면 max 값에서 weight 빼야하므로 이값이 쓰임.
    private static final double MANHANTTAN_MAX = 2 * 111320;

    /**
     * https://jgrapht.org/javadoc/org.jgrapht.core/org/jgrapht/alg/matching/package-summary.html
     *
     * @param users
     * @param drivers
     * @return
     */
    @Override
    public Mono<List<Matching>> match(Set<UserOrderAddressDto> users, Set<DriverDetailsDto> drivers) {

        Graph<String, SimpleWeightedEdge> biGraph = new SimpleWeightedGraph<>(SimpleWeightedEdge.class);

        Set<String> uSet = new HashSet<>(); // users
        Set<String> vSet = new HashSet<>(); // drivers

        users.stream()
            .map(UserOrderAddressDto::userId)
            .forEach(userId -> {
                uSet.add(userId);
                biGraph.addVertex(userId);
            });

        drivers.stream()
            .map(DriverDetailsDto::getDriverId)
            .forEach(driverId -> {
                vSet.add(driverId);
                biGraph.addVertex(driverId);
            });

        drivers.forEach(driverDetailsDto -> {
            users.forEach(userAddressDto -> {
                double driverLatitude = driverDetailsDto.getLat();
                double driverLongitude = driverDetailsDto.getLon();
                double addressLatitude = userAddressDto.address().getLat();
                double addressLongitude = userAddressDto.address().getLon();

                // 맨하탄 거리 계산
                String u = userAddressDto.userId();
                String v = driverDetailsDto.getDriverId();
                // 적도 기준 위경도 거리.
                double manhattanDistanceInMeter = MANHANTTAN_MAX - (Math.abs(addressLatitude - driverLatitude) * 111320 + Math.abs(addressLongitude - driverLongitude) * 111320);
                SimpleWeightedEdge edge = biGraph.addEdge(u, v);
                biGraph.setEdgeWeight(edge, manhattanDistanceInMeter);
            });

        });

        // Kuhn-Munkres 알고리즘 실행
        // 조건: equally sized partition 이여야함. O(V^3)
        // KuhnMunkresMinimalWeightBipartitePerfectMatching<String, SimpleWeightedEdge> kmMatcher =
        //   new KuhnMunkresMinimalWeightBipartitePerfectMatching<>(biGraph, uSet, vSet);
        // MatchingAlgorithm.Matching<String, SimpleWeightedEdge> result = kmMatcher.getMatching();

        // O(n(m + nlogn))
        // 조건: maximum 매칭이라서 cost = max - cost 로 바꿔야함.
        MaximumWeightBipartiteMatching<String, SimpleWeightedEdge> matchingAlgorithm =
            new MaximumWeightBipartiteMatching<>(biGraph, uSet, vSet);

        MatchingAlgorithm.Matching<String, SimpleWeightedEdge> result =
            matchingAlgorithm.getMatching();


        HashMap<String, UserOrderAddressDto> usersMap = new HashMap<>();
        HashMap<String, DriverDetailsDto> driversMap = new HashMap<>();

        drivers.stream().forEach(e -> {
            driversMap.put(e.getDriverId(), e);
        });
        users.stream().forEach(e -> {
            usersMap.put(e.userId(), e);
        });

        return Mono.just(result.getEdges().stream().map(e -> {
            var userId = e.getSource();
            var driverId = e.getTarget();

            var userOrderAddressDto = usersMap.get(userId);
            var driverDto = driversMap.get(driverId);


            return Matching.builder()
                .driver(driverDto)
                .userOrderAddress(userOrderAddressDto)
                .build();
        }).collect(Collectors.toList()));
    }


//        private Mono<Matching> tryLockAndMatchDriver (AddressDto addressDto, DriverDetailsDto driver){
//            String driverId = driver.getDriverId();
//            RLockReactive lock = redissonClient.getLock(DRIVER_LOCK_KEY.apply(driverId));
//
//            return lock.tryLock(LOCK_TIMEOUT, TimeUnit.SECONDS)
//                .flatMap(locked -> {
//                    if (locked) {
//                        return matchingMap.get(driverId)//TODO 존재하면 취소. 없으면 userIdListStr 를 List 로 objectMapping 후 userId 추가 후 다시 str 으로 저장
//                            .switchIfEmpty((Mono.defer(() -> {
//                                // 만약 매칭된 유저가 없는 경우, 처음으로 유저를 추가
//                                List<AddressDto> addressDtoList = new ArrayList<>();
//                                addressDtoList.add(addressDto);
//                                return Mono.fromCallable(() -> objectMapper.writeValueAsString(addressDtoList));
//
//
//                            })))
//                            .flatMap(listStr -> {
//                                return matchingMap.put(driverId, listStr)
//                                    .thenReturn(new Matching(addressDto, driver))
//                                    .doOnSuccess(matching -> log.info("First match of user {} with driver {}", addressDto.getUserId(), driver))
//                                    .publishOn(Schedulers.boundedElastic())
//                                    .doFinally(signalType -> lock.unlock().subscribe());
//                            });
//                    } else {
//                        log.info("Driver {} is locked, skipping.", driverId);
//                        return Mono.empty(); // 락을 얻지 못한 경우 빈 Flux를 반환
//                    }
//                })
//                .onErrorResume(e -> {
//                    log.error("Error while trying to lock and match driver {}: {}", driverId, e.getMessage());
//                    return Mono.empty(); // 에러 발생 시 해당 드라이버에 대한 처리를 건너뛰고 다음으로 이동
//                });
//        }


}
