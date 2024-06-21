//package com.example.couponapp.service;
//
//import com.example.couponapp.dto.IssueRequestDto;
//import com.google.common.collect.HashBasedTable;
//import com.google.common.collect.Table;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.redisson.api.RMapReactive;
//import org.redisson.api.RedissonReactiveClient;
//import org.redisson.client.RedisClient;
//import org.redisson.client.RedisClientConfig;
//import org.redisson.client.protocol.RedisCommands;
//import org.redisson.client.protocol.RedisStrictCommand;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.redis.core.ReactiveRedisTemplate;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.context.web.WebAppConfiguration;
//
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.when;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@WebAppConfiguration
//class VerificationServiceTest {
//
//    @MockBean
//    private RedissonReactiveClient redissonReactiveClient;
//
//    @Autowired
//    private VerificationService verificationService;
//
//    private RMapReactive<String, String> couponMap;
//
//    @BeforeEach
//    void setUp() {
//        // Setup RedissonReactiveClient mock
//        couponMap = redissonReactiveClient.getMap("coupon:1000000:issue");
//        given(redissonReactiveClient.getMap(anyString())).willReturn(couponMap);
//
//        // Initialize local cache
//        Table<Long, String, String> localCache = HashBasedTable.create();
//        localCache.put(1000000L, "startDate", "2022-01-01T00:00:00");
//        localCache.put(1000000L, "endDate", "2023-01-01T00:00:00");
//        verificationService.localCache = localCache;
//
//        // Setup mock data in Redis
//        when(couponMap.get("startDate")).thenReturn("2022-01-01T00:00:00");
//        when(couponMap.get("endDate")).thenReturn("2023-01-01T00:00:00");
//    }
//
//    @Test
//    void testCheckPeriodAndTime() {
//        // Given
//        IssueRequestDto issueRequestDto = new IssueRequestDto();
//        issueRequestDto.setCouponId(1000000L);
//
//        // When
//        boolean result = verificationService.checkPeriodAndTime(issueRequestDto);
//
//        // Then
//        assertTrue(result);
//    }
//
//    @AfterEach
//    void tearDown() {
//        // Clean up code if necessary
//    }
//}
