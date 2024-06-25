package com.example.couponapp;

import com.example.couponapp.dto.IssueRequestDto;
import com.example.couponapp.dto.ResponseDto;
import com.example.couponapp.utils.DockerComposeStarter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        dockerComposeStarter.startServiceAndWaitForLog("lomojiki/uber-msa-coupon-app", "Started CouponAppApplication", 5, TimeUnit.MINUTES);
    }

    @AfterAll
    public static void tearDown() throws Exception {
        if (dockerComposeStarter != null) {
            dockerComposeStarter.stopAllServices();
        }
    }

    @BeforeEach
    public void reInitData() throws InterruptedException, IOException {
        initRedisData();
    }

    public boolean initRedisData() throws InterruptedException, IOException {
        // Redis 초기화 스크립트 실행
        String scriptPath = Objects.requireNonNull(getClass().getClassLoader().getResource("init_redis.sh")).getPath();
        ProcessBuilder processBuilder = new ProcessBuilder(scriptPath);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        // 스크립트 실행 결과 출력
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Failed to execute script. Exit code: " + exitCode);
        }

        System.out.println("초기 데이터가 Redis에 삽입되었습니다.");
        return true;
    }

    @Test
    public void testSomething() throws InterruptedException {
        // Your test code here
        WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8092")
            .build();

        IssueRequestDto issueRequestDto = new IssueRequestDto();
        issueRequestDto.setUserId("1000000");
        issueRequestDto.setCouponId(123L);

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