
package com.example.couponapp;

import com.example.couponapp.config.RedisInitializer;
import com.example.couponapp.config.RedissonConfig;
import com.example.couponapp.controller.CouponController;
import com.example.couponapp.dto.IssueRequestDto;
import com.example.couponapp.dto.ResponseDto;
import com.example.couponapp.dto.Status;
import com.example.couponapp.utils.DockerComposeStarter;
import com.example.kafka.admin.client.KafkaAdminClient;
import com.example.kafka.admin.config.KafkaAdminConfig;
import com.example.kafka.config.data.KafkaConfigData;
import com.example.kafka.config.data.KafkaProducerConfigData;
import com.example.kafkaproducer.config.KafkaProducerConfig;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.concurrent.TimeUnit;

@WebFluxTest(CouponController.class)
@ContextConfiguration(classes = {RedissonConfig.class,
    RedisInitializer.class,
    KafkaProducerConfig.class,
    KafkaAdminClient.class,
    KafkaConfigData.class,
    KafkaAdminConfig.class,
    KafkaProducerConfigData.class
})
@ActiveProfiles("local")
public class IntegrationTestCustomDockerComposeStarter {
    private static final String DOCKER_COMPOSE_FILE_PATH = ClassLoader.getSystemResource("docker-compose-test.yml").getPath();

    private static DockerComposeStarter dockerComposeStarter;

    private static RedissonReactiveClient redissonReactiveClient;
    private static RedisInitializer redisInitializer;

    @Autowired
    private WebTestClient webTestClient;

    static {
        dockerComposeStarter = new DockerComposeStarter(DOCKER_COMPOSE_FILE_PATH);

        // Start PostgreSQL
        try {
            dockerComposeStarter.startServiceAndWaitForLog("coupon-db", ".*ready to accept connections.*", 5, TimeUnit.MINUTES);
            // Start Redis
            dockerComposeStarter.startServiceAndWaitForLog("coupon-redis", ".*Ready to accept connections.*", 5, TimeUnit.MINUTES);

//            dockerComposeStarter.startServiceAndWaitForLog("zookeeper", ".*started.*", 5, TimeUnit.MINUTES);

            // Start Kafka
//            dockerComposeStarter.startServiceAndWaitForLog("kafka-broker-1", ".*started.*", 5, TimeUnit.MINUTES);
//            dockerComposeStarter.startServiceAndWaitForLog("kafka-broker-2", ".*started.*", 5, TimeUnit.MINUTES);
//            dockerComposeStarter.startServiceAndWaitForLog("kafka-broker-3", ".*started.*", 5, TimeUnit.MINUTES);
//
//            dockerComposeStarter.startServiceAndWaitForLog("schema-registry", ".*Cluster ID.*", 5, TimeUnit.MINUTES);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    @BeforeAll
    public static void setup() throws Exception {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379");
        config.setCodec(StringCodec.INSTANCE); // Codec 설정 필수!
        redissonReactiveClient = Redisson.create(config).reactive();
        redisInitializer = new RedisInitializer(redissonReactiveClient);

    }

    @AfterAll
    public static void tearDown() throws Exception {
        if (dockerComposeStarter != null) {
            dockerComposeStarter.stopAllServices();
        }
    }

    @BeforeEach
    public void reInitData() throws Exception {
        // 각 테스트 마다 시작상태 초기화
        redisInitializer.initRedis().run();
    }


    @Test
    public void testSomething() throws InterruptedException {
        IssueRequestDto issueRequestDto = new IssueRequestDto();
        issueRequestDto.setUserId("testUser");
        issueRequestDto.setCouponId(1000000L);

        // Your test code here
        webTestClient = WebTestClient.bindToServer()
            .baseUrl("http://localhost:8092")
            .build();
        webTestClient.post()
            .uri("/api/issue")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(issueRequestDto)
            .exchange()
            .expectStatus().isOk()
            .expectBody(ResponseDto.class)
            .value(responseDto -> {
                assert responseDto.getStatus() == Status.SUCCESSFUL;
                assert responseDto.getMessage().equals("Coupon issued successfully");
            });


    }
}