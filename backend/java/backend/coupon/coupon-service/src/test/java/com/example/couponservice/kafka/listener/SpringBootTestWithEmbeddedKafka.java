// ref: https://medium.com/@igorvlahek1/no-need-for-schema-registry-in-your-spring-kafka-tests-a5b81468a0e1
// ref: https://github.com/ivlahek/kafka-avro-without-registry
package com.example.couponservice.kafka.listener;

import com.example.couponservice.entity.Coupon;
import com.example.couponservice.entity.CouponIssue;
import com.example.couponservice.repository.CouponIssueRepository;
import com.example.couponservice.repository.CouponRepository;
import com.example.kafka.avro.model.CouponIssueRequestAvroModel;
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsResult;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ExtendWith(MockitoExtension.class)
@EmbeddedKafka(partitions = 1, topics = {"coupon-issue-request-topic"}, brokerProperties = "listeners=PLAINTEXT://localhost:49092")
@ActiveProfiles("test") // application-test.yml
@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SpringBootTestWithEmbeddedKafka {
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    private AdminClient adminClient;
    private KafkaTemplate<String, CouponIssueRequestAvroModel> kafkaTemplate;
    @MockBean
    private CouponRepository couponRepository;
    @MockBean
    private CouponIssueRepository couponIssueRepository;
    @Value("${kafka-consumer-group-id.coupon-issue-request-consumer-group-id}")
    private String consumerGroupId;
    @Value("${topic-names.coupon-issue-request-topic-name}")
    private String topicName;
    @Value("${kafka-listener-id}")
    private String listenerId;

    @BeforeEach
    void setUp() {
        // KafkaTemplate 설정
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        producerProps.put("key.serializer", StringSerializer.class);
        producerProps.put("value.serializer", KafkaAvroSerializer.class);
        producerProps.put("schema.registry.url", "mock://test-uri");
        kafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerProps));

        // AdminClient 설정
        Map<String, Object> adminProps = new HashMap<>(KafkaTestUtils.consumerProps("testGroup", "false", embeddedKafkaBroker));
        adminProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString());
        adminClient = AdminClient.create(adminProps);

    }


    @Test
    void givenRepositoryThrowsError_whenKafkaListenerReceives_thenOffsetShouldNotBeCommitted() throws ExecutionException, InterruptedException {
        // Given
        var message = new CouponIssueRequestAvroModel();
        message.setCallerId(UUID.randomUUID().toString());
        message.setCouponId(1L);
        message.setIssueId(1L);
        message.setAmount(1000);
        message.setCreatedAt(Instant.from(ZonedDateTime.now()));

        // 첫 번째 메시지 처리 시 예외 발생
        doThrow(new RuntimeException("Mocked Exception")).when(couponRepository).findById(1L);
        doThrow(new RuntimeException("Mocked Exception")).when(couponIssueRepository).findById(1L);

        // Send the first message to the topic
        kafkaTemplate.send(topicName, "partition-key", message).get();

        // Allow some time for the listener to process
        Thread.sleep(1000);


        // Check the consumer group offsets after the first failure
        ListConsumerGroupOffsetsResult offsetsResult = adminClient.listConsumerGroupOffsets(consumerGroupId);
        Map<TopicPartition, Long> offsets = offsetsResult.partitionsToOffsetAndMetadata().get().entrySet().stream()
            .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue().offset()), HashMap::putAll);
        assertEquals(0L, offsets.get(new TopicPartition(topicName, 0)));

    }

    @Test
    void givenRepositoryFirstThrowsErrorAndSecondNotThrowsError_whenKafkaListenerReceives_thenOffsetShouldBeCommitted() throws ExecutionException, InterruptedException {
        // Given
        var message = new CouponIssueRequestAvroModel();
        message.setCallerId(UUID.randomUUID().toString());
        message.setCouponId(1L);
        message.setIssueId(1L);
        message.setAmount(1000);
        message.setCreatedAt(Instant.from(ZonedDateTime.now()));

        // 첫 번째 메시지 처리 시 예외 발생
//        doThrow(new RuntimeException("Mocked Exception")).when(couponRepository).findById(1L);
//        doThrow(new RuntimeException("Mocked Exception")).when(couponIssueRepository).findById(1L);
//        // 두 번째 메시지 처리 시 예외 발생하지 않음
        doReturn(Optional.of(new Coupon())).when(couponRepository).findById(1L);
        doReturn(Optional.of(new CouponIssue())).when(couponIssueRepository).findById(1L);

        // Send the first message to the topic
        kafkaTemplate.send(topicName, "partition-key", message).get();

        // Allow some time for the listener to process
        Thread.sleep(1000);


        // Check the consumer group offsets after the first failure
        ListConsumerGroupOffsetsResult offsetsResult = adminClient.listConsumerGroupOffsets(consumerGroupId);
        Map<TopicPartition, Long> offsets = offsetsResult.partitionsToOffsetAndMetadata().get().entrySet().stream()
            .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue().offset()), HashMap::putAll);
        assertEquals(1L, offsets.get(new TopicPartition(topicName, 0)));

    }

    @Test
    void whenKafkaListenerReceives_thenRepositorySaveMethodsShouldBeCalled() throws ExecutionException, InterruptedException {
        // Given
        var message = new CouponIssueRequestAvroModel();
        message.setCallerId(UUID.randomUUID().toString());
        message.setCouponId(1000000L);
        message.setIssueId(1L);
        message.setAmount(1000);
        message.setCreatedAt(Instant.from(ZonedDateTime.now()));

        Coupon mockCoupon = new Coupon();
        mockCoupon.setId(1L);
        mockCoupon.setIssuedQuantity(0L);
        mockCoupon.setMaxQuantity(100L);

        // Mock repository responses
        when(couponRepository.findById(1L)).thenReturn(Optional.of(mockCoupon));
        when(couponIssueRepository.findById(1L)).thenReturn(Optional.empty());

        // Send the message to the topic
        kafkaTemplate.send(topicName, "partition-key", message).get();

        // Allow some time for the listener to process
        Thread.sleep(1000);

        // Verify that save methods were called
        ArgumentCaptor<Coupon> couponCaptor = ArgumentCaptor.forClass(Coupon.class);
        verify(couponRepository).save(couponCaptor.capture());
        Coupon savedCoupon = couponCaptor.getValue();
        assertEquals(1L, savedCoupon.getIssuedQuantity());

        ArgumentCaptor<CouponIssue> couponIssueCaptor = ArgumentCaptor.forClass(CouponIssue.class);
        verify(couponIssueRepository).save(couponIssueCaptor.capture());
        CouponIssue savedCouponIssue = couponIssueCaptor.getValue();
        assertEquals(1L, savedCouponIssue.getCouponId());
        assertEquals(UUID.fromString(message.getCallerId().toString()), savedCouponIssue.getMemberId());
        assertEquals("NOT_ACTIVE", savedCouponIssue.getCouponStatus());
    }


    @Configuration
    public class TestConfig {

        @Bean
        @Primary
        public SchemaRegistryClient schemaRegistryClient() {
            return new MockSchemaRegistryClient();
        }
    }
}
