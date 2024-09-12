//package com.example.eatsorderapplication.service.driver;
//
//import com.example.eatsorderapplication.service.client.UserServiceFeignClient;
//import com.example.eatsorderdataaccess.entity.MatchingEntity;
//import com.example.eatsorderdataaccess.repository.MatchingRepository;
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.PreDestroy;
//import lombok.extern.slf4j.Slf4j;
//import org.redisson.api.RQueueReactive;
//import org.redisson.api.RedissonReactiveClient;
//import org.redisson.codec.TypedJsonJacksonCodec;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Mono;
//import reactor.core.scheduler.Schedulers;
//
//import java.util.List;
//import java.util.UUID;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//@Service
//@Slf4j
//public class OrderMatchingService {
//    private static final int POLLING_INTERVAL = 5; // 5초마다 큐를 검사
//
//    private final DriverMatchingStrategy strategy;
//
//    private final UserServiceFeignClient userServiceFeignClient;
//    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//
//    private final MatchingRepository matchingRepository;
//    private final RedissonReactiveClient redissonReactiveClient;
//    private RQueueReactive<Matching> matchingQueue;
//    private ThreadPoolTaskScheduler taskScheduler;
//
//    public OrderMatchingService(@Qualifier("euclideanDistanceStrategy") DriverMatchingStrategy strategy,
//                                UserServiceFeignClient userServiceFeignClient,
//                                MatchingRepository matchingRepository, RedissonReactiveClient redissonReactiveClient) {
//        this.strategy = strategy;
//        this.userServiceFeignClient = userServiceFeignClient;
//        this.matchingRepository = matchingRepository;
//        this.redissonReactiveClient = redissonReactiveClient;
//    }
//
//    @PostConstruct
//    public void init() {
//        // 스케줄러 설정
//        this.taskScheduler = new ThreadPoolTaskScheduler();
//        this.taskScheduler.setPoolSize(5);
//        this.taskScheduler.initialize();
//
//        matchingQueue = redissonReactiveClient.getQueue("matching_queue", new TypedJsonJacksonCodec(Matching.class));
//
//    }
//
//    @PreDestroy
//    public void cleanup() {
//        scheduler.shutdown();
//    }
//
//    private Mono<Void> saveAllMatchings(List<MatchingEntity> matchedList) {
//        return Mono.fromCallable(() -> {
//                matchingRepository.saveAll(matchedList);
//                return Void.TYPE;
//            }).subscribeOn(Schedulers.boundedElastic())
//            .then();
//    }
//
//    // 큐에 쌓인 주문 처리
//    private void processOrders() {
//        // TODO convert to RDBC or do this with Virtual Thread.
//        getUnmatchedOrders()
//            .flatMap(unmatchedEntityList -> Mono.fromCallable(() ->
//                userServiceFeignClient.findUserLatLonByIds(unmatchedEntityList.stream().map(MatchingEntity::getUserId).toList())))//TODO convert to non-blocking
//            .subscribeOn(Schedulers.boundedElastic())
//            .flatMap(strategy::findNearbyDrivers)
//            .doOnNext(item -> log.info("matched drivers: {}", item.toString()))
//            .flatMap((List<Matching> matchingList) -> Mono.just(matchingList.stream().map(
//                matching -> MatchingEntity.builder()
//                    .userId(UUID.fromString(matching.getAddress().getUserId()))
//                    .driverId(UUID.fromString(matching.getDriver().getDriverId()))
//                    .status("PROCESSING")
//                    .build()).toList()))
//            .flatMap(this::saveAllMatchings)
//            .subscribe(); //TODO subscribe() 안쓰고, 전체적인 스케쥴러 자체를 non-blocking 으로 하려면?
//    }
//
//    private Mono<List<MatchingEntity>> getUnmatchedOrders() {
//        return Mono.fromCallable(() -> matchingRepository.findByStatusIn(List.of("PENDING", "FAILED")))
//            .subscribeOn(Schedulers.boundedElastic()); // 블로킹 작업 처리
//    }
//
//    private Mono<Void> addToMatchingQueue(List<Matching> matchings) {
//        return Mono.fromCallable(() -> {
//                matchingQueue.addAll(matchings).block();
//                return Void.TYPE;
//            }).subscribeOn(Schedulers.boundedElastic())
//            .then();
//    }
//
//
//}
//
