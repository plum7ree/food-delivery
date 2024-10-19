package com.example.websocketserver.application.service;


import com.example.commondata.domain.events.order.DriverMatchedStatus;
import com.example.kafka.avro.model.DriverMatchedEvent;
import com.example.websocketserver.application.data.dto.DriverDetailsDto;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RGeoReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;
import reactor.util.concurrent.Queues;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class DriverInfoService {

    private final Sinks.Many<DriverMatchedEvent> matchedDriverEventSink;
    private final SimpMessagingTemplate messagingTemplate;  // 웹소켓 메시지 전송을 위한 템플릿
    @Resource(name = "driverMatchingMap")
    private final ConcurrentHashMap<String, DriverDetailsDto> driverMatchingMap; // userId, driver

    private final RedissonReactiveClient redissonReactiveClient;
    public static final String DRIVER_GEO_KEY = "drivers:geo";

    private Disposable periodicTaskDisposable;

    public DriverInfoService(
        @Qualifier("matchedDriverEventSink")
        Sinks.Many<DriverMatchedEvent> matchedDriverEventSink,
        SimpMessagingTemplate messagingTemplate,
        ConcurrentHashMap<String, DriverDetailsDto> driverMatchingMap,
        RedissonReactiveClient redissonReactiveClient) {
        this.matchedDriverEventSink = matchedDriverEventSink;
        this.messagingTemplate = messagingTemplate;
        this.driverMatchingMap = driverMatchingMap;
        this.redissonReactiveClient = redissonReactiveClient;
    }

    @PostConstruct
    public void init() {
        matchedDriverEventSink.asFlux()
            .subscribe(this::handleDriverMatchedEvent);

        periodicTaskDisposable = Flux.interval(Duration.ofSeconds(1))
            .publishOn(Schedulers.boundedElastic())
            .flatMap(tick -> sendDriverLocationsToUsers())
            .subscribe();
    }

    private void handleDriverMatchedEvent(DriverMatchedEvent event) {
        String userId = event.getUserId().toString();
        DriverMatchedStatus status = DriverMatchedStatus.valueOf(event.getStatus().toString());
        if (status == DriverMatchedStatus.MATCHED) {
            // 배달 매칭 이벤트: map에 추가 또는 업데이트
            var driver = event.getDriverDetails();
            DriverDetailsDto driverDetails = DriverDetailsDto.builder()
                .driverId(driver.getDriverId().toString())
                .lat(driver.getLat())
                .lon(driver.getLon())
                .build();
            driverMatchingMap.put(userId, driverDetails);
            log.info("매칭 추가/업데이트: userId={} driverId={}", userId, driverDetails.getDriverId());
        } else if (status == DriverMatchedStatus.DELIVERY_COMPLETED) {

            // 배달 완료 이벤트: map에서 제거
            driverMatchingMap.remove(userId);
            log.info("배달 완료로 매칭 제거: userId={}", userId);
        } else if (status == DriverMatchedStatus.CANCELLED) {
        }
    }

    public Mono<Void> sendDriverLocationsToUsers() {
        if (driverMatchingMap.isEmpty()) {
            return Mono.empty();
        }

        return Flux.fromIterable(driverMatchingMap.entrySet())
            .flatMap(entry -> {
                String userId = entry.getKey();
                DriverDetailsDto driverDetails = entry.getValue();
                return getDriverLocation(driverDetails.getDriverId())
                    .doOnNext(driverDetailsDto -> {
                        sendLocationToUser(userId, driverDetailsDto);
                    })
                    .then();
            }, Math.min(driverMatchingMap.size(), Queues.SMALL_BUFFER_SIZE))
            .then();
    }

    // getGeo 에서 드라이버가 더이상 레디스에 존재하지 않는 경우 에러를 방출함: filter(map->!map.isEmpty()) 로 해결
    // TODO 카프카에서 구독 못하게 하려면 어떻게하지.. 현재도 latest + read_commited + enable.auto.commit 인데.
    public Mono<DriverDetailsDto> getDriverLocation(String driverId) {
        RGeoReactive<String> geo = redissonReactiveClient.getGeo(DRIVER_GEO_KEY, new StringCodec());
        return geo.pos(driverId)
            .filter(map -> !map.isEmpty())
            .map(map -> {
                var entry = map.entrySet().iterator().next();
                var _driverId = entry.getKey();
                var lat = entry.getValue().getLatitude();
                var lon = entry.getValue().getLongitude();
                return DriverDetailsDto.builder()
                    .driverId(_driverId)
                    .lat(lat)
                    .lon(lon)
                    .build();
            });
    }

    public void sendLocationToUser(String userId, DriverDetailsDto driverDetailsDto) {
        // 웹소켓을 통해 실시간 알림 전송
        log.info("sendLocationToUser: {} {}", userId, driverDetailsDto);
        messagingTemplate.convertAndSendToUser(userId, "/queue/driver", driverDetailsDto);

    }

}
