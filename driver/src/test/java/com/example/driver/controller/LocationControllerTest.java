package com.example.driver.controller;

import com.example.driver.config.RedisConfig;
import com.example.driver.dto.LocationDto;
import com.example.driver.dto.ResponseDto;
import org.junit.jupiter.api.TestInstance;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // @BeforeAll, @AfterAll
//@ActiveProfiles("test")
// test container
// https://testcontainers.com/guides/testing-spring-boot-rest-api-using-testcontainers/
// https://spring.io/blog/2023/06/23/improved-testcontainers-support-in-spring-boot-3-1
class LocationControllerTest {

    @Value("${driver.message}")
    private String driverMessage;
    @Value("${driver.redis.fields.location-edge.key}")
    private String locationEdgeKey;

    @Value("${driver.redis.fields.location-edge.field-name}")
    private String locationEdgeFieldName;

     @Value("${driver.redis.fields.edge-visit.key}")
    private String edgeVisitKey;
     @Value("${driver.redis.fields.edge-visit.field-name}")
     private String edgeVisitFieldName;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RedissonReactiveClient redissonReactiveClient;

    @Autowired
    private RedissonClient redissonClient;

    @Test
    void connectionTest() {
        RBucketReactive<String> bucket = redissonReactiveClient.getBucket("connection-test");
        Mono<Void> set = bucket.set("test");
        Mono<Void> get = bucket.get().doOnNext(v -> assertEquals(v, "test")).then();
        StepVerifier.create(set.concatWith(get)).verifyComplete();

    }
    @Test
    void testMonoExample() {
        String driverId = "driver1";
        String oldEdgeId = "1000000";
        String currEdgeId = "1000002";
        // Example data
        LocationDto exampleLocationDto = new LocationDto();
        exampleLocationDto.setDriverId(driverId);
        exampleLocationDto.setOldEdgeId(oldEdgeId);
        exampleLocationDto.setEdgeId(currEdgeId);

//        // Mocking the RedissonReactiveClient behavior
//        RScriptReactive rScriptReactive = mock(RScriptReactive.class);
//        when(redissonReactiveClient.getScript()).thenReturn(rScriptReactive);
//        when(rScriptReactive.eval(any(RScript.Mode.class), any(String.class), any(RScript.ReturnType.class), anyList()))
//                .thenReturn(Mono.just(Arrays.asList(-1, 1))); // Simulated return value from the script

        // Perform the POST request with the example data and verify the response
        webTestClient.post().uri("/api/driver/location")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(exampleLocationDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseDto.class)
                .isEqualTo(new ResponseDto("200", ""));

        // Additional verifications can be performed here to check that the script was called with the correct parameters
        // However, note that the actual update in Redis is not verified in this unit test, and would typically be covered in an integration test

        RMapReactive<String, String> driverLocationEdgeMap = redissonReactiveClient.getMap(locationEdgeFieldName);
        Mono<String> monoMapGet = driverLocationEdgeMap.get(driverId);
        StepVerifier.create(monoMapGet)
            .expectNext(currEdgeId)
            .verifyComplete();

    }
}
