package com.example.mytest.kafkatest;

import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.Map;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"test-topic"})
public class KafkaListenerTest {


    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaListenerService kafkaListenerService;

    private KafkaTemplate<String, String> kafkaTemplate;
    private final static String topicName = "test-topic";

    @BeforeEach
    void setUp() {
        // KafkaTemplate 설정
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        producerProps.put("key.serializer", StringSerializer.class);
        producerProps.put("value.serializer", StringSerializer.class);
        producerProps.put("schema.registry.url", "mock://test-uri");
        kafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerProps));

    }

    @Test
    public void testAcknowledgeLastMessage() throws Exception {
        // Send multiple messages
        String message = "test";
        kafkaTemplate.send(topicName, "partition-key", message).get();

        // Simulate waiting for messages to be processed
        Thread.sleep(2000);

        kafkaListenerService.acknowledgeLast();


    }
}
