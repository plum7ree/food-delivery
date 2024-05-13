package com.example.user;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.config.Config;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;


//@SpringBootTest(classes = RedissonConfig.class)
//@TestPropertySource(properties = {"eureka.client.enabled=false"})

public class CouponServiceTest {
   private RedissonClient redissonClient;

    @BeforeEach
    void setUp() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379");
        redissonClient = Redisson.create(config);
    }


    // [Without Redisson]
    // Pessimistic Lock version.
    // Problem, what if lock is not unlocked, and pod is down?
    // -> we need timeout
    @Test
    public void transactionWithPLock() {

    }


    // [Without Redisson]
    // problem. what if lock expires before transaction done?
    @Test
    public void transactionWithPLockAndTimeout() {

    }

    // [Without Redisson]
    // almost perfect. but how does other devlopers know how to use this?
    // -> make a proxy client.
    @Test
    public void transactionWithPLockAndTimeoutAndSignal() {

    }

    // [Reddison]
    // 문제: 락의 유효 시간이 초과되면 락은 자동으로 해제되지만, 별도의 예외는 발생하지 않음.
    // 따라서 시그널 추가 코드를 작성해야할듯. 그리고 롤백해야함.
    //    -- KEY 정보
    //    쿠폰정책ID : 쿠폰 정책 PK ID
    //    일자별ID : 일자별 이벤트 시간 PK ID
    //    key : coupon:time-attack:{쿠폰정책ID}:date-time:{일자별ID}:issued:users
    //
    //    -- VALUE 정보
    //    사용자ID : 사용자 PK ID
    //    value : 사용자 PK ID

    @Test
    public void testRSetFunctionsWithTransaction() {
        // given


        // when
        RLock lock = redissonClient.getLock("myLock");
        try {
            boolean isLocked = lock.tryLock(1, 5, TimeUnit.SECONDS);
            if (isLocked) {
                RTransaction transaction = redissonClient.createTransaction(TransactionOptions.defaults());
                RSet<String> couponSet = transaction.getSet("coupon");
                couponSet.add("user1");

                transaction.commit();
            }
        } catch (InterruptedException e) {
            // 락 획득 중 인터럽트 발생 시 처리
            Thread.currentThread().interrupt();
        } finally {
            // 락 해제
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }


        // then
        var couponSet = redissonClient.getSet("coupon");
//        couponSet.
    }
}
}
