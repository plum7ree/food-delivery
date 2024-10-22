-- schema ref: https://www.youtube.com/watch?v=VUdpb0FFsY4
drop schema if exists "coupon" cascade;

create schema "coupon";

-- H2 Database does not support CREATE EXTENSION
-- Therefore, uuid-ossp extension is not created

create table "coupon".coupons
(
    id                  bigint auto_increment primary key, -- H2에서는 bigserial을 지원하지 않으므로 auto_increment를 사용합니다.
    event_id            bigint      null,
    discount_type       varchar(50) not null,
    discount_rate       bigint      null,
    discount_price      bigint      null,
    coupon_type         varchar(50) not null,
    max_quantity        bigint      null,
    issued_quantity     bigint      null,
    validate_start_date timestamp   not null,
    validate_end_date   timestamp   not null,
    created_at          timestamp   not null default current_timestamp(),
    updated_at          timestamp   not null default current_timestamp()
);

create table "coupon".coupon_issue
(
    id                            bigint auto_increment primary key,          -- H2에서는 bigserial을 지원하지 않으므로 auto_increment를 사용합니다.
    member_id                     uuid        not null default random_uuid(), -- H2에서는 random_uuid()를 사용하여 UUID를 생성합니다.
    coupon_id                     bigint      not null,
    coupon_status                 varchar(50) not null default 'NOT_ACTIVE',
    created_at                    timestamp   not null default current_timestamp(),
    updated_at                    timestamp   not null default current_timestamp(),
    check_related_issued_quantity boolean              default false
);

-- Column comments
COMMENT ON COLUMN "coupon".coupons.id IS 'coupon id';
COMMENT ON COLUMN "coupon".coupons.discount_type IS 'by price / percent';
COMMENT ON COLUMN "coupon".coupons.discount_rate IS 'only for by percent';
COMMENT ON COLUMN "coupon".coupons.discount_price IS 'only for by amount';
COMMENT ON COLUMN "coupon".coupons.coupon_type IS '';
COMMENT ON COLUMN "coupon".coupons.max_quantity IS 'null is infinite';
COMMENT ON COLUMN "coupon".coupons.issued_quantity IS 'null is infinite';
COMMENT ON COLUMN "coupon".coupons.validate_start_date IS '';
COMMENT ON COLUMN "coupon".coupons.validate_end_date IS '';
COMMENT ON COLUMN "coupon".coupon_issue.member_id IS '';
COMMENT ON COLUMN "coupon".coupon_issue.coupon_status IS '';
