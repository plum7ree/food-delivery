package com.example.couponapp;

import com.example.couponapp.config.RedisInitializer;
import com.example.couponapp.dto.IssueRequestDto;
import com.example.couponapp.dto.ResponseDto;
import com.example.couponapp.utils.DockerComposeStarter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class IntegrationTest {
    private static final String DOCKER_COMPOSE_FILE_PATH = ClassLoader.getSystemResource("docker-compose-test.yml").getPath();

    private static DockerComposeStarter dockerComposeStarter;

    private static RedissonReactiveClient redissonReactiveClient;
    private static RedisInitializer redisInitializer;

    @BeforeAll
    public static void setup() throws Exception {
        dockerComposeStarter = new DockerComposeStarter(DOCKER_COMPOSE_FILE_PATH);

        // Start PostgreSQL
        dockerComposeStarter.startServiceAndWaitForLog("coupon-db", ".*ready to accept connections.*", 5, TimeUnit.MINUTES);

        // Start Redis
        dockerComposeStarter.startServiceAndWaitForLog("coupon-redis", ".*Ready to accept connections.*", 5, TimeUnit.MINUTES);

        dockerComposeStarter.startServiceAndWaitForLog("zookeeper", ".*started.*", 5, TimeUnit.MINUTES);

        // Start Kafka
        dockerComposeStarter.startServiceAndWaitForLog("kafka-broker-1", ".*started.*", 5, TimeUnit.MINUTES);

        dockerComposeStarter.startServiceAndWaitForLog("schema-registry", ".*Cluster ID.*", 5, TimeUnit.MINUTES);
//
//        // Start your application services
//        dockerComposeStarter.startServiceAndWaitForLog("coupon-service", "Started CouponServiceApplication", 5, TimeUnit.MINUTES);
        dockerComposeStarter.startServiceAndWaitForLog("coupon-app", "Started CouponAppApplication", 5, TimeUnit.MINUTES);


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
        redisInitializer.initRedis().run();
    }


    @Test
    public void testSomething() throws InterruptedException {
        Thread.sleep(10000000);
        // Your test code here
        WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8092")
            .build();

        IssueRequestDto issueRequestDto = new IssueRequestDto();
        issueRequestDto.setUserId("testUser");
        issueRequestDto.setCouponId(1000000L);

        Mono<ResponseEntity<ResponseDto>> response = webClient.post()
            .uri("/api/issue")
            .body(BodyInserters.fromValue(issueRequestDto))
            .retrieve()
            .toEntity(ResponseDto.class);

        ResponseEntity<ResponseDto> responseEntity = response.block();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
    }
}