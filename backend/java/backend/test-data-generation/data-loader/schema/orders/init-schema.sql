DROP SCHEMA IF EXISTS "order" CASCADE;

CREATE SCHEMA "order";

CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";


DROP TABLE IF EXISTS "order".orders CASCADE;

CREATE TABLE "order".orders
(
    id            uuid              NOT NULL,
    customer_id   uuid              NOT NULL,
    restaurant_id uuid              NOT NULL,
    price         numeric(10, 2)    NOT NULL,
    order_status  character varying NOT NULL,
    failure_messages character varying COLLATE pg_catalog."default",
    CONSTRAINT orders_pkey PRIMARY KEY (id)
);

DROP TABLE IF EXISTS "order".order_items CASCADE;

CREATE TABLE "order".order_items
(
    order_id   uuid           NOT NULL,
    product_id uuid           NOT NULL,
    price      numeric(10, 2) NOT NULL,
    quantity   integer        NOT NULL,
    sub_total  numeric(10, 2) NOT NULL,
    CONSTRAINT order_items_pkey PRIMARY KEY (order_id, product_id)
);

ALTER TABLE "order".order_items
    ADD CONSTRAINT "FK_ORDER_ID" FOREIGN KEY (order_id) -- 추가할 제약 조건 이름 "FK_ORDER_ID", column "order_id"
        REFERENCES "order".orders (id) MATCH SIMPLE -- foreign key 와 연결시킬 table, column. MATCH SIMPLE: 왜래키 열중 하나라도 null 일 경우 무효
        ON UPDATE NO ACTION -- 참조된 기본 키가 업데이트될 때 외래 키에는 아무런 동작을 취하지 않도록 함. default. ON UPDATE CASCADE 로 바꿀까??
        ON DELETE CASCADE -- 참조된 기본 키가 삭제될 때, 이를 참조하는 외래 키 열을 가진 행도 함께 삭제
        NOT VALID;

DROP TABLE IF EXISTS "order".order_address CASCADE;

CREATE TABLE "order".order_address
(
    order_id    uuid UNIQUE                                    NOT NULL,
    street      character varying COLLATE pg_catalog."default" NOT NULL,
    postal_code character varying COLLATE pg_catalog."default" NOT NULL,
    city        character varying COLLATE pg_catalog."default" NOT NULL,
    lat numeric(8, 6) NOT NULL,
    lon numeric(9, 6) NOT NULL,
    CONSTRAINT order_address_pkey PRIMARY KEY (order_id) -- id, order_id 가 다중 키
);

ALTER TABLE "order".order_address
    ADD CONSTRAINT "FK_ORDER_ID" FOREIGN KEY (order_id)
        REFERENCES "order".orders (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
        NOT VALID;


DROP TABLE IF EXISTS "order".restaurant_approval_outbox CASCADE;

-- TODO idempotence key, partition key, payload 등을 추가하자.
--  현재는 orderId 를 idempotency key, parition key, correlation_id 세개로 모두 쓰고 있음.
CREATE TABLE "order".restaurant_approval_outbox
(
    id             uuid              NOT NULL,
    correlation_id uuid              NOT NULL,
    status         character varying NOT NULL,
    CONSTRAINT restaurant_approval_outbox_pkey PRIMARY KEY (id)
);
ALTER TABLE "order".restaurant_approval_outbox
    ADD CONSTRAINT "FK_ORDER_ID" FOREIGN KEY (correlation_id)
        REFERENCES "order".orders (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
        NOT VALID;