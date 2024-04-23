-- user_schema
DROP SCHEMA IF EXISTS user_schema CASCADE;

CREATE SCHEMA user_schema;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA user_schema;

-- users
CREATE TABLE user_schema.users
(
    id         uuid                                           NOT NULL,
    username   character varying COLLATE pg_catalog."default" NOT NULL,
    first_name character varying COLLATE pg_catalog."default" NOT NULL,
    last_name  character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id)
);

-- restaurants type enum
DROP TYPE IF EXISTS user_schema.restaurant_type_enum;
CREATE TYPE user_schema.restaurant_type_enum AS ENUM ('BURGER', 'PIZZA', 'KOREAN', 'CHINESE', 'JAPANESE', 'MEXICAN', 'ITALIAN', 'AMERICAN', 'FUSION', 'MISC');
-- restaurants
CREATE TABLE user_schema.restaurants
(
    id          uuid                                           NOT NULL,
    user_id     uuid                                           NOT NULL,
    name        character varying COLLATE pg_catalog."default" NOT NULL,
    type        user_schema.restaurant_type_enum               NOT NULL,
    open_time   TIME                                           NOT NULL,
    close_time  TIME                                           NOT NULL,
    picture_url1 character varying COLLATE pg_catalog."default",
    picture_url2 character varying COLLATE pg_catalog."default",
    CONSTRAINT restaurants_pkey PRIMARY KEY (id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES user_schema.users (id)
);

-- menus
CREATE TABLE user_schema.menus
(
    id           uuid                                           NOT NULL,
    name         character varying COLLATE pg_catalog."default" NOT NULL,
    description  character varying COLLATE pg_catalog."default",
    picture_url  character varying COLLATE pg_catalog."default",
    restaurant_id uuid                                           NOT NULL,
    created_at   TIMESTAMP                                      NOT NULL,
    updated_at   TIMESTAMP                                      NOT NULL,
    CONSTRAINT menus_pkey PRIMARY KEY (id),
    CONSTRAINT fk_restaurant_id FOREIGN KEY (restaurant_id) REFERENCES user_schema.restaurants (id)
);

-- option_groups
CREATE TABLE user_schema.option_groups
(
    id                   uuid                                           NOT NULL,
    is_duplicated_allowed BOOLEAN                                       NOT NULL,
    is_necessary         BOOLEAN                                       NOT NULL,
    menu_id              uuid                                           NOT NULL,
    CONSTRAINT option_groups_pkey PRIMARY KEY (id),
    CONSTRAINT fk_menu_id FOREIGN KEY (menu_id) REFERENCES user_schema.menus (id)
);

-- options
CREATE TABLE user_schema.options
(
    id              uuid                                           NOT NULL,
    name            character varying COLLATE pg_catalog."default" NOT NULL,
    cost            BIGINT                                         NOT NULL,
    option_group_id uuid                                           NOT NULL,
    CONSTRAINT options_pkey PRIMARY KEY (id),
    CONSTRAINT fk_option_group_id FOREIGN KEY (option_group_id) REFERENCES user_schema.option_groups (id)
);