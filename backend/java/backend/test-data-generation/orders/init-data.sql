-- Insert into orders
INSERT INTO "order".orders(id, customer_id, restaurant_id, tracking_id, price, order_status, failure_messages)
VALUES ('10000000-0000-0000-0010-000000000001', '10000000-0000-0000-0000-000000000001',
        '10000000-0000-0000-0001-000000000001',
        '10000000-0000-0000-0010-000000000002', 100.00, 'PENDING', '');

-- Insert into order_items
INSERT INTO "order".order_items(id, order_id, product_id, price, quantity, sub_total)
VALUES (1, '10000000-0000-0000-0010-000000000001', '10000000-0000-0000-0010-000000000001', 100.00, 1, 100.00);

-- Insert into order_address
INSERT INTO "order".order_address(id, order_id, street, postal_code, city)
VALUES ('10000000-0000-0000-0030-000000000001', '10000000-0000-0000-0010-000000000001', 'test street', '1000AA',
        'test city');

-- Insert into payment_outbox
INSERT INTO "order".payment_outbox(id, saga_id, created_at, saga_type, payload, outbox_status, saga_status,
                                   order_status, version)
VALUES ('10000000-0000-0000-0040-000000000001', '10000000-0000-0001-0000-000000000001', current_timestamp, 'EATS_ORDER',
        '{"price": 100, "orderId": "10000000-0000-0000-0010-000000000001", "createdAt": "2022-01-07T16:21:42.917756+01:00",
         "customerId": "10000000-0000-0000-0000-000000000001", "paymentOrderStatus": "PENDING"}',
        'STARTED', 'STARTED', 'PENDING', 0);
