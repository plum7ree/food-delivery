package com.example.couponapp.service;

import com.example.couponapp.dto.IssueRequestDto;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.redisson.api.RBucketReactive;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import java.util.HashMap;

import static com.example.couponapp.service.VerificationService.COUPON_COUNT_KEY;
import static com.example.couponapp.service.VerificationService.COUPON_INFO_KEY;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class VerificationServiceTest {

    private VerificationService verificationService;

    @Mock
    private RedissonReactiveClient redissonReactiveClient;

    @Mock
    private RMapReactive<String, String> couponStaticInfoMap;

    @Mock
    private RBucketReactive<String> couponIssueCountBucket;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        verificationService = new VerificationService(redissonReactiveClient);

        when(redissonReactiveClient.<String, String>getMap(anyString())).thenReturn(couponStaticInfoMap);
        when(redissonReactiveClient.<String>getBucket(anyString())).thenReturn(couponIssueCountBucket);

        var couponId = 1000000L;
        var couponInfoKey = COUPON_INFO_KEY.apply(couponId);
        var couponCountKey = COUPON_COUNT_KEY.apply(couponId);

        Table<String, String, String> localCache = HashBasedTable.create();
        localCache.put(couponInfoKey, "startDate", "1900-01-01T00:00:00");
        localCache.put(couponInfoKey, "endDate", "2100-12-31T23:59:59");

        ReflectionTestUtils.setField(verificationService, "localCouponStaticInfoCache", localCache);

    }

    @Test
    void checkPeriodAndTime_ShouldReturnTrue_WhenWithinPeriod() {
        var couponId = 1000000L;
        var couponInfoKey = COUPON_INFO_KEY.apply(couponId);
        var couponCountKey = COUPON_COUNT_KEY.apply(couponId);


        IssueRequestDto requestDto = new IssueRequestDto();
        requestDto.setUserId("1000000");
        requestDto.setCouponId(couponId);

        boolean result = verificationService.checkPeriodAndTime(requestDto).block();

        assertTrue(result);
    }

    @Test
    void checkPeriodAndTime_ShouldReturnFalse_WhenOutsidePeriod() {
        var couponId = 1000000L;
        var couponInfoKey = COUPON_INFO_KEY.apply(couponId);
        var couponCountKey = COUPON_COUNT_KEY.apply(couponId);

        IssueRequestDto requestDto = new IssueRequestDto();
        requestDto.setUserId("1000000");
        requestDto.setCouponId(couponId);

        Table<String, String, String> localCache = HashBasedTable.create();
        localCache.put(couponInfoKey, "startDate", "2925-01-01T00:00:00");
        localCache.put(couponInfoKey, "endDate", "2925-12-31T23:59:59");

        ReflectionTestUtils.setField(verificationService, "localCouponStaticInfoCache", localCache);

        boolean result = verificationService.checkPeriodAndTime(requestDto).block();

        assertFalse(result);
    }

    @Test
    void checkCouponInventory_ShouldReturnTrue_WhenInventoryAvailable() {
        var couponId = 1000000L;
        var couponInfoKey = COUPON_INFO_KEY.apply(couponId);
        var couponCountKey = COUPON_COUNT_KEY.apply(couponId);

        IssueRequestDto requestDto = new IssueRequestDto();
        requestDto.setUserId("1000000");
        requestDto.setCouponId(couponId);

        Table<String, String, String> localCouponStaticInfoCache = HashBasedTable.create();
        HashMap<String, String> localCouponIssueCountCache = new HashMap<>();

        localCouponStaticInfoCache.put(couponInfoKey, "maxCount", "10");
        when(couponIssueCountBucket.get()).thenReturn(Mono.just("9"));

        ReflectionTestUtils.setField(verificationService, "localCouponStaticInfoCache", localCouponStaticInfoCache);


        boolean result = verificationService.checkCouponInventory(requestDto).block();

        assertTrue(result);
    }

    @Test
    void checkCouponInventory_ShouldReturnFalse_WhenInventoryNotAvailable() {
        var couponId = 1000000L;
        var couponInfoKey = COUPON_INFO_KEY.apply(couponId);
        var couponCountKey = COUPON_COUNT_KEY.apply(couponId);

        IssueRequestDto requestDto = new IssueRequestDto();
        requestDto.setUserId("1000000");
        requestDto.setCouponId(couponId);

        Table<String, String, String> localCouponStaticInfoCache = HashBasedTable.create();
        HashMap<String, String> localCouponIssueCountCache = new HashMap<>();

        localCouponStaticInfoCache.put(couponInfoKey, "maxCount", "10");
        when(couponIssueCountBucket.get()).thenReturn(Mono.just("10"));

        ReflectionTestUtils.setField(verificationService, "localCouponStaticInfoCache", localCouponStaticInfoCache);


        boolean result = verificationService.checkCouponInventory(requestDto).block();

        assertFalse(result);
    }
}