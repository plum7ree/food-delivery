package com.example.websocketserver.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;  // 웹소켓 메시지 전송을 위한 템플릿

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotification(String userId, String message) {

        // 웹소켓을 통해 실시간 알림 전송
        messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", message);

        log.info("Notification created for user: {}", userId);
    }

    @Scheduled(fixedRate = 1000)
    public void heartbeat() {
        log.info("heartbeat");
        // 웹소켓을 통해 실시간 알림 전송
        messagingTemplate.convertAndSend("/topic/heartbeat", "heartbeat");

    }

//    public List<UserNotification> getUnreadNotifications(String userId) {
//        return notificationRepository.findByUserIdAndIsReadFalse(userId);
//    }
}