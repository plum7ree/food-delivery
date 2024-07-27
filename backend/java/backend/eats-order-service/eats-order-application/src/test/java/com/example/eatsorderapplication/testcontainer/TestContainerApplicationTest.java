/**
 * TODO for now, since schema registry testcontainer doesn't work well,
 *  please manually run [project root]/docker/start-kafka-cluster.sh, start-postgres.sh
 *  see the topics published with kafka ui. http://localhost:9000/
 */
package com.example.eatsorderapplication.testcontainer;

import com.example.commonutil.DockerComposeStarter;
import com.example.eatsorderapplication.data.dto.EatsOrderResponseDto;
import com.example.eatsorderapplication.utils.TestDataGenerator;
import com.example.kafka.avro.model.RequestAvroModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableMap;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // random port 이기 때문ㅇ에 8074 가 아닐수있다.
@ActiveProfiles("test")
@Slf4j
public class TestContainerApplicationTest {
    private static final String DOCKER_COMPOSE_FILE_PATH = ClassLoader.getSystemResource("docker-compose-test.yml").getPath();

    private static DockerComposeStarter dockerComposeStarter;
    @Value("${topic-names.restaurant-approval-request-topic-name}")
    String restaurantApprovalRequestTopicName;

    @Value("${kafka-config.bootstrap-servers}")
    private String kafkaBootStrapServers;
    private static final String schemaRegistryUrl = "http://localhost:8081"; // Schema Registry URL 설정

    @LocalServerPort
    private int localPort;
    private String appUrl;

    private static final RestTemplate restTemplate = new RestTemplate();

    static {
        dockerComposeStarter = new DockerComposeStarter(DOCKER_COMPOSE_FILE_PATH);

        // Start PostgreSQL
        try {
            dockerComposeStarter.startServiceAndWaitForLog("order-db", ".*ready to accept connections.*", 5, TimeUnit.MINUTES);
            dockerComposeStarter.startServiceAndWaitForLog("user-db", ".*ready to accept connections.*", 5, TimeUnit.MINUTES);

            dockerComposeStarter.startServiceAndWaitForLog("zookeeper", ".*started.*", 5, TimeUnit.MINUTES);

            // Start Kafka
            dockerComposeStarter.startServiceAndWaitForLog("kafka-broker-1", ".*started.*", 5, TimeUnit.MINUTES);
            dockerComposeStarter.startServiceAndWaitForLog("kafka-broker-2", ".*started.*", 5, TimeUnit.MINUTES);
            dockerComposeStarter.startServiceAndWaitForLog("kafka-broker-3", ".*started.*", 5, TimeUnit.MINUTES);

            dockerComposeStarter.startServiceAndWaitForLog("schema-registry", ".*Cluster ID.*", 5, TimeUnit.MINUTES);

            dockerComposeStarter.startServiceAndWaitForLog("configserver-app", ".*Started.*", 5, TimeUnit.MINUTES);

            dockerComposeStarter.startServiceAndWaitForLog("user-app", ".*Started.*", 5, TimeUnit.MINUTES);
            Thread.sleep(5000);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    @PostConstruct
    public void init() {
        appUrl = "http://localhost:" + localPort;
    }

    @AfterAll
    public static void tearDown() throws Exception {
        if (dockerComposeStarter != null) {
            dockerComposeStarter.stopAllServices();
        }
    }

    @Test
    public void getPort() {
        assertTrue(localPort > 0);
    }

    /**
     * /api/eatsorder 로 요청 보내면, sagaStatus PENDING 을 가지고 요청 보내야함
     *
     * @throws InterruptedException
     * @throws JsonProcessingException
     */
    @Test
    public void whenOrderRequestThenKafkaProducesMessage() throws InterruptedException, JsonProcessingException {

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
            Collection<NewTopic> topics = Collections.singletonList(new NewTopic(restaurantApprovalRequestTopicName, 3, (short) 2));
            consumer.subscribe(Collections.singletonList(restaurantApprovalRequestTopicName));

            // 추가: 현재 offset 확인
            var partitionOffsets = consumer.endOffsets(consumer.assignment());
            var currentOffset = partitionOffsets.values().stream().mapToLong(v -> v).sum();

            // Given
            TestDataGenerator.TestData testData = TestDataGenerator.generateTestData();

            HttpEntity<String> entity = new HttpEntity<>(testData.jsonPayload, testData.headers);

            // When
            ResponseEntity<EatsOrderResponseDto> response = restTemplate.postForEntity(appUrl + "/api/eatsorder", entity, EatsOrderResponseDto.class);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());

            assertNotNull(response.getBody());
            assertEquals("PENDING", response.getBody().getOrderStatus().toString());
            assertNotNull(response.getBody().getCallTrackingId());

            // 추가: 컨슈머 폴링 및 Avro 메시지 소비
            var records = consumer.poll(Duration.ofSeconds(1000));
            RequestAvroModel paymentRequest = null;
            for (var record : records) {
                paymentRequest = record.value();
                log.info("Consumed PaymentRequestAvroModel: {}", paymentRequest);
            }
            consumer.commitSync();
            assertNotNull(paymentRequest);

            partitionOffsets = consumer.endOffsets(consumer.assignment());
            var newOffset = partitionOffsets.values().stream().mapToLong(v -> v).sum();
            var messageCount = newOffset - currentOffset;
            assertEquals(1, messageCount, "Producer should send only one message");
        }
    }

    /**
     * /api/cancelorder 로 요청 보내면 saga status CANCELED 로 요청 보내야함.
     */
    @Test
    public void whenCancelOrderThenKafkaProducesCancelMessage() {

    }

    @Test
    public void testPostRequestToTest() {
        // Given
        HttpEntity<String> entity = new HttpEntity<>(null, null);

        // When
        // Connection Refused error 가 왜 뜨지??
        ResponseEntity<String> response = restTemplate.postForEntity(appUrl + "/api/test", entity, String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("done", response.getBody());
    }

}
