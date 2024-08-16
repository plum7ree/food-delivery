package com.example.websocketserver.service.listener;


import com.example.kafka.avro.model.NotificationAvroModel;
import com.example.websocketserver.service.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationKafkaConsumer {

    private KafkaConsumer<String, NotificationAvroModel> consumer;
    private final NotificationService notificationService;


    @Value("${kafka-consumer-group-id.notification-consumer-group-id}")
    private String groupId;

    @Value("${topic-names.notification-topic-name}")
    private String topicName;

    @Resource(name = "commonKafkaConsumerConfigs")
    // bean 이 map 인 경우는 @Resource 를 사용해야한다. 안그러면 map of map 리 리턴되어서 이름으로 다시 찾아야함.
    private Map<String, Object> commonConsumerConfigs;


    @PostConstruct
    public void init() {
        Map<String, Object> props = commonConsumerConfigs;
        log.info(commonConsumerConfigs.toString());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topicName));

        new Thread(this::pollMessages).start();
    }

    @PreDestroy
    public void cleanup() {
        if (consumer != null) {
            consumer.close();
        }
    }

    private void pollMessages() {
        try {
            while (true) {
                ConsumerRecords<String, NotificationAvroModel> records = consumer.poll(Duration.ofMillis(100));
                records.forEach(record -> {
                    if (processRecord(record.value())) {
                        consumer.commitSync(Collections.singletonMap(
                            new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset() + 1)
                        ));
                    } else {
                        consumer.commitSync(Collections.singletonMap(
                            new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset())
                        ));
                    }
                });
            }
        } catch (Exception e) {
            log.error("Error in Kafka polling loop: {}", e.getMessage());
        } finally {
            consumer.close();
        }
    }

    private boolean processRecord(NotificationAvroModel message) {
        log.info("topic received: {}", message);
        String userId = message.getUserId().toString();
        String content = message.getMessage().toString();
        notificationService.sendNotification(userId, content);
        return true;
    }


}
