DROP SCHEMA IF EXISTS call_schema CASCADE; -- CASCADE 로 관련 객체 같이 삭제

CREATE SCHEMA call_schema;

-- uuid-ossp extension 설치
-- uuid_generate_v4() 함수를 통해 Version 4 UUID를 생성
CREATE
EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA call_schema;

-- enum type 생성
DROP TYPE IF EXISTS call_schema.call_status_enum;
CREATE TYPE call_schema.call_status_enum AS ENUM ('PENDING', 'PAID', 'APPROVED', 'CANCELLED', 'CANCELLING');

-- 1. TABLE call
DROP TABLE IF EXISTS call_schema.calls CASCADE;

-- numeric(10, 2): 고정 소숫점. 전체 자리수 10. 소숫점 이하 2.
-- text[]
-- ex) ARRAY['Payment processing failed', 'Insufficient funds', 'Order cancelled']
CREATE TABLE call_schema.calls
(
    id               uuid                         NOT NULL,
    user_id          uuid                         NOT NULL,
    driver_id        uuid                         NOT NULL,
    price            numeric(10, 2)               NOT NULL,
    call_status      call_schema.call_status_enum NOT NULL,
    failure_messages character varying COLLATE pg_catalog."default",
    CONSTRAINT call_pkey PRIMARY KEY (id)
);






