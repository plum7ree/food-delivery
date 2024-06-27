package com.example.couponapp.config;

import com.example.couponapp.utils.DockerComposeStarter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.couponapp.service.VerificationService.COUPON_INFO_KEY;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = {RedissonConfig.class, RedisInitializer.class})
@TestPropertySource(properties = {"spring.data.redis-server=redis://localhost:6379"})
public class RedisInitializerTest {

    private static final String DOCKER_COMPOSE_FILE_PATH = ClassLoader.getSystemResource("docker-compose-test.yml").getPath();
    private static DockerComposeStarter dockerComposeStarter;

    @Autowired
    private RedissonReactiveClient redissonReactiveClient;

    @Autowired
    private RedisInitializer redisInitializer;

    @BeforeAll
    public static void globalSetup() throws Exception {

        dockerComposeStarter = new DockerComposeStarter(DOCKER_COMPOSE_FILE_PATH);
        dockerComposeStarter.startServiceAndWaitForLog("coupon-redis", ".*Ready to accept connections.*", 5, TimeUnit.MINUTES);

    }

    @AfterAll
    public static void globalTearDown() throws Exception {
        if (dockerComposeStarter != null) {
            dockerComposeStarter.stopAllServices();
        }
    }

    @BeforeEach
    public void setup() throws Exception {
        // Run the initRedis function
//        redisInitializer.initRedis().run();
    }

    @Test
    public void testRedisInitialization() {
        // Check if the data was inserted into Redis correctly
        RMapReactive<String, String> couponInfoMap = redissonReactiveClient
            .getMap(COUPON_INFO_KEY.apply(1000000L));
        Mono<Map<String, String>> mapMono = couponInfoMap.readAllMap();

        Map<String, String> result = mapMono.block();
        assertThat(result).isNotNull();
        assertThat(result.size()).isGreaterThan(0);
        assertThat(result.get("maxCount")).isEqualTo("1000");

        RMapReactive<String, String> couponInfoMap2 = redissonReactiveClient.getMap(COUPON_INFO_KEY.apply(2000000L));
        Mono<Map<String, String>> mapMono2 = couponInfoMap2.readAllMap();

        Map<String, String> result2 = mapMono2.block();
        assertThat(result2).isNotNull();
        assertThat(result2.get("maxCount")).isEqualTo("500");

        System.out.println("Redis data initialization verified.");
    }
}
