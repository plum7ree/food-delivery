-- Insert example data for users
INSERT INTO account(id, username, password, email, role)
VALUES ('13f0c8f0-eec0-4169-ad9f-e8bb408a5325', 'username1', 'password1', 'user1@example.com', 'ROLE_USER');

INSERT INTO account(id, username, password, email, role)
VALUES ('dafd9593-0027-42f6-981a-629216784217', 'username2', 'password2', 'user2@example.com', 'ROLE_ADMIN');

INSERT INTO account(id, username, password, email, role)
VALUES ('10000000-0000-0000-0000-000000000000', 'username3', 'password2', 'user3@example.com', 'ROLE_ADMIN');


-- Insert example data for BURGER restaurants
-- restaurants
INSERT INTO restaurant (id, account_id, name, type, open_time, close_time, picture_url1, picture_url2)
VALUES ('db38a0d6-af39-487f-80e0-e88995c8b5bd', '13f0c8f0-eec0-4169-ad9f-e8bb408a5325', 'Burger Place 1', 'BURGER',
        '10:00:00', '22:00:00', 'url1', 'url2');

-- menus
INSERT INTO menu (id, name, description, picture_url, price, currency, restaurant_id, created_at,
                  updated_at)
VALUES ('a59ce8e1-9969-4625-a15c-53d5cf671017', 'Classic Burger', 'Delicious classic burger', 'burger_url1', 10000,
        'KRW', 'db38a0d6-af39-487f-80e0-e88995c8b5bd', now(), now()),
       ('5d45696d-232f-40eb-a14f-0c16cff1511a', 'Cheeseburger', 'Tasty cheeseburger with melted cheese', 'burger_url2',
        12000, 'KRW', 'db38a0d6-af39-487f-80e0-e88995c8b5bd', now(), now());

-- option_groups
-- Insert example data for BURGER restaurants
INSERT INTO option_group (id, description, max_select_number, is_necessary, menu_id)
VALUES ('6dbe903a-ae92-4877-b3c5-48f796ee7acb', 'Select options', 1, true, 'a59ce8e1-9969-4625-a15c-53d5cf671017'),
       ('6dbe903a-ae92-4877-b3c5-48f796ee7acc', 'Select options', 1, false, 'a59ce8e1-9969-4625-a15c-53d5cf671017'),
       ('6dbe903a-ae92-4877-b3c5-48f796ee7acd', 'Select options', 1, true, '5d45696d-232f-40eb-a14f-0c16cff1511a'),
       ('6dbe903a-ae92-4877-b3c5-48f796ee7ace', 'Select options', 1, false, '5d45696d-232f-40eb-a14f-0c16cff1511a');

-- options
INSERT INTO option (id, name, cost, currency, option_group_id)
VALUES ('12345678-1234-1234-1234-123456789012', 'Lettuce', 500, 'KRW', '6dbe903a-ae92-4877-b3c5-48f796ee7acb'),
       ('12345678-1234-1234-1234-123456789013', 'Tomato', 500, 'KRW', '6dbe903a-ae92-4877-b3c5-48f796ee7acb'),
       ('12345678-1234-1234-1234-123456789014', 'Onion', 500, 'KRW', '6dbe903a-ae92-4877-b3c5-48f796ee7acb'),
       ('12345678-1234-1234-1234-123456789015', 'Cheddar Cheese', 1000, 'KRW', '6dbe903a-ae92-4877-b3c5-48f796ee7acc'),
       ('12345678-1234-1234-1234-123456789016', 'Swiss Cheese', 1000, 'KRW', '6dbe903a-ae92-4877-b3c5-48f796ee7acc'),
       ('12345678-1234-1234-1234-123456789017', 'Bacon', 1500, 'KRW', '6dbe903a-ae92-4877-b3c5-48f796ee7acc'),
       ('12345678-1234-1234-1234-123456789018', 'Lettuce', 500, 'KRW', '6dbe903a-ae92-4877-b3c5-48f796ee7acd'),
       ('12345678-1234-1234-1234-123456789019', 'Tomato', 500, 'KRW', '6dbe903a-ae92-4877-b3c5-48f796ee7acd'),
       ('12345678-1234-1234-1234-123456789020', 'Onion', 500, 'KRW', '6dbe903a-ae92-4877-b3c5-48f796ee7acd'),
       ('12345678-1234-1234-1234-123456789021', 'Cheddar Cheese', 1000, 'KRW', '6dbe903a-ae92-4877-b3c5-48f796ee7ace'),
       ('12345678-1234-1234-1234-123456789022', 'Swiss Cheese', 1000, 'KRW', '6dbe903a-ae92-4877-b3c5-48f796ee7ace'),
       ('12345678-1234-1234-1234-123456789023', 'Bacon', 1500, 'KRW', '6dbe903a-ae92-4877-b3c5-48f796ee7ace');


-- Insert example data for PIZZA restaurants
-- restaurants
INSERT INTO restaurant (id, account_id, name, type, open_time, close_time, picture_url1, picture_url2)
VALUES ('13f0c8f0-eec0-4169-ad9f-e8bb408a5325', '10000000-0000-0000-0000-000000000000', 'Pizza Place 1', 'PIZZA',
        '11:00:00', '23:00:00', 'pizza_url1', 'pizza_url2');

-- menus
INSERT INTO menu (id, name, description, picture_url, price, currency, restaurant_id, created_at,
                  updated_at)
VALUES ('d1a99a90-fd1d-4c9c-94a5-75d8a6f95fa5', 'Margherita Pizza',
        'Traditional pizza with tomato, mozzarella, and basil', 'pizza1_url1', 15000, 'KRW',
        '13f0c8f0-eec0-4169-ad9f-e8bb408a5325', now(), now()),
       ('3cc8bc8f-9a1e-4a49-9e5f-40da2cd2a3a1', 'Pepperoni Pizza', 'Classic pizza with pepperoni and cheese',
        'pizza1_url2', 17000, 'KRW', '13f0c8f0-eec0-4169-ad9f-e8bb408a5325', now(), now());

-- option_groups
-- Insert example data for PIZZA restaurants
INSERT INTO option_group (id, description, max_select_number, is_necessary, menu_id)
VALUES ('e4db2ad7-0e6f-419d-80de-47e0b89056f3', 'Select options', 1, true, 'd1a99a90-fd1d-4c9c-94a5-75d8a6f95fa5'),
       ('f049a923-6767-469a-9e77-9f15b7884c32', 'Select options', 1, false, 'd1a99a90-fd1d-4c9c-94a5-75d8a6f95fa5'),
       ('cc94d70a-31d1-4b33-9d3d-04de92680144', 'Select options', 1, true, '3cc8bc8f-9a1e-4a49-9e5f-40da2cd2a3a1'),
       ('a0d7e0ed-1684-4a5c-a073-6050a20de8d8', 'Select options', 1, false, '3cc8bc8f-9a1e-4a49-9e5f-40da2cd2a3a1');

-- options
INSERT INTO option (id, name, cost, currency, option_group_id)
VALUES ('63f2852a-55c3-4a77-a8e2-eb039b6c84d1', 'Extra Cheese', 1000, 'KRW', 'e4db2ad7-0e6f-419d-80de-47e0b89056f3'),
       ('cb0de529-4a09-4e56-8b69-8f4e42611ef1', 'Mushroom', 1000, 'KRW', 'e4db2ad7-0e6f-419d-80de-47e0b89056f3'),
       ('67469a69-5d6b-44d8-8503-5dd698e7a4f3', 'Olives', 1000, 'KRW', 'e4db2ad7-0e6f-419d-80de-47e0b89056f3'),
       ('4a9e3c98-2d9c-47c4-9c4b-7477e2ad24b1', 'Pepperoni', 1500, 'KRW', 'f049a923-6767-469a-9e77-9f15b7884c32'),
       ('e1a5248b-cf7f-4ae8-bf78-d0aa225ae733', 'Sausage', 1500, 'KRW', 'f049a923-6767-469a-9e77-9f15b7884c32'),
       ('32d186b8-f10b-49a5-87d9-fc14d0f09a0c', 'Bacon', 1500, 'KRW', 'f049a923-6767-469a-9e77-9f15b7884c32'),
       ('b99d9eb3-e40a-4977-b159-5a96e4749ebf', 'Extra Cheese', 1000, 'KRW', 'cc94d70a-31d1-4b33-9d3d-04de92680144'),
       ('7c13e274-5e32-439e-a0de-85e10e58a6ef', 'Mushroom', 1000, 'KRW', 'cc94d70a-31d1-4b33-9d3d-04de92680144'),
       ('19d82b92-91aa-41cc-bdbd-dfd1e646625d', 'Olives', 1000, 'KRW', 'cc94d70a-31d1-4b33-9d3d-04de92680144'),
       ('7eddd75c-77e8-4d14-a2c1-1862d1753b2c', 'Pepperoni', 1500, 'KRW', 'a0d7e0ed-1684-4a5c-a073-6050a20de8d8'),
       ('a2e267b7-f1de-44cb-b1f5-c556cbf41fc8', 'Sausage', 1500, 'KRW', 'a0d7e0ed-1684-4a5c-a073-6050a20de8d8'),
       ('4e2e0fc7-0c10-4bcb-bd51-02c1eb7a258f', 'Bacon', 1500, 'KRW', 'a0d7e0ed-1684-4a5c-a073-6050a20de8d8');
