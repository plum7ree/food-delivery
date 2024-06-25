#!/bin/bash

# Redis 서버 정보
REDIS_HOST=localhost
REDIS_PORT=6379

# 쿠폰 데이터 초기화
redis-cli -h $REDIS_HOST -p $REDIS_PORT FLUSHALL

# 쿠폰 정보 설정
redis-cli -h $REDIS_HOST -p $REDIS_PORT HMSET coupon_info_1000000 startDate "$(date -Iseconds)" endDate "$(date -Iseconds -d '+30 days')" maxCount 1000
redis-cli -h $REDIS_HOST -p $REDIS_PORT HMSET coupon_info_2000000 startDate "$(date -Iseconds)" endDate "$(date -Iseconds -d '+15 days')" maxCount 500

# 쿠폰 카운트 설정
redis-cli -h $REDIS_HOST -p $REDIS_PORT SET coupon_count_1000000 0
redis-cli -h $REDIS_HOST -p $REDIS_PORT SET coupon_count_2000000 0

# 사용자 쿠폰 발급 설정
redis-cli -h $REDIS_HOST -p $REDIS_PORT SET user_coupon_issue_testUser_2000000 true

echo "초기 데이터가 Redis에 삽입되었습니다."
