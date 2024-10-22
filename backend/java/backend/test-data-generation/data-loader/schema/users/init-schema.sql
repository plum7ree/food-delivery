-- user_schema
DROP SCHEMA IF EXISTS user_schema CASCADE;

CREATE SCHEMA user_schema;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA user_schema;

-- users
CREATE TABLE user_schema.account
(
    id         UUID PRIMARY KEY,
    username   VARCHAR(255) NOT NULL UNIQUE,
    password        VARCHAR(255),
    email      VARCHAR(255) NOT NULL UNIQUE,
    profile_pic_url character varying,
    role            VARCHAR(255) NOT NULL,
    oauth2provider  VARCHAR(255),
    oauth2sub  VARCHAR(255),
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL
);

-- restaurants
CREATE TABLE user_schema.restaurant
(
    id           uuid                                           NOT NULL,
    account_id   uuid                                           NOT NULL,
    name         character varying COLLATE pg_catalog."default" NOT NULL,
    type         character varying COLLATE pg_catalog."default" NOT NULL,
    open_time    TIME                                           NOT NULL,
    close_time   TIME                                           NOT NULL,
    picture_url1 character varying COLLATE pg_catalog."default",
    picture_url2 character varying COLLATE pg_catalog."default",
    picture_url3 character varying COLLATE pg_catalog."default",
    created_at   TIMESTAMP NOT NULL,
    updated_at   TIMESTAMP NOT NULL,
    CONSTRAINT restaurant_pkey PRIMARY KEY (id),
    CONSTRAINT fk_account_id FOREIGN KEY (account_id) REFERENCES user_schema.account (id)
);

-- menus
CREATE TABLE user_schema.menu
(
    id            uuid                                           NOT NULL,
    name          character varying COLLATE pg_catalog."default" NOT NULL,
    description   character varying COLLATE pg_catalog."default",
    picture_url   character varying COLLATE pg_catalog."default",
    price         BIGINT                                         NOT NULL,
    currency      VARCHAR(255),
    restaurant_id uuid                                           NOT NULL,
    created_at    TIMESTAMP                                      NOT NULL,
    updated_at    TIMESTAMP                                      NOT NULL,
    CONSTRAINT menu_pkey PRIMARY KEY (id),
    CONSTRAINT fk_restaurant_id FOREIGN KEY (restaurant_id) REFERENCES user_schema.restaurant (id)
);

-- option_groups
CREATE TABLE user_schema.option_group
(
    id                uuid         NOT NULL,
    description       varchar(500) NOT NULL,
    max_select_number int          NOT NULL,
    is_necessary      BOOLEAN      NOT NULL,
    menu_id           uuid         NOT NULL,
    CONSTRAINT option_group_pkey PRIMARY KEY (id),
    CONSTRAINT fk_menu_id FOREIGN KEY (menu_id) REFERENCES user_schema.menu (id)
);


-- options
CREATE TABLE user_schema.option
(
    id              uuid                                           NOT NULL,
    name            character varying COLLATE pg_catalog."default" NOT NULL,
    cost            BIGINT                                         NOT NULL,
    currency        VARCHAR(3),
    option_group_id uuid                                           NOT NULL,
    CONSTRAINT options_pkey PRIMARY KEY (id),
    CONSTRAINT fk_option_group_id FOREIGN KEY (option_group_id) REFERENCES user_schema.option_group (id)
);

-- options
CREATE TABLE user_schema.reviews
(
    id            uuid                                           NOT NULL,
    user_id       uuid                                           NOT NULL,
    user_name     character varying COLLATE pg_catalog."default" NOT NULL,
    restaurant_id uuid                                           NOT NULL,
    rating        FLOAT                                          NOT NULL,
    created_at    TIMESTAMP                                      NOT NULL,
    updated_at    TIMESTAMP                                      NOT NULL,
    image_url     character varying,
    comment       VARCHAR(1000)                                  NOT NULL,
    CONSTRAINT reviews_pkey PRIMARY KEY (id),
    CONSTRAINT fk_account_id FOREIGN KEY (user_id) REFERENCES user_schema.account (id),
    CONSTRAINT fk_restaurant_id FOREIGN KEY (restaurant_id) REFERENCES user_schema.restaurant (id)

);

CREATE TABLE user_schema.address
(
    id          uuid         NOT NULL,
    user_id     uuid         NOT NULL,
    city        VARCHAR(20)  NOT NULL,
    street      VARCHAR(200) NOT NULL,
    postal_code VARCHAR(20)  NOT NULL,
    lat         FLOAT        NOT NULL,
    lon         FLOAT        NOT NULL,
    CONSTRAINT fk_account_id FOREIGN KEY (user_id) REFERENCES user_schema.account (id)
);

CREATE INDEX idx_user_id ON user_schema.address (user_id);
