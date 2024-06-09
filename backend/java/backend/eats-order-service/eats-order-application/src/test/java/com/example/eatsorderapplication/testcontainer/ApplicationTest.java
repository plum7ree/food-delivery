package com.example.eatsorderapplication.testcontainer;

import com.example.eatsorderapplication.data.dto.EatsOrderResponseDto;
import com.example.eatsorderconfigdata.EatsOrderServiceConfigData;
import com.example.kafka.config.data.KafkaConsumerConfigData;
import com.example.kafkaconsumer.config.KafkaConsumerConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.internals.Topic;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//Important. 이걸로 setUp 을 non-static 으로 만들고, autowired 한 KafkaConsumerConfig 내부의 빈들을 setUp 에서 사용할수있다.
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({KafkaConsumerConfig.class, ApplicationTest.TestConfig.class})
//@TestPropertySource("classpath:application.yml") // should set profile to avoid multiple applicatino.yml exists
@Profile("test")
@Slf4j
public class ApplicationTest {
    private static final Network network = Network.newNetwork();
    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("db")
            .withUsername("admin")
            .withPassword("1234")
            .withNetwork(network)
            .withExposedPorts(5432);

    // ref: https://github.com/testcontainers/testcontainers-java/blob/main/examples/kafka-cluster/src/test/java/com/example/kafkacluster/KafkaContainerCluster.java

//        this.zookeeper =
//            new GenericContainer<>(DockerImageName.parse("confluentinc/cp-zookeeper").withTag(confluentPlatformVersion))
//                .withNetwork(network)
//                .withNetworkAliases("zookeeper")
//                .withEnv("ZOOKEEPER_CLIENT_PORT", String.valueOf(KafkaContainer.ZOOKEEPER_PORT));
//
//        this.brokers =
//            IntStream
//                .range(0, this.brokersNum)
//                .mapToObj(brokerNum -> {
//                    return new KafkaContainer(
//                        DockerImageName.parse("confluentinc/cp-kafka").withTag(confluentPlatformVersion)
//                    )
//                        .withNetwork(this.network)
//                        .withNetworkAliases("broker-" + brokerNum)
//                        .dependsOn(this.zookeeper)
//                        .withExternalZookeeper("zookeeper:" + KafkaContainer.ZOOKEEPER_PORT)
//                        .withEnv("KAFKA_BROKER_ID", brokerNum + "")
//                        .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", internalTopicsRf + "")
//                        .withEnv("KAFKA_OFFSETS_TOPIC_NUM_PARTITIONS", internalTopicsRf + "")
//                        .withEnv("KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", internalTopicsRf + "")
//                        .withEnv("KAFKA_TRANSACTION_STATE_LOG_MIN_ISR", internalTopicsRf + "")
//                        .withStartupTimeout(Duration.ofMinutes(1));
//                })
//                .collect(Collectors.toList());


//    @Container
//    public static GenericContainer<?> zookeeperContainer = new GenericContainer<>(DockerImageName.parse("confluentinc/cp-zookeeper:latest"))
//            .withNetwork(network)
//            .withNetworkAliases("zookeeper")
//            .withExposedPorts(2181)
//            .withEnv("ZOOKEEPER_SERVER_ID", "1")
//            .withEnv("ZOOKEEPER_CLIENT_PORT", "2181")
//            .withEnv("ZOOKEEPER_TICK_TIME", "2000")
//            .withEnv("ZOOKEEPER_SYNC_LIMIT", "2")
//            .withEnv("ZOOKEEPER_SERVERS", "zookeeper:2888:3888");

    // https://java.testcontainers.org/modules/kafka/
//    @Container
//    public static KafkaContainer kafkaBrokerContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))
//            .withNetwork(network)
//            .withNetworkAliases("kafka");
//            .withExposedPorts(9092, 19092) // Ensure ports are properly exposed
//            .withEnv("KAFKA_BROKER_ID", "1")
//            .withEnv("KAFKA_ZOOKEEPER_CONNECT", "zookeeper:2181")
//            .withEnv("KAFKA_ADVERTISED_LISTENERS", "PLAINTEXT://kafka-broker-1:9092,LISTENER_LOCAL://localhost:19092")
//            .withEnv("KAFKA_LISTENER_SECURITY_PROTOCOL_MAP", "PLAINTEXT:PLAINTEXT,LISTENER_LOCAL:PLAINTEXT")
//            .withEnv("KAFKA_INTER_BROKER_LISTENER_NAME", "PLAINTEXT")
//            .withEnv("KAFKA_COMPRESSION_TYPE", "producer");
//            .dependsOn(zookeeperContainer);
//    @Container
//    public static GenericContainer<?> schemaRegistryContainer = new GenericContainer<>(DockerImageName.parse("confluentinc/cp-schema-registry:latest"))
//            .withNetwork(network)
//            .withNetworkAliases("schema-registry")
//            .withExposedPorts(8081)
//            .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
//            .withEnv("SCHEMA_REGISTRY_KAFKASTORE_CONNECTION_URL", "zookeeper:2181")
//            .withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", "PLAINTEXT://kafka-broker-2:9092,LISTENER_LOCAL://localhost:29092")
//            .withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8081")
//            .dependsOn(zookeeperContainer)
//            .dependsOn(kafkaBrokerContainer);

    private static KafkaContainerCluster cluster;
    private static String kafkaBootStrapServeres = "";
    private static ConcurrentMessageListenerContainer<String, String> listenerContainer;
    private static final CountDownLatch latch = new CountDownLatch(1);
    private static String receivedMessage;
    @Autowired
    @Qualifier("testKafkaListenerContainerFactory")
    private final KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EatsOrderServiceConfigData eatsOrderServiceConfigData;

    @Autowired // @Autowired 명시해서 kafkaListenerContainerFactory 처리.
    public ApplicationTest(KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory) {
        this.kafkaListenerContainerFactory = kafkaListenerContainerFactory;
    }


    private static void startKafkaCluster() {
        try {
            cluster = new KafkaContainerCluster("6.2.1", 3, 2);
            cluster.start();
            kafkaBootStrapServeres = cluster.getBootstrapServers();
            log.info("boot strap servers: {}", kafkaBootStrapServeres);
            assertThat(cluster.getBrokers()).hasSize(3);

            AdminClient adminClient = AdminClient.create(
                ImmutableMap.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootStrapServeres)
            );
            var topicNames = List.of("payment-request-topic", "payment-response-topic", "restaurant-approval-request-topic", "restaurant-approval-response-topic");
            var topics = topicNames.stream().map(topicName -> new NewTopic(topicName, 3, (short) 2)).collect(Collectors.toList());
            adminClient.createTopics(topics).all().get(30, TimeUnit.SECONDS);

        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            log.error(e.getMessage());
        }


    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        //Important. we must start container before accessing getFirstMappedPort(). @BeforeAll is called later than this.
//        Startables.deepStart(postgresContainer, zookeeperContainer, kafkaBrokerContainer, schemaRegistryContainer).join();
        postgresContainer.start();
        startKafkaCluster();

        var datasourceUrl = String.format("jdbc:postgresql://localhost:%d/db?currentSchema=call_schema&binaryTransfer=true&reWriteBatchedInserts=true",
                postgresContainer.getFirstMappedPort());

        registry.add("spring.datasource.url", () -> datasourceUrl);
        registry.add("spring.datasource.username", () -> "admin");
        registry.add("spring.datasource.password", () -> "1234");
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.sql.init.mode", () -> "always");
        registry.add("spring.sql.init.platform", () -> "postgres");
        registry.add("spring.sql.init.schema-locations", () -> "classpath:sql/calls/init-schema.sql");
        registry.add("spring.sql.init.data-locations", () -> "classpath:sql/calls/init-data.sql");

        registry.add("kafka-config.bootstrap-servers", () -> kafkaBootStrapServeres);
        registry.add("kafka-config.schema-registry-url-key", () -> "schema.registry.url");
        registry.add("kafka-config.schema-registry-url", () -> "http://localhost:" + KafkaContainerCluster.schemaRegistryPort);
        registry.add("kafka-config.topic-name", () -> "");
        registry.add("kafka-config.topic-names-to-create", () -> List.of(
            "payment-response-topic",
            "payment-request-topic",
            "restaurant-approval-request-topic",
            "restaurant-approval-response-topic"));
        registry.add("kafka-config.num-of-partitions", () -> 3);
        registry.add("kafka-config.replication-factor", () -> 3);
//registry.add("kafka.properties.schemaRegistryUrl", () ->
//    { "http://${schemaRegistryContainer.host}:${schemaRegistryContainer.firstMappedPort}" }
    }

    @AfterAll
    public static void tearDown() {
        postgresContainer.stop();
    }

    @BeforeAll // run after spring boot app context loaded
    public void setUp() {

    }

//    @BeforeAll // if you want non-static. do it @BeforeEach. @BeforeAll will initiate only once for entire tests.
//    public void setUp() {
//        postgresContainer.start();
//        kafkaContainer.start();
//
////        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", kafkaContainer.getBootstrapServers());
////        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
////        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "testGroup");
////        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
////        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
////        DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), new StringDeserializer());
//
////        ContainerProperties containerProperties = new ContainerProperties("eatsOrderTopic");
//        log.info("kafka config data: {}", kafkaConfigData.getTopicName());
//        ContainerProperties containerProperties = new ContainerProperties("eatsOrderTopic");
//        listenerContainer = kafkaListenerContainerFactory.createContainer(Objects.requireNonNull(containerProperties.getTopicPattern()));
//        listenerContainer.setupMessageListener((MessageListener<String, String>) record -> {
//            receivedMessage = record.value();
//            latch.countDown();
//        });
//        listenerContainer.start();
//    }

    @Test
    public void testPostRequestToEatsOrder() throws InterruptedException, JsonProcessingException {
        // ref: Registered Lsiterner https://java.testcontainers.org/modules/kafka/
//        KafkaContainer kafka = new KafkaContainer(KAFKA_KRAFT_TEST_IMAGE)
//            .withListener(() -> "kafka:19092")
//            .withNetwork(network);

//        listenerContainer = kafkaListenerContainerFactory.createContainer(eatsOrderServiceConfigData.getRestaurantApprovalRequestTopicName());
//        listenerContainer.setupMessageListener((MessageListener<String, String>) record -> {
//            log.error("listenerContainer message received!");
//            receivedMessage = record.value();
//            latch.countDown();
//        });
//        listenerContainer.start();
        try (
//            AdminClient adminClient = AdminClient.create(
//                ImmutableMap.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootStrapServeres)
//            );
            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(
                ImmutableMap.of(
                    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                    kafkaBootStrapServeres,
                    ConsumerConfig.GROUP_ID_CONFIG,
                    "tc-" + UUID.randomUUID(),
                    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                    "earliest"
                ),
                new StringDeserializer(),
                new StringDeserializer()
            );
        ) {
            Collection<NewTopic> topics = Collections.singletonList(new NewTopic(eatsOrderServiceConfigData.getRestaurantApprovalRequestTopicName(), 3, (short) 2));
            consumer.subscribe(Collections.singletonList(eatsOrderServiceConfigData.getRestaurantApprovalRequestTopicName()));

        }
        // Given
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // given
        UUID userId = UUID.fromString("d290f1ee-6c54-4b01-90e6-d701748f0851");
        UUID driverId = UUID.fromString("c240a1ee-6c54-4b01-90e6-d701748f0852");
        BigDecimal price = new BigDecimal("100.50");
        var street = "123 Main St";
        var postalCode = "12345";
        var city = "City";
        String address = String.format("""
                {
                    "street": "%s",
                    "postalCode": "%s",
                    "city": "%s"
                }
                """,
            street,
            postalCode,
            city);
        String addressJson = objectMapper.writeValueAsString(address);

        // adding " in "%s" is important!
        String jsonPayload = String.format("""
                {
                    "userId": "%s",
                    "driverId": "%s",
                    "price": %f,
                    "address": %s,
                    "payment": null,
                    "route": null
                }
                """,
            userId,
            driverId,
            price,
            address);

        HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);

        // When
        ResponseEntity<EatsOrderResponseDto> response = restTemplate.postForEntity("/api/eatsorder", entity, EatsOrderResponseDto.class);
        Thread.sleep(1000000);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());
        assertEquals("Order created successfully", response.getBody().getMessage());

        // Kafka verification
        boolean messageConsumed = latch.await(10, TimeUnit.SECONDS);
        assertTrue(messageConsumed);
        assertNotNull(receivedMessage);
//        assertEquals(requestBody, receivedMessage);
    }

    @Test
    public void testPostRequestToTest() {
        // Given
        HttpEntity<String> entity = new HttpEntity<>(null, null);

        // When
        ResponseEntity<String> response = restTemplate.postForEntity("/api/test", entity, String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("done", response.getBody());
    }

    @TestConfiguration
    @RequiredArgsConstructor
    static class TestConfig {

        @Autowired
        @Qualifier("commonKafkaConsumerConfigs") //WARNING. qualifier 는 map 으로 되어있으면, bean name 을 한번 key 로 등록시켜버림.
        private final Map<String, Object> commonConsumerConfigs;

        @Autowired
        private final KafkaConsumerConfigData kafkaConsumerConfigData;

        @Value("${kafka-consumer-group-id.payment-consumer-group-id}")
        private String paymentConsumerGroupId;

        @Bean(name = "testKafkaListenerContainerFactory")
        public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
            Map<String, Object> props = (HashMap<String, Object>) commonConsumerConfigs.get("commonKafkaConsumerConfigs");
            props.keySet().forEach(log::info);
            props.put(ConsumerConfig.GROUP_ID_CONFIG, paymentConsumerGroupId);
            var consumerFactory = new DefaultKafkaConsumerFactory<>(props);
            ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(consumerFactory);
            factory.setBatchListener(kafkaConsumerConfigData.getBatchListener());
            factory.setConcurrency(kafkaConsumerConfigData.getConcurrencyLevel());
            factory.setAutoStartup(kafkaConsumerConfigData.getAutoStartup());
            factory.getContainerProperties().setPollTimeout(kafkaConsumerConfigData.getPollTimeoutMs());
            return factory;
        }
    }
}
