-- Insert example data for users
INSERT INTO account(id, username, password, email, profile_pic_url, role, oauth2provider, oauth2sub)
VALUES ('10000000-0000-0000-0000-000000000001', 'username1', 'password1', 'user1@example.com', '', 'ROLE_USER',
        'GOOGLE', '10000000000');

INSERT INTO account(id, username, password, email, profile_pic_url, role, oauth2provider, oauth2sub)
VALUES ('10000000-0000-0000-0000-000000000002', 'username2', 'password2', 'user2@example.com', '', 'ROLE_ADMIN',
        'GOOGLE', '10000000001');

INSERT INTO account(id, username, password, email, profile_pic_url, role, oauth2provider, oauth2sub)
VALUES ('10000000-0000-0000-0000-000000000003', 'username3', 'password3', 'user3@example.com', '', 'ROLE_ADMIN',
        'GOOGLE', '10000000002');

-- Insert example data for BURGER restaurants
-- restaurants
INSERT INTO restaurant (id, account_id, name, type, open_time, close_time, picture_url1, picture_url2)
VALUES ('10000000-0000-0000-0001-000000000001', '10000000-0000-0000-0000-000000000001', 'Burger Place 1', 'BURGER',
        '10:00:00', '22:00:00', 'url1', 'url2');

INSERT INTO restaurant (id, account_id, name, type, open_time, close_time, picture_url1, picture_url2)
VALUES ('10000000-0000-0000-0001-000000000002', '10000000-0000-0000-0000-000000000002', 'Burger Place 2', 'BURGER',
        '11:00:00', '23:00:00', 'url3', 'url4');

-- menus
INSERT INTO menu (id, name, description, picture_url, price, currency, restaurant_id, created_at, updated_at)
VALUES ('10000000-0000-0000-0002-000000000001', 'Classic Burger', 'Delicious classic burger', 'burger_url1', 10000,
        'KRW', '10000000-0000-0000-0001-000000000001', now(), now());

INSERT INTO menu (id, name, description, picture_url, price, currency, restaurant_id, created_at, updated_at)
VALUES ('10000000-0000-0000-0002-000000000002', 'Cheeseburger', 'Tasty cheeseburger with melted cheese', 'burger_url2',
        12000, 'KRW', '10000000-0000-0000-0001-000000000001', now(), now());

-- option_groups
INSERT INTO option_group (id, description, max_select_number, is_necessary, menu_id)
VALUES ('10000000-0000-0000-0003-000000000001', 'Select options', 1, true, '10000000-0000-0000-0002-000000000001'),
       ('10000000-0000-0000-0003-000000000002', 'Select options', 1, false, '10000000-0000-0000-0002-000000000001'),
       ('10000000-0000-0000-0003-000000000003', 'Select options', 1, true, '10000000-0000-0000-0002-000000000002'),
       ('10000000-0000-0000-0003-000000000004', 'Select options', 1, false, '10000000-0000-0000-0002-000000000002');

-- options
INSERT INTO option (id, name, cost, currency, option_group_id)
VALUES ('10000000-0000-0000-0004-000000000001', 'Lettuce', 500, 'KRW', '10000000-0000-0000-0003-000000000001'),
       ('10000000-0000-0000-0004-000000000002', 'Tomato', 500, 'KRW', '10000000-0000-0000-0003-000000000001'),
       ('10000000-0000-0000-0004-000000000003', 'Onion', 500, 'KRW', '10000000-0000-0000-0003-000000000001'),
       ('10000000-0000-0000-0004-000000000004', 'Cheddar Cheese', 1000, 'KRW', '10000000-0000-0000-0003-000000000002'),
       ('10000000-0000-0000-0004-000000000005', 'Swiss Cheese', 1000, 'KRW', '10000000-0000-0000-0003-000000000002'),
       ('10000000-0000-0000-0004-000000000006', 'Bacon', 1500, 'KRW', '10000000-0000-0000-0003-000000000002'),
       ('10000000-0000-0000-0004-000000000007', 'Lettuce', 500, 'KRW', '10000000-0000-0000-0003-000000000003'),
       ('10000000-0000-0000-0004-000000000008', 'Tomato', 500, 'KRW', '10000000-0000-0000-0003-000000000003'),
       ('10000000-0000-0000-0004-000000000009', 'Onion', 500, 'KRW', '10000000-0000-0000-0003-000000000003'),
       ('10000000-0000-0000-0004-000000000010', 'Cheddar Cheese', 1000, 'KRW', '10000000-0000-0000-0003-000000000004'),
       ('10000000-0000-0000-0004-000000000011', 'Swiss Cheese', 1000, 'KRW', '10000000-0000-0000-0003-000000000004'),
       ('10000000-0000-0000-0004-000000000012', 'Bacon', 1500, 'KRW', '10000000-0000-0000-0003-000000000004');

-- Insert example data for PIZZA restaurants
-- restaurants
INSERT INTO restaurant (id, account_id, name, type, open_time, close_time, picture_url1, picture_url2)
VALUES ('10000000-0000-0000-0001-000000000003', '10000000-0000-0000-0000-000000000003', 'Pizza Place 1', 'PIZZA',
        '11:00:00', '23:00:00', 'pizza_url1', 'pizza_url2');

-- menus
INSERT INTO menu (id, name, description, picture_url, price, currency, restaurant_id, created_at, updated_at)
VALUES ('10000000-0000-0000-0002-000000000003', 'Margherita Pizza',
        'Traditional pizza with tomato, mozzarella, and basil', 'pizza1_url1', 15000, 'KRW',
        '10000000-0000-0000-0001-000000000003', now(), now());

INSERT INTO menu (id, name, description, picture_url, price, currency, restaurant_id, created_at, updated_at)
VALUES ('10000000-0000-0000-0002-000000000004', 'Pepperoni Pizza', 'Classic pizza with pepperoni and cheese',
        'pizza1_url2', 17000, 'KRW', '10000000-0000-0000-0001-000000000003', now(), now());

-- option_groups
INSERT INTO option_group (id, description, max_select_number, is_necessary, menu_id)
VALUES ('10000000-0000-0000-0003-000000000005', 'Select options', 1, true, '10000000-0000-0000-0002-000000000003'),
       ('10000000-0000-0000-0003-000000000006', 'Select options', 1, false, '10000000-0000-0000-0002-000000000003'),
       ('10000000-0000-0000-0003-000000000007', 'Select options', 1, true, '10000000-0000-0000-0002-000000000004'),
       ('10000000-0000-0000-0003-000000000008', 'Select options', 1, false, '10000000-0000-0000-0002-000000000004');

-- options
INSERT INTO option (id, name, cost, currency, option_group_id)
VALUES ('10000000-0000-0000-0004-000000000013', 'Extra Cheese', 1000, 'KRW', '10000000-0000-0000-0003-000000000005'),
       ('10000000-0000-0000-0004-000000000014', 'Mushroom', 1000, 'KRW', '10000000-0000-0000-0003-000000000005'),
       ('10000000-0000-0000-0004-000000000015', 'Olives', 1000, 'KRW', '10000000-0000-0000-0003-000000000005'),
       ('10000000-0000-0000-0004-000000000016', 'Pepperoni', 1500, 'KRW', '10000000-0000-0000-0003-000000000006'),
       ('10000000-0000-0000-0004-000000000017', 'Sausage', 1500, 'KRW', '10000000-0000-0000-0003-000000000006'),
       ('10000000-0000-0000-0004-000000000018', 'Bacon', 1500, 'KRW', '10000000-0000-0000-0003-000000000006'),
       ('10000000-0000-0000-0004-000000000019', 'Extra Cheese', 1000, 'KRW', '10000000-0000-0000-0003-000000000007'),
       ('10000000-0000-0000-0004-000000000020', 'Mushroom', 1000, 'KRW', '10000000-0000-0000-0003-000000000007'),
       ('10000000-0000-0000-0004-000000000021', 'Olives', 1000, 'KRW', '10000000-0000-0000-0003-000000000007'),
       ('10000000-0000-0000-0004-000000000022', 'Pepperoni', 1500, 'KRW', '10000000-0000-0000-0003-000000000008'),
       ('10000000-0000-0000-0004-000000000023', 'Sausage', 1500, 'KRW', '10000000-0000-0000-0003-000000000008'),
       ('10000000-0000-0000-0004-000000000024', 'Bacon', 1500, 'KRW', '10000000-0000-0000-0003-000000000008');
