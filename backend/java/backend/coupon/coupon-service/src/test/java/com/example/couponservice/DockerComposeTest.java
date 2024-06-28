
package com.example.couponservice;


import com.example.commonutil.DockerComposeStarter;
import com.example.kafka.avro.model.CouponIssueRequestAvroModel;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

//@WebFluxTest(CouponController.class)
//@AutoConfigureWebTestClient
//@ContextConfiguration(classes = {RedissonConfig.class,
//    RedisInitializer.class,
//    KafkaProducerConfig.class,
//    KafkaAdminClient.class,
//    KafkaConfigData.class,
//    KafkaAdminConfig.class,
//    KafkaProducerConfigData.class
//})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
public class DockerComposeTest {
    private static final String DOCKER_COMPOSE_FILE_PATH = ClassLoader.getSystemResource("docker-compose-test.yml").getPath();

    private static DockerComposeStarter dockerComposeStarter;


    static {
        dockerComposeStarter = new DockerComposeStarter(DOCKER_COMPOSE_FILE_PATH);

        try {
            dockerComposeStarter.startServiceAndWaitForLog("coupon-db", ".*ready to accept connections.*", 5, TimeUnit.MINUTES);

            dockerComposeStarter.startServiceAndWaitForLog("zookeeper", ".*started.*", 5, TimeUnit.MINUTES);

            // Start Kafka
            dockerComposeStarter.startServiceAndWaitForLog("kafka-broker-1", ".*started.*", 5, TimeUnit.MINUTES);
            dockerComposeStarter.startServiceAndWaitForLog("kafka-broker-2", ".*started.*", 5, TimeUnit.MINUTES);
            dockerComposeStarter.startServiceAndWaitForLog("kafka-broker-3", ".*started.*", 5, TimeUnit.MINUTES);

            dockerComposeStarter.startServiceAndWaitForLog("schema-registry", ".*Cluster ID.*", 5, TimeUnit.MINUTES);

            Thread.sleep(5000); // 충분히 켜지길 기다려야함... TODO 시그널 방식으로 어떻게 바꿀까?

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private KafkaTemplate<String, CouponIssueRequestAvroModel> kafkaTemplate;
    private AdminClient adminClient;
    @Value("${kafka-consumer-group-id.coupon-issue-request-consumer-group-id}")
    private String consumerGroupId;
    @Value("${topic-names.coupon-issue-request-topic-name}")
    private String topicName;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // KafkaTemplate 설정
        Map<String, Object> producerProps = KafkaTestUtils.producerProps("localhost:19092,localhost:29092,localhost:39092");
        producerProps.put("key.serializer", StringSerializer.class);
        producerProps.put("value.serializer", KafkaAvroSerializer.class);
        producerProps.put("schema.registry.url", "http://localhost:8081");
        kafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerProps));

        // AdminClient 설정
        Map<String, Object> adminProps = new HashMap<>(KafkaTestUtils.consumerProps("localhost:19092,localhost:29092,localhost:39092", consumerGroupId, "true"));
        adminProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19092,localhost:29092,localhost:39092");
        adminClient = AdminClient.create(adminProps);

    }

    @AfterAll
    public static void tearDown() throws Exception {
        if (dockerComposeStarter != null) {
            dockerComposeStarter.stopAllServices();
        }
    }


    @Test
    public void multipleUserIssueTest() throws InterruptedException, ExecutionException {
        int userCount = 100;
        long couponId = 1000000L;
        CountDownLatch latch = new CountDownLatch(userCount);
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < userCount; i++) {
            executorService.submit(() -> {
                try {
                    var message = new CouponIssueRequestAvroModel();
                    message.setCallerId(UUID.randomUUID().toString());
                    message.setCouponId(couponId);
                    message.setIssueId((long) (Math.random() * 1000000)); // 랜덤 issueId
                    message.setAmount(1);
                    message.setCreatedAt(Instant.now());

                    kafkaTemplate.send(topicName, "partition-key", message).get();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 메시지가 전송될 때까지 대기
        executorService.shutdown();

        // 처리 시간을 위해 추가 대기
        Thread.sleep(5000);

        // 데이터베이스에서 결과 확인
        String couponQuery = "SELECT issued_quantity FROM coupons WHERE id = ?";
        Integer issuedQuantity = jdbcTemplate.queryForObject(couponQuery, Integer.class, couponId);

        String issueQuery = "SELECT COUNT(*) FROM coupon_issue WHERE coupon_id = ?";
        Integer issuedCount = jdbcTemplate.queryForObject(issueQuery, Integer.class, couponId);

        Assertions.assertNotNull(issuedQuantity, "Issued quantity should not be null");
        Assertions.assertEquals(userCount, issuedQuantity, "Issued quantity should match the number of users");
        Assertions.assertEquals(userCount, issuedCount, "Number of issued coupons should match the number of users");

        // 추가적으로 개별 발급 상태 확인
        String statusQuery = "SELECT coupon_status FROM coupon_issue WHERE coupon_id = ?";
        List<String> statuses = jdbcTemplate.queryForList(statusQuery, String.class, couponId);

        Assertions.assertEquals(userCount, statuses.size(), "Number of status records should match the number of users");
        for (String status : statuses) {
            Assertions.assertEquals("NOT_ACTIVE", status, "All issued coupons should have NOT_ACTIVE status");
        }
    }

}