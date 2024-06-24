package com.example.eatsorderapplication.testcontainer;

import com.example.eatsorderapplication.data.dto.EatsOrderResponseDto;
import com.example.eatsorderconfigdata.EatsOrderServiceConfigData;
import com.example.kafka.avro.model.RequestAvroModel;
import com.example.kafka.config.data.KafkaConsumerConfigData;
import com.example.kafkaconsumer.config.KafkaConsumerConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.serialization.StringDeserializer;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.sql.Statement;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//Important. 이걸로 setUp 을 non-static 으로 만들고, autowired 한 KafkaConsumerConfig 내부의 빈들을 setUp 에서 사용할수있다.
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({KafkaConsumerConfig.class, ManualKafkaAndPostgresTestContainerApplicationTest.TestConfig.class})
//@TestPropertySource("classpath:application.yml") // should set profile to avoid multiple applicatino.yml exists
@Profile("test")
@Slf4j
public class ManualKafkaAndPostgresTestContainerApplicationTest {
    private static final Network network = Network.newNetwork();
    private static final String kafkaBootStrapServeres = "localhost:19092,localhost:29092,localhost:39092";
    private static final List<String> topics = List.of(
        "payment-response",
        "payment-request",
        "restaurant-approval-request",
        "restaurant-approval-response");


    @Container
    public static GenericContainer<?> postgresContainer = new GenericContainer<>("debezium/example-postgres")
        .withEnv("POSTGRES_USER", "postgres")
        .withEnv("POSTGRES_PASSWORD", "admin")
        .withEnv("POSTGRES_DB", "postgres")
        .withExposedPorts(5432)
        .withCommand("postgres", "-c", "max_connections=200", "-c", "max_replication_slots=4")
        .withCreateContainerCmdModifier(cmd -> cmd.withName("postgres"))
        .withNetworkMode("kafka_global-my-network");


    private static KafkaContainerCluster cluster;
    private static ConcurrentMessageListenerContainer<String, String> listenerContainer;
    private static String receivedMessage;
    private static AdminClient adminClient;

    @Autowired
    @Qualifier("testKafkaListenerContainerFactory")
    private final KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EatsOrderServiceConfigData eatsOrderServiceConfigData;

    //TODO for now, since schema registry testcontainer doesn't work well,
    // please manually run kafka-cluster.yml
    @Autowired // @Autowired 명시해서 kafkaListenerContainerFactory 처리.
    public ManualKafkaAndPostgresTestContainerApplicationTest(KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory) {
        this.kafkaListenerContainerFactory = kafkaListenerContainerFactory;
    }


//    private static void startKafkaCluster() {
//        try {
//            cluster = new KafkaContainerCluster("6.2.1", 3, 2);
//            cluster.start();
//            kafkaBootStrapServeres = cluster.getBootstrapServers();
//            log.info("boot strap servers: {}", kafkaBootStrapServeres);
//            assertThat(cluster.getBrokers()).hasSize(3);
//
//        } catch (ExecutionException | InterruptedException | TimeoutException e) {
//            log.error(e.getMessage());
//        }
//    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        // Important. we must start container before accessing getFirstMappedPort(). @BeforeAll is called later than this.
        Startables.deepStart(postgresContainer).join();
        // startKafkaCluster();

        var datasourceUrl = String.format("jdbc:postgresql://localhost:%d/postgres?currentSchema=order&binaryTransfer=true&reWriteBatchedInserts=true&stringtype=unspecified",
            postgresContainer.getFirstMappedPort());

        registry.add("spring.datasource.url", () -> datasourceUrl);
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> "admin");
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.sql.init.mode", () -> "always");
        registry.add("spring.sql.init.platform", () -> "postgres");
        registry.add("spring.sql.init.schema-locations", () -> "classpath:sql/orders/init-schema.sql");
        registry.add("spring.sql.init.data-locations", () -> "classpath:sql/orders/init-data.sql");

        registry.add("kafka-config.bootstrap-servers", () -> kafkaBootStrapServeres);
        registry.add("kafka-config.schema-registry-url-key", () -> "schema.registry.url");
        // registry.add("kafka-config.schema-registry-url", () -> "http://schema-registry:8081");
        registry.add("kafka-config.schema-registry-url", () -> "http://localhost:8081");
        registry.add("kafka-config.topic-name", () -> "");
        registry.add("kafka-config.topic-names-to-create", () -> topics);
        registry.add("kafka-config.num-of-partitions", () -> 3);
        registry.add("kafka-config.replication-factor", () -> 3);
    }

    @AfterAll
    public static void tearDown() {
        postgresContainer.stop();
    }

    @BeforeAll // run after spring boot app context loaded
    public void setUp() {
        try {
            // PostgreSQL 컨테이너의 정보를 사용하여 DataSource 설정
            String datasourceUrl = String.format("jdbc:postgresql://localhost:%d/postgres?currentSchema=order&binaryTransfer=true&reWriteBatchedInserts=true",
                postgresContainer.getFirstMappedPort());
            String username = "postgres";
            String password = "admin";

            // DriverManagerDataSource를 사용하여 DataSource 생성
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("org.postgresql.Driver");
            dataSource.setUrl(datasourceUrl);
            dataSource.setUsername(username);
            dataSource.setPassword(password);

            // ResourceDatabasePopulator를 사용하여 SQL 파일 로드
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("init-schema.sql"));
            populator.execute(dataSource);
            try (Connection connection = dataSource.getConnection()) {
                DatabaseMetaData metaData = connection.getMetaData();
                ResultSet resultSet = metaData.getSchemas();

                // 1. schema exists check
                boolean schemaExists = false;
                while (resultSet.next()) {
                    String schemaName = resultSet.getString("TABLE_SCHEM");
                    if ("order".equals(schemaName)) {
                        schemaExists = true;
                        break;
                    }
                }

                assertTrue(schemaExists, "order schema should exist");

                // 2. order table exsists check
                List<String> tableNames = new ArrayList<>();
                resultSet = metaData.getTables(null, "order", null, new String[]{"TABLE"});
                while (resultSet.next()) {
                    tableNames.add(resultSet.getString("TABLE_NAME"));
                }

                // 2.5. column name and types
                String tableName = "orders";
                resultSet = metaData.getColumns(null, "order", tableName, null);

                System.out.println("Columns in table " + tableName + ":");
                while (resultSet.next()) {
                    String columnName = resultSet.getString("COLUMN_NAME");
                    String columnType = resultSet.getString("TYPE_NAME");
                    System.out.println(columnName + " - " + columnType);
                }


                // 3. insert check
                Statement statement = connection.createStatement();
                String insertDataSql = "INSERT INTO payment_outbox(id, saga_id, created_at, type, payload, outbox_status, saga_status, order_status, version) " +
                    "VALUES ('8904808e-286f-449b-9b56-b63ba8351cf2', '15a497c1-0f4b-4eff-b9f4-c402c8c07afa', current_timestamp, 'OrderProcessingSaga', " +
                    "'{\"price\": 100, \"orderId\": \"ef471dac-ec22-43a7-a3f4-9d04195567a5\", \"createdAt\": \"2022-01-07T16:21:42.917756+01:00\", " +
                    "\"customerId\": \"d215b5f8-0249-4dc5-89a3-51fd148cfb41\", \"paymentOrderStatus\": \"PENDING\"}', " +
                    "'STARTED', 'STARTED', 'PENDING', 0);";
                statement.execute(insertDataSql);
            } catch (SQLException e) {
                fail("Failed to check order schema existence or insert: " + e.getMessage());
            }

        } catch (Exception e) {
            fail(e.getMessage());
        }


        try {
            adminClient = AdminClient.create(

                ImmutableMap.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootStrapServeres)
            );

        } catch (Exception e) {
            fail(e.getMessage());
        }

        try {
            KafkaFuture<Void> deleteFutures = adminClient.deleteTopics(topics).all();
            deleteFutures.get();
        } catch (Exception e) {
            log.warn(e.getMessage());
        }

        try {
//            HttpClient client = HttpClient.newHttpClient();

            String[] requests = {
                "{" +
                    "\"name\": \"order-payment-connector\"," +
                    "\"config\": {" +
                    "\"connector.class\": \"io.debezium.connector.postgresql.PostgresConnector\"," +
                    "\"tasks.max\": \"1\"," +
                    "\"database.hostname\": \"postgres\"," +
                    "\"database.port\": \"5432\"," +
                    "\"database.user\": \"postgres\"," +
                    "\"database.password\": \"admin\"," +
                    "\"database.dbname\" : \"postgres\"," +
                    "\"database.server.name\": \"PostgreSQL-15\"," +
                    "\"table.include.list\": \"order.payment_outbox\"," +
                    "\"topic.prefix\": \"debezium\"," +
                    "\"tombstones.on.delete\" : \"false\"," +
                    "\"slot.name\" : \"order_payment_outbox_slot\"," +
                    "\"plugin.name\": \"pgoutput\"," +
                    "\"auto.create.topics.enable\": false," +
                    "\"auto.register.schemas\": false" +
                    "}" +
                    "}",
                "{" +
                    "\"name\": \"order-restaurant-connector\"," +
                    "\"config\": {" +
                    "\"connector.class\": \"io.debezium.connector.postgresql.PostgresConnector\"," +
                    "\"tasks.max\": \"1\"," +
                    "\"database.hostname\": \"postgres\"," +
                    "\"database.port\": \"5432\"," +
                    "\"database.user\": \"postgres\"," +
                    "\"database.password\": \"admin\"," +
                    "\"database.dbname\" : \"postgres\"," +
                    "\"database.server.name\": \"PostgreSQL-15\"," +
                    "\"table.include.list\": \"order.restaurant_approval_outbox\"," +
                    "\"topic.prefix\": \"debezium\"," +
                    "\"tombstones.on.delete\" : \"false\"," +
                    "\"slot.name\" : \"order_restaurant_approval_outbox_slot\"," +
                    "\"plugin.name\": \"pgoutput\"," +
                    "\"auto.create.topics.enable\": false," +
                    "\"auto.register.schemas\": false" +
                    "}" +
                    "}",
                "{" +
                    "\"name\": \"payment-order-connector\"," +
                    "\"config\": {" +
                    "\"connector.class\": \"io.debezium.connector.postgresql.PostgresConnector\"," +
                    "\"tasks.max\": \"1\"," +
                    "\"database.hostname\": \"postgres\"," +
                    "\"database.port\": \"5432\"," +
                    "\"database.user\": \"postgres\"," +
                    "\"database.password\": \"admin\"," +
                    "\"database.dbname\" : \"postgres\"," +
                    "\"database.server.name\": \"PostgreSQL-15\"," +
                    "\"table.include.list\": \"payment.order_outbox\"," +
                    "\"topic.prefix\": \"debezium\"," +
                    "\"tombstones.on.delete\" : \"false\"," +
                    "\"slot.name\" : \"payment_order_outbox_slot\"," +
                    "\"plugin.name\": \"pgoutput\"," +
                    "\"auto.create.topics.enable\": false," +
                    "\"auto.register.schemas\": false" +
                    "}" +
                    "}",
                "{" +
                    "\"name\": \"restaurant-order-connector\"," +
                    "\"config\": {" +
                    "\"connector.class\": \"io.debezium.connector.postgresql.PostgresConnector\"," +
                    "\"tasks.max\": \"1\"," +
                    "\"database.hostname\": \"postgres\"," +
                    "\"database.port\": \"5432\"," +
                    "\"database.user\": \"postgres\"," +
                    "\"database.password\": \"admin\"," +
                    "\"database.dbname\" : \"postgres\"," +
                    "\"database.server.name\": \"PostgreSQL-15\"," +
                    "\"table.include.list\": \"restaurant.order_outbox\"," +
                    "\"topic.prefix\": \"debezium\"," +
                    "\"tombstones.on.delete\" : \"false\"," +
                    "\"slot.name\" : \"restaurant_order_outbox_slot\"," +
                    "\"plugin.name\": \"pgoutput\"," +
                    "\"auto.create.topics.enable\": false," +
                    "\"auto.register.schemas\": false" +
                    "}" +
                    "}"
            };


        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testPostRequestToEatsOrderAndRestaurantRequestApprovalTopic() throws InterruptedException, JsonProcessingException {

        String schemaRegistryUrl = "http://localhost:8081"; // Schema Registry URL 설정

        try (KafkaConsumer<String, RequestAvroModel> consumer = new KafkaConsumer<>(
            ImmutableMap.<String, Object>builder()
                .put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootStrapServeres)
                .put(ConsumerConfig.GROUP_ID_CONFIG, "tc-" + UUID.randomUUID())
                .put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
                .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class)
                .put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl)
                .put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true)
                .build()
        )) {
            Collection<NewTopic> topics = Collections.singletonList(new NewTopic(eatsOrderServiceConfigData.getRestaurantApprovalRequestTopicName(), 3, (short) 2));
            consumer.subscribe(Collections.singletonList(eatsOrderServiceConfigData.getRestaurantApprovalRequestTopicName()));

            // 추가: 현재 offset 확인
            var partitionOffsets = consumer.endOffsets(consumer.assignment());
            var currentOffset = partitionOffsets.values().stream().mapToLong(v -> v).sum();


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
            String orderItemJson = """
                {
                    "product": {
                        "name": "Product Name",
                        "description": "Description"
                    },
                    "quantity": 1,
                    "price": {
                        "amount": "50.25"
                    },
                    "subTotal": {
                        "amount": "50.25"
                    }
                }
                """;
            // adding " in "%s" is important!
            String jsonPayload = String.format("""
                    {
                        "callerId": "%s",
                        "calleeId": "%s",
                        "price": %f,
                        "address": %s,
                        "payment": null,
                        "items": [%s]
                    }
                    """,
                userId,
                driverId,
                price,
                address,
                orderItemJson);

            HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);

            // When
            ResponseEntity<EatsOrderResponseDto> response = restTemplate.postForEntity("/api/eatsorder", entity, EatsOrderResponseDto.class);

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
