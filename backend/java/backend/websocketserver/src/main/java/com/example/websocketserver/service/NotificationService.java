package com.example.websocketserver.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

// https://velog.io/@raddaslul/Stomp%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%98%EC%97%AC-%EC%B1%84%ED%8C%85-%EB%B0%8F-item-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0
@Service
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;  // 웹소켓 메시지 전송을 위한 템플릿
    private final ObjectMapper objectMapper;

    public NotificationService(SimpMessagingTemplate messagingTemplate, ObjectMapper objectMapper) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendNotification(String userId, String message) {
        try {
            Map<String, Object> json = new HashMap<>();
            json.put("message", message);
            String jsonMessage = objectMapper.writeValueAsString(json);
            // 웹소켓을 통해 실시간 알림 전송
            messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", jsonMessage);

            log.info("Notification created for user: {}", userId);
        } catch (Exception e) {
            log.error("Error sending sendNotification: ", e);
        }
    }

    @Scheduled(fixedRate = 10000)
    public void heartbeat() {
        try {
            Map<String, Object> heartbeatMessage = new HashMap<>();
            heartbeatMessage.put("type", "heartbeat");
            heartbeatMessage.put("timestamp", Instant.now().toString());

            String jsonMessage = objectMapper.writeValueAsString(heartbeatMessage);
            messagingTemplate.convertAndSend("/topic/heartbeat", jsonMessage);
        } catch (Exception e) {
            log.error("Error sending heartbeat: ", e);
        }
    }

//    public List<UserNotification> getUnreadNotifications(String userId) {
//        return notificationRepository.findByUserIdAndIsReadFalse(userId);
//    }
}