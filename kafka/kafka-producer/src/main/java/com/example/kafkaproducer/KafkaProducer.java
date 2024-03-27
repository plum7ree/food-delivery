package com.example.kafkaproducer;

import com.example.kafka.admin.client.KafkaAdminClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * To use this in a service A,
 * 1. import dependency into pom.xml
 * 2. add the package into A's main java file's @ComponentScan
 *
 * @param <K>
 * @param <V>
 */
@Component
@RequiredArgsConstructor
public class KafkaProducer<K, V> {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);


    private final KafkaAdminClient kafkaAdminClient;
    private final KafkaTemplate<K, V> kafkaTemplate;

    public void send(String topicName, K key, V message) {
        kafkaTemplate.send(topicName, key, message);
    }

    @EventListener
    public void OnAppStarted(ApplicationStartedEvent event) {
        log.info("Kafka Producer createTopics!");
        kafkaAdminClient.createTopics();
    }
}
