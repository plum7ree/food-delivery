-- 쿠폰 데이터 초기화
redis.call("FLUSHALL")

-- 쿠폰 정보 설정
-- success 테스트
redis.call("HMSET", "coupon:1000000:issue:info", "startDate", "2000-01-01T08:00:00", "endDate", "2999-01-30T08:00:00", "maxCount", "1000")
-- 사용자에게 issue 이미 됬을때 테스트
redis.call("HMSET", "coupon:2000000:issue:info", "startDate", "2000-02-01T08:00:00", "endDate", "2999-02-30T08:00:00", "maxCount", "500")
-- count 다 찼을때 테스트
redis.call("HMSET", "coupon:3000000:issue:info", "startDate", "2000-02-01T08:00:00", "endDate", "2999-02-30T08:00:00", "maxCount", "500")
-- 기간 범위 밖일때 테스트
redis.call("HMSET", "coupon:4000000:issue:info", "startDate", "2999-02-01T08:00:00", "endDate", "2999-02-30T08:00:00", "maxCount", "500")

-- 쿠폰 카운트 설정
redis.call("SET", "coupon:1000000:issue:count", "0")
redis.call("SET", "coupon:2000000:issue:count", "0")
redis.call("SET", "coupon:3000000:issue:count", "500")
redis.call("SET", "coupon:3000000:issue:count", "0")

-- 사용자 쿠폰 발급 설정
redis.call("SET", "user:testUSer:coupon:2000000:issued", "true")

return "초기 데이터가 Redis에 삽입되었습니다."
