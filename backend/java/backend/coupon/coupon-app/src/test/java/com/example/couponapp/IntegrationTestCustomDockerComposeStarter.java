
package com.example.couponapp;

import com.example.couponapp.config.RedisInitializer;
import com.example.couponapp.dto.IssueRequestDto;
import com.example.couponapp.dto.ResponseDto;
import com.example.couponapp.dto.Status;
import com.example.couponapp.utils.DockerComposeStarter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.concurrent.TimeUnit;

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
@AutoConfigureWebTestClient(timeout = "360000") //3600sec for debug
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
    public void simpleUserIssueTest() throws InterruptedException {
        IssueRequestDto issueRequestDto = new IssueRequestDto();
        issueRequestDto.setUserId("testUser");
        issueRequestDto.setCouponId(1000000L);

        // Your test code here
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

    @Test
    public void whenSameUserIssueAgain_thenErrorWithDuplicateIssue() throws InterruptedException {
        IssueRequestDto issueRequestDto = new IssueRequestDto();
        issueRequestDto.setUserId("testUser");
        issueRequestDto.setCouponId(1000000L);

        // Your test code here
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

        webTestClient.post()
            .uri("/api/issue")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(issueRequestDto)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(ResponseDto.class)
            .value(responseDto -> {
                assert responseDto.getStatus() == Status.FAILED;
                assert responseDto.getMessage().equals("Duplicate issue");
            });

    }

    @Test
    public void whenAlreadyIssuedUser_thenErrorWithDuplicateIssue() throws InterruptedException {
        IssueRequestDto issueRequestDto = new IssueRequestDto();
        issueRequestDto.setUserId("testUser");
        issueRequestDto.setCouponId(2000000L);

        webTestClient.post()
            .uri("/api/issue")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(issueRequestDto)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(ResponseDto.class)
            .value(responseDto -> {
                assert responseDto.getStatus() == Status.FAILED;
                assert responseDto.getMessage().equals("Duplicate issue");
            });

    }

    @Test
    public void whenIssueCountAlreadyFull_thenErrorWithInsufficientInventory() throws InterruptedException {
        IssueRequestDto issueRequestDto = new IssueRequestDto();
        issueRequestDto.setUserId("testUser");
        issueRequestDto.setCouponId(3000000L);

        webTestClient.post()
            .uri("/api/issue")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(issueRequestDto)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(ResponseDto.class)
            .value(responseDto -> {
                assert responseDto.getStatus() == Status.FAILED;
                assert responseDto.getMessage().equals("Insufficient inventory");
            });

    }

}