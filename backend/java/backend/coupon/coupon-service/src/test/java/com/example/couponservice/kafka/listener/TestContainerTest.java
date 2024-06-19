// ref: https://medium.com/@igorvlahek1/no-need-for-schema-registry-in-your-spring-kafka-tests-a5b81468a0e1
// ref: https://github.com/ivlahek/kafka-avro-without-registry
package com.example.couponservice.kafka.listener;

import com.example.couponservice.entity.Coupon;
import com.example.couponservice.repository.CouponIssueRepository;
import com.example.couponservice.repository.CouponRepository;
import com.example.kafka.avro.model.CouponIssueRequestAvroModel;
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.Schema;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsResult;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@EmbeddedKafka(partitions = 1, topics = {"coupon-issue-request-topic"})
@Transactional
class TestContainerTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;


    private KafkaTemplate<String, CouponIssueRequestAvroModel> kafkaTemplate;
    private AdminClient adminClient;

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.consumer.key-deserializer", () -> CustomKafkaAvroDeserializer.class.getName());
        registry.add("spring.kafka.consumer.value-deserializer", () -> CustomKafkaAvroDeserializer.class.getName());
        registry.add("spring.kafka.producer.key-serializer", () -> CustomKafkaAvroSerializer.class.getName());
        registry.add("spring.kafka.producer.value-serializer", () -> CustomKafkaAvroSerializer.class.getName());
    }

    @BeforeEach
    void setUp() {
        // KafkaTemplate 설정
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        producerProps.put("key.serializer", StringSerializer.class);
        producerProps.put("value.serializer", CustomKafkaAvroSerializer.class);
        kafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerProps));

        // AdminClient 설정
        Map<String, Object> adminProps = new HashMap<>(KafkaTestUtils.consumerProps("testGroup", "false", embeddedKafkaBroker));
        adminProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString());
        adminClient = AdminClient.create(adminProps);
    }

    @Test
    void testOffsetCommitBehavior() throws ExecutionException, InterruptedException {
        // Given
        var message = new CouponIssueRequestAvroModel();
        message.setCouponId(1L);
        message.setIssueId(1L);

//        // 첫 번째 메시지 처리 시 예외 발생
//        doThrow(new RuntimeException("Mocked Exception")).when(couponRepository).findById(1L);
//        // 두 번째 메시지 처리 시 예외 발생하지 않음
//        doReturn(Optional.of(new Coupon())).when(couponRepository).findById(1L);

        // Send the first message to the topic
        kafkaTemplate.send("coupon-issue-request-topic", message).get();

//        // Poll the message to simulate receiving by listener
//        MessageListenerContainer listenerContainer = kafkaListenerEndpointRegistry.getListenerContainer("coupon-issue-consumer-group-id");
//        listenerContainer.stop(); // Stop the listener container to avoid auto ack
//        KafkaTestUtils.getRecords((KafkaConsumer<?, ?>) listenerContainer.getAssignedConsumer());
//
//        // When
//        listenerContainer.start();

//        // Allow some time for the listener to process
//        Thread.sleep(2000);
//
//        // Check the consumer group offsets after the first failure
//        ListConsumerGroupOffsetsResult offsetsResult = adminClient.listConsumerGroupOffsets("coupon-issue-consumer-group-id");
//        Map<TopicPartition, Long> offsets = offsetsResult.partitionsToOffsetAndMetadata().get().entrySet().stream()
//            .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue().offset()), HashMap::putAll);
//        assertEquals(0L, offsets.get(new TopicPartition("coupon-issue-request-topic", 0)));
//
//        // Reset mocks for the second message
//        reset(couponRepository);
//
//        // Send the second message to the topic
//        kafkaTemplate.send("coupon-issue-request-topic", message).get();
//
//        // Poll the message to simulate receiving by listener
//        KafkaTestUtils.getRecords((KafkaConsumer<?, ?>) listenerContainer.getAssignedConsumer());
//
//        // Allow some time for the listener to process
//        Thread.sleep(2000);
//
//        // Check the consumer group offsets after the second successful processing
//        offsetsResult = adminClient.listConsumerGroupOffsets("coupon-issue-consumer-group-id");
//        offsets = offsetsResult.partitionsToOffsetAndMetadata().get().entrySet().stream()
//            .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue().offset()), HashMap::putAll);
//        assertEquals(1L, offsets.get(new TopicPartition("coupon-issue-request-topic", 0)));
    }

    class CustomKafkaAvroSerializer extends KafkaAvroSerializer {
        public CustomKafkaAvroSerializer() {
            super();
            super.schemaRegistry = new MockSchemaRegistryClient();
        }

        public CustomKafkaAvroSerializer(SchemaRegistryClient client) {
            super(new MockSchemaRegistryClient());
        }

        public CustomKafkaAvroSerializer(SchemaRegistryClient client, Map<String, ?> props) {
            super(new MockSchemaRegistryClient(), props);
        }
    }

    class CustomKafkaAvroDeserializer extends KafkaAvroDeserializer {
        private static SchemaRegistryClient getMockClient(final Schema schema$) {
            return new MockSchemaRegistryClient() {
                @Override
                public synchronized Schema getById(int id) {
                    return schema$;
                }
            };
        }

        @Override
        public Object deserialize(String topic, byte[] bytes) {
            if (topic.equals("coupon-issue-request")) {
                this.schemaRegistry = getMockClient(CouponIssueRequestAvroModel.SCHEMA$);
            }
            return super.deserialize(topic, bytes);
        }
    }
}
