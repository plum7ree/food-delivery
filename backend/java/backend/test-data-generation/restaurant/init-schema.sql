DROP SCHEMA IF EXISTS restaurant CASCADE;

CREATE SCHEMA restaurant;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TYPE IF EXISTS approval_status;

CREATE TYPE approval_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED');

DROP TABLE IF EXISTS restaurant.order_approval CASCADE; -- 해당 테이블에 의존하는 모든 객체들도 함께 삭제

CREATE TABLE restaurant.order_approval
(
    id            uuid            NOT NULL,
    restaurant_id uuid            NOT NULL,
    order_id      uuid            NOT NULL,
    status        approval_status NOT NULL,
    CONSTRAINT order_approval_pkey PRIMARY KEY (id)
);


DROP TYPE IF EXISTS order_status;
CREATE TYPE order_status AS ENUM (
    'PENDING',
    'CALLER_CANCELLED',
    'PAYMENT_COMPLETED',
    'PAYMENT_CANCELLED',
    'CALLEE_APPROVED',
    'CALLEE_REJECTED');

DROP TYPE IF EXISTS saga_status;
CREATE TYPE saga_status AS ENUM ('STARTED', 'FAILED', 'SUCCEEDED', 'PROCESSING', 'COMPENSATING', 'COMPENSATED', 'NOT_USED');

DROP TYPE IF EXISTS outbox_status;
CREATE TYPE outbox_status AS ENUM ('STARTED', 'COMPLETED', 'FAILED');

DROP TABLE IF EXISTS restaurant.order_outbox CASCADE;

DROP TABLE IF EXISTS restaurant.restaurant_approval_outbox CASCADE;

CREATE TABLE restaurant.restaurant_approval_outbox
(
    id            uuid                                           NOT NULL,
    saga_id       uuid                                           NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE                       NOT NULL,
    processed_at  TIMESTAMP WITH TIME ZONE,
    saga_type     character varying COLLATE pg_catalog."default" NOT NULL,
    payload       jsonb                                          NOT NULL,
    outbox_status outbox_status                                  NOT NULL,
    saga_status   saga_status                                    NOT NULL,
    order_status  order_status                                   NOT NULL,
    version       integer                                        NOT NULL,
    CONSTRAINT restaurant_approval_outbox_pkey PRIMARY KEY (id)
);

CREATE INDEX "restaurant_approval_outbox_saga_status"
    ON restaurant.restaurant_approval_outbox
        (saga_type, outbox_status, saga_status);

