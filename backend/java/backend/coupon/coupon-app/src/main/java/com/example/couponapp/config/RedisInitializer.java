package com.example.couponapp.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScript;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Configuration
@Slf4j
public class RedisInitializer {
    private final RedissonReactiveClient redissonReactiveClient;

    public RedisInitializer(RedissonReactiveClient redissonReactiveClient) {
        this.redissonReactiveClient = redissonReactiveClient;
    }

    @Bean
    public CommandLineRunner initRedis() {
        return args -> {
            // Lua 스크립트 파일 경로
            ClassPathResource resource = new ClassPathResource("init-redis.lua");

            // Lua 스크립트 파일 읽기
            String script;
            try {
                Path scriptPath = resource.getFile().toPath();
                script = Files.readString(scriptPath);
            } catch (IOException e) {
                log.error("Failed to read Lua script file", e);
                return;
            }

            // Lua 스크립트 실행. Codec 설정 필수!
            redissonReactiveClient.getScript(StringCodec.INSTANCE)
                .eval(RScript.Mode.READ_WRITE, script, RScript.ReturnType.VALUE)
                .doOnError(error -> log.error("Failed to execute Lua script", error))
                .doOnSuccess(result -> {
                    System.out.println(result);
                    log.info("초기 데이터가 Redis에 삽입되었습니다.");
                })
                .block();
        };
    }

//    private final RedissonReactiveClient redissonReactiveClient;
//
//    public RedisInitializer(RedissonReactiveClient redissonReactiveClient) {
//        this.redissonReactiveClient = redissonReactiveClient;
//    }


//    @Bean
//    public CommandLineRunner initRedis() {
//        return args -> {
//            // 쿠폰 데이터 예시
//            Map<String, Map<String, String>> couponInfoMap = Map.of(
//                COUPON_INFO_KEY.apply(1000000L), Map.of(
//                    "startDate", LocalDateTime.now().toString(),
//                    "endDate", LocalDateTime.now().plusDays(30).toString(),
//                    "maxCount", "1000"
//                ),
//                COUPON_INFO_KEY.apply(2000000L), Map.of(
//                    "startDate", LocalDateTime.now().toString(),
//                    "endDate", LocalDateTime.now().plusDays(15).toString(),
//                    "maxCount", "500"
//                )
//            );
//
//            // Redis에 데이터 삽입
//            couponInfoMap.forEach((key, fields) -> {
//                RMapReactive<String, String> map = redissonReactiveClient.getMap(key);
//                map.delete().block();
//                map.putAll(fields).block();
//                Map<String, String> storedData = map.readAllMap().block();
//                System.out.println("Data readAllMap for key: " + key + " - " + storedData);
//                map.readAllEntrySet()
//                    .doOnNext(entrySet -> {
//                        for (Map.Entry<String, String> entry : entrySet) {
//                            log.info("entrySet key: {}, value: {}", entry.getKey(), entry.getValue());
//                        }
//                    }).block();
//            });
//
//            Map<String, String> couponCountMap = Map.of(
//                COUPON_COUNT_KEY.apply(1000000L), "0",
//                COUPON_COUNT_KEY.apply(2000000L), "0"
//            );
//            couponCountMap.forEach((k, v) -> {
//                var map = redissonReactiveClient.getBucket(k);
//                map.set(v).block();
//            });
//
//
//            Map<String, String> userCouponIssueMap = Map.of(
//                USER_COUPON_ISSUE_KEY.apply("testUser", 2000000L), "true"
//            );
//            userCouponIssueMap.forEach((k, v) -> {
//                var map = redissonReactiveClient.getBucket(k);
//                map.set(v).block();
//            });
//
//            // 삽입 완료 메시지
//            System.out.println("초기 데이터가 Redis에 삽입되었습니다.");
//        };
//    }
}