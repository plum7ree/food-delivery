//package com.example.couponapp;
//
//import com.example.couponapp.config.RedisInitializer;
//import com.example.couponapp.dto.IssueRequestDto;
//import com.example.couponapp.dto.ResponseDto;
//import com.example.couponapp.utils.DockerComposeStarter;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.redisson.Redisson;
//import org.redisson.api.RedissonReactiveClient;
//import org.redisson.client.codec.StringCodec;
//import org.redisson.config.Config;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.web.reactive.function.BodyInserters;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//import java.util.concurrent.TimeUnit;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.assertj.core.api.AssertionsForClassTypes.fail;
//
//@SpringBootTest
//@ActiveProfiles("local")
//public class IntegrationTestManualDockerComposeRunNeeded {
//
//    private static RedissonReactiveClient redissonReactiveClient;
//    private static RedisInitializer redisInitializer;
//
//
//
//    @BeforeAll
//    public static void setup() throws Exception {
//        Config config = new Config();
//        config.useSingleServer().setAddress("redis://localhost:6379");
//        config.setCodec(StringCodec.INSTANCE); // Codec 설정 필수!
//        redissonReactiveClient = Redisson.create(config).reactive();
//        redisInitializer = new RedisInitializer(redissonReactiveClient);
//
//    }
//
//
//    @BeforeEach
//    public void reInitData() throws Exception {
//        // 각 테스트 마다 시작상태 초기화
//        redisInitializer.initRedis().run();
//    }
//
//
//    @Test
//    public void testSomething() throws InterruptedException {
//        // Your test code here
//        WebClient webClient = WebClient.builder()
//            .baseUrl("http://localhost:8092")
//            .build();
//
//        IssueRequestDto issueRequestDto = new IssueRequestDto();
//        issueRequestDto.setUserId("testUser");
//        issueRequestDto.setCouponId(1000000L);
//
//        Mono<ResponseEntity<ResponseDto>> response = webClient.post()
//            .uri("/api/issue")
//            .body(BodyInserters.fromValue(issueRequestDto))
//            .retrieve()
//            .toEntity(ResponseDto.class)
//            .onErrorResume(e -> {
//                fail(e.getMessage());
//                return Mono.empty();
//            });
//
//        ResponseEntity<ResponseDto> responseEntity = response.block();
//
//        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(responseEntity.getBody()).isNotNull();
//    }
//}