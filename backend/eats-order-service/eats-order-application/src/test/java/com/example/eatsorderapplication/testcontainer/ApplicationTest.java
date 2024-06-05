package com.example.eatsorderapplication.testcontainer;

import com.example.eatsorderapplication.data.dto.EatsOrderResponseDto;
import com.example.kafka.config.data.KafkaConsumerConfigData;
import com.example.kafkaconsumer.config.KafkaConsumerConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
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
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
    @Container
    public static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))
            .withNetwork(network)
            .withNetworkAliases("kafka");
    private static ConcurrentMessageListenerContainer<String, String> listenerContainer;
    private static final CountDownLatch latch = new CountDownLatch(1);
    private static String receivedMessage;
    @Autowired
    @Qualifier("testKafkaListenerContainerFactory")
    private final KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory;
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    public ApplicationTest(KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory, TestRestTemplate restTemplate) {
        this.kafkaListenerContainerFactory = kafkaListenerContainerFactory;
        this.restTemplate = restTemplate;
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        //Important. we must start container before accessing getFirstMappedPort(). @BeforeAll is called later than this.
        postgresContainer.start();
        kafkaContainer.start();

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

        registry.add("spring.kafka.bootstrap-servers", () -> kafkaContainer.getBootstrapServers());
    }

    @AfterAll
    public static void tearDown() {
        kafkaContainer.stop();
        postgresContainer.stop();
        listenerContainer.stop();
    }

    @BeforeAll // run after spring boot app context loaded
    public void setUp() {


        ContainerProperties containerProperties = new ContainerProperties("eatsOrderTopic");
        listenerContainer = kafkaListenerContainerFactory.createContainer("driver-approval-request-topic");
        listenerContainer.setupMessageListener((MessageListener<String, String>) record -> {
            receivedMessage = record.value();
            latch.countDown();
        });
        listenerContainer.start();
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
    public void testPostRequestToEatsOrder() throws InterruptedException {
        // Given
        String requestBody = "{ \"orderId\": \"123\", \"orderDetails\": \"Sample order details\" }"; // JSON 형태의 요청 바디
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // When
        ResponseEntity<EatsOrderResponseDto> response = restTemplate.postForEntity("/api/eatsorder", entity, EatsOrderResponseDto.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Order created successfully", response.getBody().getMessage());

        // Kafka verification
        boolean messageConsumed = latch.await(10, TimeUnit.SECONDS);
        assertTrue(messageConsumed);
        assertNotNull(receivedMessage);
        assertEquals(requestBody, receivedMessage);
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
