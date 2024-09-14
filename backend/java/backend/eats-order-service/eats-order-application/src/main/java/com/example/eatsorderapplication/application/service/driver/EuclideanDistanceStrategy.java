//package com.example.eatsorderapplication.service.driver;
//
//import com.example.eatsorderapplication.data.dto.DriverDetailsDto;
//import com.example.eatsorderdomain.data.dto.AddressDto;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.annotation.PostConstruct;
//import lombok.extern.slf4j.Slf4j;
//import org.jgrapht.Graph;
//import org.jgrapht.alg.interfaces.MatchingAlgorithm;
//import org.jgrapht.alg.matching.MaximumWeightBipartiteMatching;
//import org.jgrapht.alg.util.Pair;
//import org.jgrapht.alg.util.Triple;
//import org.jgrapht.graph.SimpleWeightedGraph;
//import org.redisson.api.*;
//import org.redisson.api.geo.GeoSearchArgs;
//import org.redisson.codec.TypedJsonJacksonCodec;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.core.scheduler.Schedulers;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.TimeUnit;
//import java.util.function.Function;
//
//@Component("euclideanDistanceStrategy")
//@Slf4j
//public class EuclideanDistanceStrategy implements DriverMatchingStrategy {
//    private static final int MAX_NEARBY_DRIVERS = 100;
//    private static final int LOCK_TIMEOUT = 10; // 10초 타임아웃
//
//    private final RedissonReactiveClient redissonClient;
//    public static final String DRIVER_GEO_KEY = "drivers:geo";
//    public static final String MATCHING_HSET_KEY = "matching";
//    public static final Function<String, String> DRIVER_LOCK_KEY =
//        _driverId -> String.format("driver:lock:", _driverId);
//    private RGeoReactive<DriverDetailsDto> geo;
//    Graph<String, SimpleWeightedEdge<String>> biGraph = new SimpleWeightedGraph<>(SimpleWeightedEdge.class);
//    private RMapReactive<String, String> matchingMap;
//
//    private final ObjectMapper objectMapper;
//
//    public EuclideanDistanceStrategy(@Qualifier("redissonReactiveClient") RedissonReactiveClient redissonClient,
//                                     ObjectMapper objectMapper) {
//        this.redissonClient = redissonClient;
//        this.objectMapper = objectMapper;
//    }
//
//    @PostConstruct
//    public void init() {
//        geo = redissonClient.getGeo(DRIVER_GEO_KEY, new TypedJsonJacksonCodec(DriverDetailsDto.class));
//        matchingMap = redissonClient.getMap(MATCHING_HSET_KEY, new TypedJsonJacksonCodec(String.class));
//    }
//
//
//    @Override
//    public Mono<List<Matching>> findNearbyDrivers(List<UserAddressDto> userLocations) {
//
//        ConcurrentHashMap<String, AddressDto> addressDtoMap = new ConcurrentHashMap<>();
//        ConcurrentHashMap<String, DriverDetailsDto> driverDetailsDtoMap = new ConcurrentHashMap<>();
//
//        Flux.fromIterable(userLocations)
//            .flatMap(addressDto -> {
//                var userLon = addressDto.getLon();
//                var userLat = addressDto.getLat();
//                GeoSearchArgs searchArgs = GeoSearchArgs.from(userLon, userLat)
//                    .radius(4, GeoUnit.KILOMETERS)
//                    .order(GeoOrder.ASC)
//                    .count(MAX_NEARBY_DRIVERS);
//                return geo.search(searchArgs)
//                    .flatMapMany(Flux::fromIterable)
//                    .flatMap(driverDetailsDto -> {
//                        addressDtoMap.put(addressDto.getId(), addressDto);
//                        driverDetailsDtoMap.put(driverDetailsDto.getDriverId(), driverDetailsDto);
//                        return Mono.just(Pair.of(addressDto, driverDetailsDto));
//                    })
//                    .take(50);
//            }) // TODO 몇몇 요청이 에러 및 타임아웃 이면 처리.
//            .collectList()
//            .flatMapMany((List<Pair<AddressDto, DriverDetailsDto>> candidateTupleList) -> {
//                Set<String> uSet = new HashSet<>();
//                Set<String> vSet = new HashSet<>();
//                candidateTupleList.stream().forEach(candidateTuple -> {
//                    var addressDto = candidateTuple.getFirst();
//                    var driverDetailsDto = candidateTuple.getSecond();
//                    // 주소와 드라이버 세부 정보에서 경도와 위도 가져오기
//                    double addressLatitude = addressDto.getLat();
//                    double addressLongitude = addressDto.getLon();
//                    double driverLatitude = driverDetailsDto.getLat();
//                    double driverLongitude = driverDetailsDto.getLon();
//
//                    // 맨하탄 거리 계산
//                    double manhattanDistance = Math.abs(addressLatitude - driverLatitude) + Math.abs(addressLongitude - driverLongitude);
//                    String u = addressDto.getId();
//                    String v = driverDetailsDto.getDriverId();
//                    uSet.add(u);
//                    vSet.add(v);
//                    SimpleWeightedEdge<String> edge = biGraph.addEdge(u, v);
//                    biGraph.setEdgeWeight(edge, manhattanDistance);
//
//                });
//                MaximumWeightBipartiteMatching<String, SimpleWeightedEdge<String>> matchingAlgorithm =
//                    new MaximumWeightBipartiteMatching<>(biGraph, uSet, vSet);
//
//                MatchingAlgorithm.Matching<String, SimpleWeightedEdge<String>> result =
//                    matchingAlgorithm.getMatching();
//                return Flux.fromIterable(result.getEdges());
//            })
//            .flatMap(matchedEdge -> {
//                var u = matchedEdge.getSource();
//                var v = matchedEdge.getTarget();
//                var triple = Triple.of(addressDtoMap.get(u), driverDetailsDtoMap.get(v), matchedEdge.getWeight());
//                return Mono.just(triple);
//            })
//            .flatMap((Triple<AddressDto, DriverDetailsDto, Double> matchedTuple) -> {
//                var addressDto = matchedTuple.getFirst();
//                var driverDto = matchedTuple.getSecond();
//                var distance = matchedTuple.getThird();
//                return tryLockAndMatchDriver(addressDto, driverDto);
//                //TODO  match 된 애들은 그대로 진생키기고 어레 뜬 애들은 다시 retry 시켜야함.
//                // TODO 최종적으로 에러가 나면 다시 큐에 넣어야할듯.
//
//            })
//            .doFinally(signalType -> {
//                addressDtoMap.clear();
//                driverDetailsDtoMap.clear();
//            }); //TODO flush addressDtoMap, driverDetailsDtoMap.
//        return null;
//    }
//
//
//    private Mono<Matching> tryLockAndMatchDriver(AddressDto addressDto, DriverDetailsDto driver) {
//        String driverId = driver.getDriverId();
//        RLockReactive lock = redissonClient.getLock(DRIVER_LOCK_KEY.apply(driverId));
//
//        return lock.tryLock(LOCK_TIMEOUT, TimeUnit.SECONDS)
//            .flatMap(locked -> {
//                if (locked) {
//                    return matchingMap.get(driverId)//TODO 존재하면 취소. 없으면 userIdListStr 를 List 로 objectMapping 후 userId 추가 후 다시 str 으로 저장
//                        .switchIfEmpty((Mono.defer(() -> {
//                            // 만약 매칭된 유저가 없는 경우, 처음으로 유저를 추가
//                            List<AddressDto> addressDtoList = new ArrayList<>();
//                            addressDtoList.add(addressDto);
//                            return Mono.fromCallable(() -> objectMapper.writeValueAsString(addressDtoList));
//
//
//                        })))
//                        .flatMap(listStr -> {
//                            return matchingMap.put(driverId, listStr)
//                                .thenReturn(new Matching(addressDto, driver))
//                                .doOnSuccess(matching -> log.info("First match of user {} with driver {}", addressDto.getUserId(), driver))
//                                .publishOn(Schedulers.boundedElastic())
//                                .doFinally(signalType -> lock.unlock().subscribe());
//                        });
//                } else {
//                    log.info("Driver {} is locked, skipping.", driverId);
//                    return Mono.empty(); // 락을 얻지 못한 경우 빈 Flux를 반환
//                }
//            })
//            .onErrorResume(e -> {
//                log.error("Error while trying to lock and match driver {}: {}", driverId, e.getMessage());
//                return Mono.empty(); // 에러 발생 시 해당 드라이버에 대한 처리를 건너뛰고 다음으로 이동
//            });
//    }
//
//}
