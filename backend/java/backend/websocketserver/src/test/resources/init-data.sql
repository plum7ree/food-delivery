INSERT INTO user_schema.account (id, username, password, email, profile_pic_url, role, oauth2provider, oauth2sub,
                                 created_at, updated_at)
VALUES ('1e4bca58-31c3-4af6-bef3-3a342d1013bd', 'john_doe', 'password123', 'john.doe@example.com',
        'https://example.com/profiles/john.jpg', 'USER', 'google', 'sub-12345', NOW(), NOW()),

       ('f6bde1e8-2b8f-4cf4-b291-3b5fc36e3c8e', 'jane_smith', 'password123', 'jane.smith@example.com',
        'https://example.com/profiles/jane.jpg', 'ADMIN', 'facebook', 'sub-67890', NOW(), NOW()),

       ('a1d1c8f7-5b2b-4c97-b87b-9d9c95c3f4f5', 'alice_jones', 'password123', 'alice.jones@example.com',
        'https://example.com/profiles/alice.jpg', 'USER', 'google', 'sub-11223', NOW(), NOW());
