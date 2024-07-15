package com.example.couponapp.service;

import com.example.couponapp.dto.IssueRequestDto;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.primitives.UnsignedLong;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLockReactive;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationService {

    public final static Long WAIT_TIME = 500L;
    public final static Long LEASE_TIME = 500L;
    public final static TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    // coupon:%s:issue:info
    // coupon:%s:issue:count
    private final RedissonReactiveClient redissonReactiveClient;
    // private RBucketReactive<String> couponIssueCountBucket; // only can be cached if reached full.

    // R = couponId, C = key, V = value
    private Table<String, String, String> localCouponStaticInfoCache = HashBasedTable.create();

    public static final Function<Long, String> COUPON_INFO_KEY =
        _couponId -> String.format("coupon:%d:issue:info", _couponId);

    public static final Function<Long, String> COUPON_COUNT_KEY =
        _couponId -> String.format("coupon:%d:issue:count", _couponId);
    public static final Function<Long, String> COUPON_LOCK_KEY =
        _couponId -> String.format("couponLock:%d", _couponId);
    public static final BiFunction<String, Long, String> USER_COUPON_ISSUE_KEY =
        (userId, _couponId) -> String.format("user:%s:coupon:%d:issued", userId, _couponId);

    public Mono<Boolean> checkLocalCache(IssueRequestDto issueRequestDto) {
        var couponId = issueRequestDto.getCouponId();
        var couponInfoKey = COUPON_INFO_KEY.apply(couponId);

        if (localCouponStaticInfoCache.containsRow(couponInfoKey)) {
            return Mono.just(true);
        }

        RMapReactive<String, String> couponStaticInfoMap = redissonReactiveClient.getMap(couponInfoKey);

        return couponStaticInfoMap.readAllEntrySet()
            .doOnNext(entrySet -> {
                for (Map.Entry<String, String> entry : entrySet) {
                    localCouponStaticInfoCache.put(couponInfoKey, entry.getKey(), entry.getValue());
                }
            })
            .then(Mono.just(true))
            .onErrorResume(e -> {
                System.err.println("Error fetching data from Redis for coupon " + couponId + ": " + e.getMessage());
                return Mono.just(false);
            });
    }

    public Mono<Boolean> checkPeriodAndTime(IssueRequestDto issueRequestDto) {
        var couponId = issueRequestDto.getCouponId();
        var couponInfoKey = COUPON_INFO_KEY.apply(couponId);
        LocalDateTime now = LocalDateTime.now();
        return Mono.justOrEmpty(localCouponStaticInfoCache.row(couponInfoKey))
            .flatMap(row -> {
                LocalDateTime startDate = LocalDateTime.parse(row.get("startDate"));
                LocalDateTime endDate = LocalDateTime.parse(row.get("endDate"));
                return Mono.just(!now.isBefore(startDate) && !now.isAfter(endDate));
            })
            .onErrorResume(e -> {
                System.err.println("checkPeriodAndTime" + couponId + ": " + e.getMessage());
                return Mono.just(false);
            })
            .defaultIfEmpty(false);
    }

    public Mono<Boolean> checkCouponInventory(IssueRequestDto issueRequestDto) {
        var couponId = issueRequestDto.getCouponId();
        var couponInfoKey = COUPON_INFO_KEY.apply(couponId);
        var couponCountKey = COUPON_COUNT_KEY.apply(couponId);

        return redissonReactiveClient.getBucket(couponCountKey).get()
            .flatMap(issuedCountObj -> {
                if (!(issuedCountObj instanceof String)) {
                    return Mono.just(false);
                }
                String issuedCountStr = (String) issuedCountObj;
                UnsignedLong issuedCount = UnsignedLong.valueOf(issuedCountStr);
                return Mono.justOrEmpty(localCouponStaticInfoCache.row(couponInfoKey))
                    .flatMap(row -> {
                        UnsignedLong maxCount = UnsignedLong.valueOf(row.get("maxCount"));
                        if (issuedCount.compareTo(maxCount) < 0) {
                            return Mono.just(true);
                        }
                        return Mono.just(false);
                    });
            })
            .defaultIfEmpty(false); // Redis에서 값을 찾지 못한 경우 false 반환
    }

    public Mono<Boolean> checkDuplicateIssue(IssueRequestDto issueRequestDto) {
        var userId = issueRequestDto.getUserId();
        var couponId = issueRequestDto.getCouponId();
        var couponInfoKey = COUPON_INFO_KEY.apply(couponId);
        var userCouponIssueKey = USER_COUPON_ISSUE_KEY.apply(userId, couponId);


//        var checkUserAlreadyIssuedCoupon = redissonReactiveClient.get(userKey);
        return redissonReactiveClient.getBucket(userCouponIssueKey).get().flatMap(bool ->
                Mono.just(true)
            )
            .defaultIfEmpty(false);

    }

    public Mono<Boolean> issueCouponToUser(IssueRequestDto issueRequestDto) {
        var userId = issueRequestDto.getUserId();
        var couponId = issueRequestDto.getCouponId();
        var couponInfoKey = COUPON_INFO_KEY.apply(couponId);
        var couponCountKey = COUPON_COUNT_KEY.apply(couponId);
        var couponLockKey = COUPON_LOCK_KEY.apply(couponId);
        var userIssueKey = USER_COUPON_ISSUE_KEY.apply(userId, couponId);

        RLockReactive lock = redissonReactiveClient.getFairLock(couponLockKey);


        //TODO 완전히 실패하지 않게 하려면 어떻게 하지?
        // waitTime 을 늘리면 자체 큐에 쌓이나? 얼마큼 까지 감당할 수 있지?
        // kube hpa 를 사용하거나 아예, kafka 로 바꿔야하나 그럴땐?
        var threadId = ThreadLocalRandom.current().nextLong();
        return lock.tryLock(WAIT_TIME, LEASE_TIME, TIME_UNIT, threadId)
            .flatMap(acquired -> {
                if (acquired) {
                    return redissonReactiveClient.<String>getBucket(couponCountKey).get() // getLockFairAsync 로 락을 걸고 락 획득에 실패한 스레드도 순서대로 락을 획득
                        .flatMap(currentIssuedCountStr -> {
                            UnsignedLong currentIssuedCount = UnsignedLong.valueOf(currentIssuedCountStr);
                            var row = localCouponStaticInfoCache.row(couponInfoKey);

                            UnsignedLong maxCount = UnsignedLong.valueOf(row.get("maxCount"));
                            if (currentIssuedCount.compareTo(maxCount) < 0) {
                                // issuedCount 증가
                                return redissonReactiveClient.getBucket(couponCountKey).set(currentIssuedCount.plus(UnsignedLong.ONE).toString())
                                    .then(redissonReactiveClient.<String>getBucket(userIssueKey).set("true"))
                                    .thenReturn(true);
                            } else {
                                log.error("coupon issue not less than max Count");
                                return Mono.just(false);
                            }
                        });
                } else {
                    log.error("lock acquired failed");
                    return Mono.just(false); // 락 획득 실패 시 처리
                }
            })
            .doOnError(e -> {
                log.error("error occurred" + e.getMessage());
            })
            .onErrorReturn(false)
            .publishOn(Schedulers.boundedElastic()) //TODO publishOn 있을때 (새로운 쓰레드에서 unlock().subscribe() -> non-blocking)랑 없을때(기존 쓰레드에서 unlock().subscribe() 로 blocking)랑 성능 비교 해보자
            .doFinally(signalType -> {
                lock.isLocked().flatMap(locked -> {
                    if (locked) {
//                        log.info("unlock tid: {}", threadId);
                        return lock.unlock(threadId);
                    }
                    return Mono.just(null);
                }).subscribe();
            });
    }

}
