-- 기존 데이터 삭제
DELETE
FROM "coupon".coupon_issue;
DELETE
FROM "coupon".coupons;

-- 쿠폰 데이터 삽입
INSERT INTO "coupon".coupons (id, discount_type, discount_rate, discount_price, coupon_type, max_quantity,
                              issued_quantity, validate_start_date, validate_end_date)
VALUES (1000000, 'PERCENT', 10, NULL, 'NORMAL', 500, 0, '2000-01-01T00:00:00', '2999-01-01T00:00:00'),
       (2000000, 'PERCENT', 20, NULL, 'NORMAL', 500, 0, '2000-01-01T00:00:00', '2999-01-01T00:00:00'),
       (3000000, 'FIXED', NULL, 5000, 'NORMAL', 500, 500, '2000-01-01T00:00:00', '2999-01-01T00:00:00'),
       (4000000, 'PERCENT', 30, NULL, 'NORMAL', 500, 0, '2999-01-01T00:00:00', '2999-01-01T00:00:00');

-- 사용자에게 이미 발급된 쿠폰 데이터 삽입
INSERT INTO "coupon".coupon_issue (member_id, coupon_id, coupon_status)
VALUES ('00000000-0000-0000-0000-000000000001', 2000000, 'NOT_ACTIVE');
