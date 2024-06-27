package com.example.couponapp.service;

import com.example.couponapp.dto.IssueRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.redisson.api.RBucketReactive;
import org.redisson.api.RLockReactive;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.AbstractMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.example.couponapp.service.VerificationService.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class VerificationServiceTest {

    private VerificationService verificationService;

    @Mock
    private RedissonReactiveClient redissonReactiveClient;

    @Mock
    private RMapReactive<String, String> rMapMock;

    @Mock
    private RBucketReactive<String> rBucketMock;

    @Mock
    private RLockReactive rLockMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        verificationService = new VerificationService(redissonReactiveClient);

    }

    @Test
    void checkPeriodAndTime_ShouldReturnTrue_WhenWithinPeriod() {
        var couponId = 1000000L;
        var couponInfoKey = COUPON_INFO_KEY.apply(couponId);
        var couponCountKey = COUPON_COUNT_KEY.apply(couponId);


        IssueRequestDto requestDto = new IssueRequestDto();
        requestDto.setUserId("1000000");
        requestDto.setCouponId(couponId);

        when(rMapMock.readAllEntrySet()).thenReturn(Mono.just(
            Set.of(
                new AbstractMap.SimpleEntry<>("startDate", "2000-01-01T00:00:00"),
                new AbstractMap.SimpleEntry<>("endDate", "2999-01-01T00:00:00"),
                new AbstractMap.SimpleEntry<>("maxCount", "100")
            )
        ));
        when(redissonReactiveClient.<String, String>getMap(couponInfoKey)).thenReturn(rMapMock);

        boolean localCacheCheck = verificationService.checkLocalCache(requestDto).block();
        assertTrue(localCacheCheck);

        var periodCheck = verificationService.checkPeriodAndTime(requestDto);
        StepVerifier.create(periodCheck)
            .expectNext(true)
            .verifyComplete();
    }

    @Test
    void checkPeriodAndTime_ShouldReturnFalse_WhenOutsidePeriod() {
        var couponId = 1000000L;
        var couponInfoKey = COUPON_INFO_KEY.apply(couponId);
        var couponCountKey = COUPON_COUNT_KEY.apply(couponId);

        IssueRequestDto requestDto = new IssueRequestDto();
        requestDto.setUserId("1000000");
        requestDto.setCouponId(couponId);

        when(rMapMock.readAllEntrySet()).thenReturn(Mono.just(
            Set.of(
                new AbstractMap.SimpleEntry<>("startDate", "2998-01-01T00:00:00"),
                new AbstractMap.SimpleEntry<>("endDate", "2999-01-01T00:00:00"),
                new AbstractMap.SimpleEntry<>("maxCount", "100")
            )
        ));
        when(redissonReactiveClient.<String, String>getMap(couponInfoKey)).thenReturn(rMapMock);

        boolean localCacheCheck = verificationService.checkLocalCache(requestDto).block();
        assertTrue(localCacheCheck);

        var periodCheck = verificationService.checkPeriodAndTime(requestDto);
        StepVerifier.create(periodCheck)
            .expectNext(false)
            .verifyComplete();
    }

    @Test
    void checkCouponInventory_ShouldReturnTrue_WhenInventoryAvailable() {
        var couponId = 1000000L;
        var couponInfoKey = COUPON_INFO_KEY.apply(couponId);
        var couponCountKey = COUPON_COUNT_KEY.apply(couponId);

        IssueRequestDto requestDto = new IssueRequestDto();
        requestDto.setUserId("1000000");
        requestDto.setCouponId(couponId);

        when(rBucketMock.get()).thenReturn(Mono.just("0"));
        when(redissonReactiveClient.<String>getBucket(couponCountKey)).thenReturn(rBucketMock);

        when(rMapMock.readAllEntrySet()).thenReturn(Mono.just(
            Set.of(
                new AbstractMap.SimpleEntry<>("startDate", "2000-01-01T00:00:00"),
                new AbstractMap.SimpleEntry<>("endDate", "2999-01-01T00:00:00"),
                new AbstractMap.SimpleEntry<>("maxCount", "100")
            )
        ));
        when(redissonReactiveClient.<String, String>getMap(couponInfoKey)).thenReturn(rMapMock);

        boolean localCacheCheck = verificationService.checkLocalCache(requestDto).block();
        assertTrue(localCacheCheck);

        var result = verificationService.checkCouponInventory(requestDto);
        StepVerifier.create(result)
            .expectNext(true)
            .verifyComplete();
    }

    @Test
    void checkCouponInventory_ShouldReturnFalse_WhenInventoryNotAvailable() {
        var couponId = 1000000L;
        var couponInfoKey = COUPON_INFO_KEY.apply(couponId);
        var couponCountKey = COUPON_COUNT_KEY.apply(couponId);

        IssueRequestDto requestDto = new IssueRequestDto();
        requestDto.setUserId("1000000");
        requestDto.setCouponId(couponId);

        when(rBucketMock.get()).thenReturn(Mono.just("100"));
        when(redissonReactiveClient.<String>getBucket(couponCountKey)).thenReturn(rBucketMock);

        when(rMapMock.readAllEntrySet()).thenReturn(Mono.just(
            Set.of(
                new AbstractMap.SimpleEntry<>("startDate", "2000-01-01T00:00:00"),
                new AbstractMap.SimpleEntry<>("endDate", "2999-01-01T00:00:00"),
                new AbstractMap.SimpleEntry<>("maxCount", "100")
            )
        ));
        when(redissonReactiveClient.<String, String>getMap(couponInfoKey)).thenReturn(rMapMock);

        boolean localCacheCheck = verificationService.checkLocalCache(requestDto).block();
        assertTrue(localCacheCheck);

        var result = verificationService.checkCouponInventory(requestDto);
        StepVerifier.create(result)
            .expectNext(false)
            .verifyComplete();
    }

    @Test
    void givenCheckDuplicateIssue_ShouldReturnTrue_WhenUserHasNotIssuedCoupon() {
        var couponId = 1000000L;
        var userId = "testUser";
        var couponInfoKey = COUPON_INFO_KEY.apply(couponId);
        var userCouponIssueKey = USER_COUPON_ISSUE_KEY.apply(userId, couponId);

        // Arrange
        IssueRequestDto issueRequestDto = new IssueRequestDto();
        issueRequestDto.setUserId(userId);
        issueRequestDto.setCouponId(couponId);


        when(rBucketMock.get()).thenReturn(Mono.empty());
        when(redissonReactiveClient.<String>getBucket(userCouponIssueKey)).thenReturn(rBucketMock);


        // Act
        Mono<Boolean> result = verificationService.checkDuplicateIssue(issueRequestDto);
        when(redissonReactiveClient.<String, String>getMap(couponInfoKey)).thenReturn(rMapMock);

        // Assert
        StepVerifier.create(result)
            .expectNext(false)
            .verifyComplete();

    }

    @Test
    void givenCheckDuplicateIssue_ShouldReturnFalse_WhenUserAlreadyHasIssuedCoupon() {
        var couponId = 1000000L;
        var userId = "testUser";
        var couponInfoKey = COUPON_INFO_KEY.apply(couponId);
        var userCouponIssueKey = USER_COUPON_ISSUE_KEY.apply(userId, couponId);

        // Arrange
        IssueRequestDto issueRequestDto = new IssueRequestDto();
        issueRequestDto.setUserId(userId);
        issueRequestDto.setCouponId(couponId);


        when(rBucketMock.get()).thenReturn(Mono.just("true"));
        when(redissonReactiveClient.<String>getBucket(userCouponIssueKey)).thenReturn(rBucketMock);


        // Act
        Mono<Boolean> result = verificationService.checkDuplicateIssue(issueRequestDto);
        when(redissonReactiveClient.<String, String>getMap(couponInfoKey)).thenReturn(rMapMock);

        // Assert
        StepVerifier.create(result)
            .expectNext(true)
            .verifyComplete();

    }

    @Test
    void givenIssueCouponToUserReturn_shouldReturnTrue_whenIssuedCountLTMaxCount() {
        var couponId = 1000000L;
        var userId = "testUser";
        var couponInfoKey = COUPON_INFO_KEY.apply(couponId);
        var userCouponIssueKey = USER_COUPON_ISSUE_KEY.apply(userId, couponId);
        var couponCountKey = COUPON_COUNT_KEY.apply(couponId);
        var couponLockKey = COUPON_LOCK_KEY.apply(couponId);

        // Arrange
        IssueRequestDto issueRequestDto = new IssueRequestDto();
        issueRequestDto.setUserId(userId);
        issueRequestDto.setCouponId(couponId);


        when(rMapMock.readAllEntrySet()).thenReturn(Mono.just(
            Set.of(
                new AbstractMap.SimpleEntry<>("startDate", "2998-01-01T00:00:00"),
                new AbstractMap.SimpleEntry<>("endDate", "2999-01-01T00:00:00"),
                new AbstractMap.SimpleEntry<>("maxCount", "100")
            )
        ));
        when(redissonReactiveClient.<String, String>getMap(couponInfoKey)).thenReturn(rMapMock);

        when(rLockMock.tryLock(10, TimeUnit.SECONDS)).thenReturn(Mono.just(true));
        when(redissonReactiveClient.<String>getLock(couponLockKey)).thenReturn(rLockMock);

        when(rBucketMock.get()).thenReturn(Mono.just("0"));
        when(rBucketMock.set("1")).thenReturn(Mono.empty());
        when(redissonReactiveClient.<String>getBucket(couponCountKey)).thenReturn(rBucketMock);

        verificationService.checkLocalCache(issueRequestDto).block();
        Mono<Boolean> result = verificationService.issueCouponToUser(issueRequestDto);
        StepVerifier.create(result)
            .expectNext(true)
            .verifyComplete();
    }

    @Test
    public void givenIssueCouponToUserReturn_shouldReturnTrue_whenIssuedCountEQMaxCount() {
        var couponId = 1000000L;
        var userId = "testUser";
        var couponInfoKey = COUPON_INFO_KEY.apply(couponId);
        var userCouponIssueKey = USER_COUPON_ISSUE_KEY.apply(userId, couponId);
        var couponCountKey = COUPON_COUNT_KEY.apply(couponId);
        var couponLockKey = COUPON_LOCK_KEY.apply(couponId);

        // Arrange
        IssueRequestDto issueRequestDto = new IssueRequestDto();
        issueRequestDto.setUserId(userId);
        issueRequestDto.setCouponId(couponId);


        when(rMapMock.readAllEntrySet()).thenReturn(Mono.just(
            Set.of(
                new AbstractMap.SimpleEntry<>("startDate", "2998-01-01T00:00:00"),
                new AbstractMap.SimpleEntry<>("endDate", "2999-01-01T00:00:00"),
                new AbstractMap.SimpleEntry<>("maxCount", "100")
            )
        ));
        when(redissonReactiveClient.<String, String>getMap(couponInfoKey)).thenReturn(rMapMock);

        when(rLockMock.tryLock(10, TimeUnit.SECONDS)).thenReturn(Mono.just(true));
        when(redissonReactiveClient.<String>getLock(couponLockKey)).thenReturn(rLockMock);

        when(rBucketMock.get()).thenReturn(Mono.just("100"));
        when(rBucketMock.set("1")).thenReturn(Mono.empty());
        when(redissonReactiveClient.<String>getBucket(couponCountKey)).thenReturn(rBucketMock);

        verificationService.checkLocalCache(issueRequestDto).block();
        Mono<Boolean> result = verificationService.issueCouponToUser(issueRequestDto);
        StepVerifier.create(result)
            .expectNext(false)
            .verifyComplete();
    }

    @Test
    void givenIssueCouponToUserReturn_shouldReturnTrue_whenLockFailed() {
        var couponId = 1000000L;
        var userId = "testUser";
        var couponInfoKey = COUPON_INFO_KEY.apply(couponId);
        var userCouponIssueKey = USER_COUPON_ISSUE_KEY.apply(userId, couponId);
        var couponCountKey = COUPON_COUNT_KEY.apply(couponId);
        var couponLockKey = COUPON_LOCK_KEY.apply(couponId);

        // Arrange
        IssueRequestDto issueRequestDto = new IssueRequestDto();
        issueRequestDto.setUserId(userId);
        issueRequestDto.setCouponId(couponId);


        when(rMapMock.readAllEntrySet()).thenReturn(Mono.just(
            Set.of(
                new AbstractMap.SimpleEntry<>("startDate", "2998-01-01T00:00:00"),
                new AbstractMap.SimpleEntry<>("endDate", "2999-01-01T00:00:00"),
                new AbstractMap.SimpleEntry<>("maxCount", "100")
            )
        ));
        when(redissonReactiveClient.<String, String>getMap(couponInfoKey)).thenReturn(rMapMock);

        when(rLockMock.tryLock(10, TimeUnit.SECONDS)).thenReturn(Mono.just(false));
        when(redissonReactiveClient.<String>getLock(couponLockKey)).thenReturn(rLockMock);

        when(rBucketMock.get()).thenReturn(Mono.just("0"));
        when(rBucketMock.set("1")).thenReturn(Mono.empty());
        when(redissonReactiveClient.<String>getBucket(couponCountKey)).thenReturn(rBucketMock);

        verificationService.checkLocalCache(issueRequestDto).block();
        Mono<Boolean> result = verificationService.issueCouponToUser(issueRequestDto);
        StepVerifier.create(result)
            .expectNext(false)
            .verifyComplete();
    }
}