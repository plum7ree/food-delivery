package com.example.locationredis;

import com.example.locationredis.config.RedissonConfig;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.redisson.api.RBucketReactive;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

@SpringBootTest
@ContextConfiguration(classes = {RedissonConfig.class})
@RequiredArgsConstructor
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // @BeforeAll, @AfterAll
//@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.redis.address=redis://localhost:6379"})
public class KeyValueObjectTest {
    @Autowired
    protected RedissonReactiveClient redissonReactiveClient;


    @Test
    public void keyVal() {
        // bucket:
        RBucketReactive<String> bucket = redissonReactiveClient.getBucket("user:1:name");
        Mono<Void> set = bucket.set("sam");
        Mono<Void> get = bucket.get().doOnNext(System.out::println).then();
        StepVerifier.create(set.concatWith(get)).verifyComplete();

    }

    @Test
    public void keyValExpiryTest() {
        RBucketReactive<String> bucket = redissonReactiveClient.getBucket("user:1:name");
        Mono<Void> set = bucket.set("sam10sec");
        Mono<Void> set1 = bucket.set("sam1sec", 1, TimeUnit.SECONDS);
        Mono<Void> set2 = bucket.set("sam2sec", 2, TimeUnit.SECONDS);
        Mono<Void> get = bucket.get().doOnNext(System.out::println).then();
//        첫 번째 스트림(set)이 완료된 후에 두 번째 스트림(get)을 구독하고 실행
//        onComplete 시그널을 발행)할 것을 기대
        StepVerifier.create(set.concatWith(set).concatWith(set1).concatWith(set2).concatWith(get)).verifyComplete();


        try {
            sleep(1500);

        } catch (Exception e) {

        }
        // extend
        Mono<Boolean> mono = bucket.expire(60, TimeUnit.SECONDS);
//        스트림이 true라는 값을 발행할 것을 기대
        StepVerifier.create(mono).expectNext(true).verifyComplete();

        // access expiration time
        Mono<Void> ttl = bucket.remainTimeToLive()
                .doOnNext(System.out::println)
                .then();
        StepVerifier.create(ttl)
                .verifyComplete();

    }

//    @Test
//    public void keyValueObjectTest() {
//        LocationDto location = new LocationDto(10.0F, 20.0F, 10000L);
//        RBucketReactive<String> bucket = redissonReactiveClient.getBucket("", JsonJacksonCodec.INSTANCE);
//    }
}
