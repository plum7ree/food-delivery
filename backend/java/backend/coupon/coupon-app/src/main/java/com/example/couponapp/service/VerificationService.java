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

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationService {

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
        log.info("{}", localCouponStaticInfoCache.containsRow(couponInfoKey));
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
                        // 분산 락 생성
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

        RLockReactive lock = redissonReactiveClient.getLock(couponLockKey);
        return lock.tryLock(10, TimeUnit.SECONDS)  // 1초 동안 락 획득 시도
            .flatMap(acquired -> {
                if (acquired) {
                    return redissonReactiveClient.<String>getBucket(couponCountKey).get()
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
                                return Mono.just(false);
                            }
                        });
                } else {
                    return Mono.just(false); // 락 획득 실패 시 처리
                }
            })
            .doOnError(e -> {
                log.error(e.getMessage());
            })
            .onErrorReturn(false)
            .doFinally(signalType -> lock.unlock());
    }

}
