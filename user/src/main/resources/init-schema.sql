DROP SCHEMA IF EXISTS user_schema CASCADE;

CREATE SCHEMA user_schema;

CREATE
EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA user_schema;

-- character varying: 가변 길이 문자열
-- COLLATE pg_catalog."default" 문자 정렬 규칙
CREATE TABLE user_schema.users
(
    id         uuid                                           NOT NULL,
    username   character varying COLLATE pg_catalog."default" NOT NULL,
    first_name character varying COLLATE pg_catalog."default" NOT NULL,
    last_name  character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT user_pkey PRIMARY KEY (id)
);

