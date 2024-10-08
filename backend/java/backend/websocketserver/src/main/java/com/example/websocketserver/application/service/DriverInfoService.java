package com.example.websocketserver.application.service;


import com.example.commondata.domain.events.order.DriverMatchedStatus;
import com.example.kafka.avro.model.DriverMatchedEvent;
import com.example.websocketserver.application.data.dto.DriverDetailsDto;
import com.example.websocketserver.application.data.dto.NotificationDto;
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
        if (event.getType() == DriverMatchedStatus.MATCHED.name()) {
            // 배달 매칭 이벤트: map에 추가 또는 업데이트
            var driver = event.getDriverDetails();
            DriverDetailsDto driverDetails = DriverDetailsDto.builder()
                .driverId(driver.getDriverId().toString())
                .lat(driver.getLat())
                .lon(driver.getLon())
                .build();
            driverMatchingMap.put(userId, driverDetails);
            System.out.println("매칭 추가/업데이트: userId=" + userId + ", driverId=" + driverDetails.getDriverId());
        } else if (event.getType() == DriverMatchedStatus.DELIVERY_COMPLETED.name()) {
            // 배달 완료 이벤트: map에서 제거
            driverMatchingMap.remove(userId);
            System.out.println("배달 완료로 매칭 제거: userId=" + userId);
        } else if (event.getType() == DriverMatchedStatus.CANCELLED.name()) {

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

    public Mono<DriverDetailsDto> getDriverLocation(String driverId) {
        RGeoReactive<String> geo = redissonReactiveClient.getGeo(DRIVER_GEO_KEY, new StringCodec());
        return geo.pos(driverId)
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
        log.info("sendLocationToUser userId: {} driver: {}", userId, driverDetailsDto);
        // 웹소켓을 통해 실시간 알림 전송
        messagingTemplate.convertAndSendToUser(userId, "/queue/driver", driverDetailsDto);

    }

}
