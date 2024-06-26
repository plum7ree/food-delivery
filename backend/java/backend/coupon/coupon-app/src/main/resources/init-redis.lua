-- 쿠폰 데이터 초기화
redis.call("FLUSHALL")

-- 쿠폰 정보 설정
redis.call("HMSET", "coupon:1000000:issue:info", "startDate", "2024-01-01T08:00:00", "endDate", "2024-01-30T08:00:00", "maxCount", "1000")
redis.call("HMSET", "coupon:2000000:issue:info", "startDate", "2024-02-01T08:00:00", "endDate", "2024-02-30T08:00:00", "maxCount", "500")

-- 쿠폰 카운트 설정
redis.call("SET", "coupon:1000000:issue:count", "0")
redis.call("SET", "coupon:2000000:issue:count", "0")

-- 사용자 쿠폰 발급 설정
redis.call("SET", "user:testUSer:coupon:2000000:issued", "true")

return "초기 데이터가 Redis에 삽입되었습니다."
