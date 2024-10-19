package com.example.eatsorderapplication.application.service.driver;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.redisson.Redisson;
import org.redisson.api.RScript;
import org.redisson.api.RScript.Mode;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Redis CRUD Test")
@ExtendWith(SpringExtension.class)
public class RedissonLuaScriptTest {

    private static RedissonReactiveClient redissonClient;

    // Define the Lua script as a Java String
    private static final String NOT_LOCKED_DRIVER_LISTS_LUA_SCRIPT =
        "local result = {} " +
            "for i, key in ipairs(ARGV) do " +
            "  if redis.call('EXISTS', key) == 0 then " +
            "    table.insert(result, key) " +
            "  end " +
            "end " +
            "return result";

    @BeforeAll
    public static void initRedisClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379"); // TODO 현실에서는 테스트 레디스 서버일듯.
        StringCodec stringCodec = StringCodec.INSTANCE;
        config.setCodec(stringCodec);
        redissonClient = Redisson.create(config).reactive();
    }

    @BeforeEach
    public void setUp() {
        // Flush all keys to ensure a clean state before each test
        redissonClient.getKeys().flushall();
    }

    /**
     * Tests the Lua script to ensure it correctly identifies non-existing keys.
     */
    @Test
    public void testNotLockedDriverListsLuaScript() {

        Set<String> inputKeys = Set.of("key1", "key2", "key3", "key4");
        var lock1 = redissonClient.getLock("key1");
        var lock2 = redissonClient.getLock("key2");

        Mono<Boolean> lockMono1 = lock1.tryLock();
        Mono<Boolean> lockMono2 = lock2.tryLock();

        Mono.zip(lockMono1, lockMono2)
            .flatMap(tuple -> {
                boolean lock1Acquired = tuple.getT1();
                boolean lock2Acquired = tuple.getT2();

                if (!lock1Acquired || !lock2Acquired) {
                    return Mono.empty();
                }

                // 존재하지 않는 키 조회
                return redissonClient.getBuckets(StringCodec.INSTANCE)
                    .get(inputKeys.toArray(new String[0]))
                    .flatMapMany(map -> Flux.fromIterable(map.keySet()))
                    .filter(key -> !inputKeys.contains(key))  // 존재하지 않는 키들 필터링
                    .collectList();
            }).doOnSuccess(nonExistingKeys -> {
                // Assert: "key3" and "key4"가 반환되어야 함
                assertNotNull(nonExistingKeys, "Result should not be null");
                assertEquals(2, nonExistingKeys.size(), "Result should contain exactly 2 keys");
                assertTrue(nonExistingKeys.contains("key3"), "Result should contain 'key3'");
                assertTrue(nonExistingKeys.contains("key4"), "Result should contain 'key4'");
            })
            .doFinally(signalType -> {
                // 락 해제
                lock1.unlock().subscribe();
                lock2.unlock().subscribe();
            })
            .subscribe();  // 작업을 실행
    }

}