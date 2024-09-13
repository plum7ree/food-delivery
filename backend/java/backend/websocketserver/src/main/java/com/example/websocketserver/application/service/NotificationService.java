package com.example.websocketserver.application.service;


import com.example.kafka.avro.model.RestaurantApprovalNotificationEvent;
import com.example.websocketserver.application.data.dto.DriverDetailsDto;
import com.example.websocketserver.application.data.dto.NotificationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// https://velog.io/@raddaslul/Stomp%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%98%EC%97%AC-%EC%B1%84%ED%8C%85-%EB%B0%8F-item-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0
@Service
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;  // 웹소켓 메시지 전송을 위한 템플릿
    private final ObjectMapper objectMapper;

    @Resource(name = "driverMatchingMap")
    private final ConcurrentHashMap<String, DriverDetailsDto> driverMatchingMap;

    private final RedissonReactiveClient redissonReactiveClient;
    public static final String DRIVER_GEO_KEY = "drivers:geo";

    private final Sinks.Many<RestaurantApprovalNotificationEvent> generalSink;

    public NotificationService(SimpMessagingTemplate messagingTemplate,
                               ObjectMapper objectMapper,
                               ConcurrentHashMap<String, DriverDetailsDto> driverMatchingMap,
                               RedissonReactiveClient redissonReactiveClient,
                               @Qualifier("generalSink") Sinks.Many<RestaurantApprovalNotificationEvent> generalSink) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
        this.driverMatchingMap = driverMatchingMap;
        this.redissonReactiveClient = redissonReactiveClient;
        this.generalSink = generalSink;
    }

    @PostConstruct
    public void init() {
        generalSink.asFlux().doOnNext(e -> {
            if (e instanceof RestaurantApprovalNotificationEvent) {
                var event = ((RestaurantApprovalNotificationEvent) e);
                sendOrderApprovedNotification(event.getUserId().toString(), ""); // TODO is this async?
            }
        }).subscribe();
    }

    public void sendOrderApprovedNotification(String userId, String message) {
        NotificationDto notificationDto = NotificationDto.builder()
            .type("ORDER_APPROVED")
            .message("order approved")
            .build();
        log.info("sending restaurant approval notification");
        // 웹소켓을 통해 실시간 알림 전송
        messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", notificationDto);

    }


    //  Flux 순서와 결과
    //  순서: Flux.fromIterable은 입력 스트림의 순서를 유지하며, flatMap 내부의 작업이 비동기적으로 실행됩니다.
    //  즉, driverLocationMap의 driverId 순서에 따라 userId에게 메시지가 전송됩니다.
    //
    //  그러나 flatMap 내부의 Mono.fromRunnable은 비동기적으로 실행되므로,
    //  메시지 전송 작업이 완료되는 순서는 입력 순서와 다를 수 있습니다.
    //
    //  결과: 최종적으로 Flux가 처리된 후, 모든 비동기 작업이 완료됩니다. 메시지 전송 순서는 비동기적 작업의 실행 시점에 따라 달라질 수 있습니다.
    @Scheduled(fixedRate = 1000L)
    public void sendMatchedDriverLocation() {
        // TODO redis pub sub 을 이융해서 어떤 driver 가 매칭이 되면 채널 에 올려놓고 계속 데이터 방출하는 app 제작
        //  이제 websocket server 가 sub 으로써 데이터 구독만하면 간단함.
        //  consistent hashing 을 통해 레디스 분산 클러스터 제작.
        // driverMatchingMap에서 모든 driverId 목록을 가져옴

        Flux.fromIterable(driverMatchingMap.entrySet())
            .flatMap(entry -> {
                String userId = entry.getKey();
                DriverDetailsDto driver = entry.getValue();
                NotificationDto notificationDto = NotificationDto.builder()
                    .type("DRIVER_MATCHED")
                    .driverDetails(driver)
                    .build();
                return Mono.fromRunnable(() ->
                    messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", notificationDto)
                );

            }).subscribe();


    }

    @Scheduled(fixedRate = 1000)
    public void heartbeat() throws JsonProcessingException {
        Map<String, Object> heartbeatMessage = new HashMap<>();
        heartbeatMessage.put("type", "heartbeat");
        heartbeatMessage.put("timestamp", Instant.now().toString());

        String jsonMessage = objectMapper.writeValueAsString(heartbeatMessage);
        messagingTemplate.convertAndSend("/topic/heartbeat", jsonMessage);

    }
}