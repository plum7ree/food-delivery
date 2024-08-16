/**
 * TODO please manually run [project root]/docker/start-kafka-cluster.sh, start-postgres.sh
 *  see the topics published with kafka ui. http://localhost:9000/
 */
package com.example.eatsorderapplication.debezium;

import com.example.commondata.domain.aggregate.valueobject.OrderStatus;
import com.example.commondata.domain.aggregate.valueobject.OutboxStatus;
import com.example.commondata.domain.aggregate.valueobject.SagaStatus;
import com.example.eatsorderconfigdata.EatsOrderServiceConfigData;
import com.example.eatsorderdataaccess.entity.RestaurantApprovalOutboxMessageEntity;
import com.example.eatsorderdataaccess.repository.RestaurantApprovalRequestOutboxRepository;
import com.example.kafka.avro.model.RequestAvroModel;
import com.google.common.collect.ImmutableMap;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Profile("test")
public class SaveOutboxTableTest {

    @Autowired
    private RestaurantApprovalRequestOutboxRepository outboxRepository;

    private static final String kafkaBootStrapServers = "localhost:19092,localhost:29092,localhost:39092";
    private static final String schemaRegistryUrl = "http://localhost:8081"; // Schema Registry URL 설정
    @Autowired
    private EatsOrderServiceConfigData eatsOrderServiceConfigData;

    @BeforeAll

    @AfterAll
    static void tearDown() {
    }

    @Test
    public void testOutboxPublishesToKafka() throws InterruptedException {
        try (KafkaConsumer<String, RequestAvroModel> consumer = new KafkaConsumer<>(
            ImmutableMap.<String, Object>builder()
                .put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootStrapServers)
                .put(ConsumerConfig.GROUP_ID_CONFIG, "tc-" + UUID.randomUUID())
                .put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
                .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class)
                .put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl)
                .put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true)
                .build()
        )) {
            consumer.subscribe(Collections.singletonList(eatsOrderServiceConfigData.getRestaurantApprovalRequestTopicName()));


            // repository 에 직접 저장해서 publish 되는지 확인
            // Given
            UUID id = UUID.randomUUID();
            UUID sagaId = UUID.randomUUID();
            ZonedDateTime now = ZonedDateTime.now();
            String type = "APPROVAL";
            String payload = "{\"orderId\":\"123\",\"status\":\"PENDING\"}";
            OrderStatus orderStatus = OrderStatus.PENDING;
            SagaStatus sagaStatus = SagaStatus.STARTED;
            OutboxStatus outboxStatus = OutboxStatus.STARTED;
            int version = 1;

            RestaurantApprovalOutboxMessageEntity entity = RestaurantApprovalOutboxMessageEntity.builder()
                .id(id)
                .sagaId(sagaId)
                .createdAt(now)
                .processedAt(now)
                .sagaType(type)
                .payload(payload)
                .orderStatus(orderStatus.name())
                .sagaStatus(sagaStatus.name())
                .outboxStatus(outboxStatus.name())
                .version(version)
                .build();

            // When
            outboxRepository.save(entity);

            // Then
            ConsumerRecords<String, RequestAvroModel> records = consumer.poll(Duration.ofSeconds(10));
            assertNotNull(records);
            assertEquals(1, records.count());

            for (ConsumerRecord<String, RequestAvroModel> record : records) {
                assertEquals(id.toString(), record.key());
                RequestAvroModel value = record.value();
                // Here you would typically deserialize and validate the message content
                assertNotNull(value);
                // Add more specific assertions based on your message format
            }
        }
    }
}