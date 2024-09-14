package com.example.user.service.driver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBatchReactive;
import org.redisson.api.RGeoReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@Slf4j
public class DriverSimulationService {


    private final RedissonReactiveClient redissonReactiveClient;

    ArrayList<DriverDomainEntity> driverSimulationObjectList = new ArrayList<>();
    Integer nDriver = 100;
    public static final String DRIVER_GEO_KEY = "drivers:geo";
    private RBatchReactive batch;
    private RGeoReactive<DriverDetailsDto> geo; // "driver-locations"는 실제 Redis 키로 변경
    private final ObjectMapper objectMapper;

    public DriverSimulationService(RedissonReactiveClient redissonReactiveClient, ObjectMapper objectMapper) {
        this.redissonReactiveClient = redissonReactiveClient;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void createDrivers() {
        batch = redissonReactiveClient.createBatch();
        geo = batch.getGeo(DRIVER_GEO_KEY, new TypedJsonJacksonCodec(DriverDetailsDto.class));

        Random random = new Random(); // Random 객체 생성

        IntStream.range(0, nDriver).forEach(i -> {
            // 랜덤 객체를 사용하여 각 드라이버에 대해 다른 시드 생성
            var coord = DriverDomainEntity.selectRandomCoordinate(
                random.nextLong(), // 랜덤 시드 사용
                37.5784,
                126.9255,
                37.4842,
                127.0842);
            driverSimulationObjectList.add(
                DriverDomainEntity.builder()
                    .id(UUID.randomUUID())
                    .lat(coord.get(0))
                    .lon(coord.get(1))
                    .build());
        });

    }

    @Scheduled(fixedRate = 10000L, initialDelay = 0L) // initialDelay = 0L 바로 호출후 10 초 간격으로 호출.
    public void sendLocation() {
        // 매번 새로운 배치와 geo 객체를 생성
        RBatchReactive batch = redissonReactiveClient.createBatch();
        RGeoReactive<DriverDetailsDto> geo = batch.getGeo(DRIVER_GEO_KEY, new TypedJsonJacksonCodec(DriverDetailsDto.class));

        driverSimulationObjectList.stream().forEach(driverDomainEntity -> {
            DriverDetailsDto driverDetailsDto = DriverDetailsDto.builder()
                .driverId(String.valueOf(driverDomainEntity.getId()))
                .lat(driverDomainEntity.getLat())
                .lon(driverDomainEntity.getLon())
                .build();
            geo.add(driverDomainEntity.getLon(), driverDomainEntity.getLat(), driverDetailsDto);
        });

        batch.execute()
            .subscribe(
                result -> log.info("Driver batch update successful!"), // 성공 시 로그 출력
                error -> log.error("Driver batch update failed", error)
            );
    }

}
