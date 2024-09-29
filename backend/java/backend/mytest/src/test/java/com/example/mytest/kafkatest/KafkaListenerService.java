package com.example.mytest.kafkatest;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class KafkaListenerService {

    private final ConcurrentHashMap<String, ConsumerRecord<String, String>> messageMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Acknowledgment> ackMap = new ConcurrentHashMap<>();

    @KafkaListener(topics = "test-topic", groupId = "test-group")
    public void listen(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        System.out.println("Received message: " + record.value());
        messageMap.put(record.key(), record);
        ackMap.put(record.key(), acknowledgment);

        // Simulate processing logic here
        // Save acknowledgment for later use
    }


    public void acknowledgeLast() {
        if (!messageMap.isEmpty()) {
            String lastKey = ackMap.keySet().stream().reduce((first, second) -> second).orElse(null);
            Acknowledgment acknowledgment = ackMap.get(lastKey);
            if (acknowledgment != null) {
                // 마지막 메시지의 Acknowledgment 객체를 호출하여 메시지를 ACK 처리
                System.out.println("Acknowledging message with key: " + lastKey);
                acknowledgment.acknowledge();
                // ackMap에서 제거
                ackMap.remove(lastKey);
            }
        }
    }
}
