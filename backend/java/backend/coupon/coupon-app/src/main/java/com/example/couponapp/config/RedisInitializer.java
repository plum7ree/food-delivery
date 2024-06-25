package com.example.couponapp.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Map;

import static com.example.couponapp.service.VerificationService.*;

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
            // 쿠폰 데이터 예시
            Map<String, Map<String, String>> couponInfoMap = Map.of(
                COUPON_INFO_KEY.apply(1000000L), Map.of(
                    "startDate", LocalDateTime.now().toString(),
                    "endDate", LocalDateTime.now().plusDays(30).toString(),
                    "maxCount", "1000"
                ),
                COUPON_INFO_KEY.apply(2000000L), Map.of(
                    "startDate", LocalDateTime.now().toString(),
                    "endDate", LocalDateTime.now().plusDays(15).toString(),
                    "maxCount", "500"
                )
            );

            // Redis에 데이터 삽입
            couponInfoMap.forEach((key, fields) -> {
                RMapReactive<String, String> map = redissonReactiveClient.getMap(key);
                map.delete().block();
                map.putAll(fields).block();
                Map<String, String> storedData = map.readAllMap().block();
                System.out.println("Data readAllMap for key: " + key + " - " + storedData);
                map.readAllEntrySet()
                    .doOnNext(entrySet -> {
                        for (Map.Entry<String, String> entry : entrySet) {
                            log.info("entrySet key: {}, value: {}", entry.getKey(), entry.getValue());
                        }
                    }).block();
            });

            Map<String, String> couponCountMap = Map.of(
                COUPON_COUNT_KEY.apply(1000000L), "0",
                COUPON_COUNT_KEY.apply(2000000L), "0"
            );
            couponCountMap.forEach((k, v) -> {
                var map = redissonReactiveClient.getBucket(k);
                map.set(v).block();
            });


            Map<String, String> userCouponIssueMap = Map.of(
                USER_COUPON_ISSUE_KEY.apply("testUser", 2000000L), "true"
            );
            userCouponIssueMap.forEach((k, v) -> {
                var map = redissonReactiveClient.getBucket(k);
                map.set(v).block();
            });

            // 삽입 완료 메시지
            System.out.println("초기 데이터가 Redis에 삽입되었습니다.");
        };
    }
}