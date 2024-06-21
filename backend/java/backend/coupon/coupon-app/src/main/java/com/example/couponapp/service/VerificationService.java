package com.example.couponapp.service;

import com.example.couponapp.dto.IssueRequestDto;
import com.google.common.collect.Table;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final RedissonReactiveClient redissonReactiveClient;
    private RMapReactive<String, String> couponMap;

    // R = couponId, C = key, V = value
    private Table<Long, String, String> localCache;

    @PostConstruct
    public void initCouponIssueBucket() {
        couponMap = redissonReactiveClient.getMap("coupon:1000000:issue");

    }

    public boolean checkPeriodAndTime(IssueRequestDto issueRequestDto) {
        var couponId = issueRequestDto.getCouponId();

        LocalDateTime startDate;
        LocalDateTime endDate;
        if (localCache.containsRow(couponId)) {
            var row = localCache.row(couponId);
            startDate = LocalDateTime.parse(row.get("startDate"));
            endDate = LocalDateTime.parse(row.get("endDate"));
        } else {
            couponMap = redissonReactiveClient.getMap(String.format("coupon:%s:issue", couponId));
            startDate = LocalDateTime.parse(couponMap.get("startDate").toString());
            endDate = LocalDateTime.parse(couponMap.get("endDate").toString());
        }
        var now = LocalDateTime.now();
        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }

    public boolean checkCouponInventory(IssueRequestDto issueRequestDto) {
        return true;
    }

    public boolean checkDuplicateIssue(IssueRequestDto issueRequestDto) {
        return true;
    }
}
