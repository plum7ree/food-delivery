-- schema ref: https://www.youtube.com/watch?v=VUdpb0FFsY4
drop schema if exists "coupon" cascade;

create schema "coupon";

CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

create table "coupon".coupons
(
    id bigint unsigned auto_increment primary key comment 'coupon id'
        event_id bigint unsigned null comment ''
        discount_type varchar (50) not null comment 'by price / percent'
        discount_rate bigint unsigned null comment 'only for by percent'
        discount_price bigint unsigned null comment 'only for by amount'
        coupon_type varchar (50) not null comment ''
        max_quantity bigint unsigned null comment 'null is infinite'
        issued_quantity bigint unsigned null comment 'null is infinite'
        validate_start_date datetime not null comment ''
        validate_end_date datetime not null comment ''
        created_at datetime not null
        updated_at datetime not null
)


create table "coupon".coupon_issue
(
    id bigint unsigned auto_increment primary key comment ''
        member_id uuid not null comment ''
        coupon_id bigint unsigned not null
        coupon_status varchar (50) not null default 'NOT_ACTIVE'
        created_at datetime not null
        updated_at datetime not null
        check_related_issued_quantity boolean default false
)



