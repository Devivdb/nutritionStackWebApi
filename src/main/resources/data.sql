-- Test data for Nutrition Stack Web API

-- Users
INSERT INTO users (id, username, password, role, created_at) VALUES
(1, 'testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'USER', CURRENT_TIMESTAMP),
(2, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'ADMIN', CURRENT_TIMESTAMP),
(3, 'nutritionist', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'USER', CURRENT_TIMESTAMP);

-- Products
INSERT INTO products (ean13_code, product_name, amount, unit, calories, protein, carbs, fat, fiber, sugar, salt, created_by, created_at) VALUES
('1234567890123', 'Organic Bananas', 100, 'G', 89, 1.1, 22.8, 0.3, 2.6, 12.2, 0.0, 1, CURRENT_TIMESTAMP),
('1234567890124', 'Fresh Apples', 100, 'G', 52, 0.3, 13.8, 0.2, 2.4, 10.4, 0.0, 1, CURRENT_TIMESTAMP),
('1234567890125', 'Chicken Breast Fillet', 100, 'G', 165, 31.0, 0.0, 3.6, 0.0, 0.0, 0.2, 1, CURRENT_TIMESTAMP),
('1234567890126', 'Salmon Fillet', 100, 'G', 208, 25.0, 0.0, 12.0, 0.0, 0.0, 0.1, 1, CURRENT_TIMESTAMP),
('1234567890127', 'Eggs (Large)', 100, 'G', 155, 12.6, 1.1, 10.6, 0.0, 1.1, 0.3, 1, CURRENT_TIMESTAMP),
('1234567890128', 'Brown Rice', 100, 'G', 111, 2.6, 23.0, 0.9, 1.8, 0.4, 0.0, 1, CURRENT_TIMESTAMP),
('1234567890129', 'Whole Grain Bread', 100, 'G', 247, 13.0, 41.0, 4.2, 7.0, 6.0, 0.5, 1, CURRENT_TIMESTAMP),
('1234567890130', 'Greek Yogurt', 100, 'G', 59, 10.0, 3.6, 0.4, 0.0, 3.2, 0.1, 1, CURRENT_TIMESTAMP),
('1234567890131', 'Almond Milk', 100, 'ML', 13, 0.4, 0.6, 1.1, 0.3, 0.6, 0.1, 1, CURRENT_TIMESTAMP),
('1234567890132', 'Spinach', 100, 'G', 23, 2.9, 3.6, 0.4, 2.2, 0.4, 0.1, 1, CURRENT_TIMESTAMP),
('1234567890133', 'Sweet Potato', 100, 'G', 86, 1.6, 20.1, 0.1, 3.0, 4.2, 0.0, 1, CURRENT_TIMESTAMP),
('1234567890134', 'Almonds', 100, 'G', 579, 21.2, 21.7, 49.9, 12.5, 4.8, 0.0, 1, CURRENT_TIMESTAMP),
('1234567890135', 'Chia Seeds', 100, 'G', 486, 17.0, 42.0, 31.0, 34.0, 0.0, 0.0, 1, CURRENT_TIMESTAMP);

-- Meals
INSERT INTO meals (id, meal_name, meal_type, created_by, created_at) VALUES
(1, 'Healthy Breakfast', 'BREAKFAST', 1, CURRENT_TIMESTAMP),
(2, 'Protein Lunch', 'LUNCH', 1, CURRENT_TIMESTAMP),
(3, 'Light Dinner', 'DINNER', 1, CURRENT_TIMESTAMP),
(4, 'Energy Snack', 'SNACK', 1, CURRENT_TIMESTAMP),
(5, 'Vegetarian Lunch', 'LUNCH', 3, CURRENT_TIMESTAMP),
(6, 'Quick Breakfast', 'BREAKFAST', 3, CURRENT_TIMESTAMP);

-- Meal products
INSERT INTO meal_products (id, meal_id, ean13_code, quantity, unit, created_at) VALUES
(1, 1, '1234567890123', 120.0, 'G', CURRENT_TIMESTAMP),
(2, 1, '1234567890130', 200.0, 'G', CURRENT_TIMESTAMP),
(3, 1, '1234567890134', 30.0, 'G', CURRENT_TIMESTAMP),
(4, 2, '1234567890125', 150.0, 'G', CURRENT_TIMESTAMP),
(5, 2, '1234567890128', 100.0, 'G', CURRENT_TIMESTAMP),
(6, 2, '1234567890132', 50.0, 'G', CURRENT_TIMESTAMP),
(7, 2, '1234567890127', 100.0, 'G', CURRENT_TIMESTAMP),
(8, 3, '1234567890126', 120.0, 'G', CURRENT_TIMESTAMP),
(9, 3, '1234567890133', 150.0, 'G', CURRENT_TIMESTAMP),
(10, 4, '1234567890135', 25.0, 'G', CURRENT_TIMESTAMP),
(11, 5, '1234567890128', 120.0, 'G', CURRENT_TIMESTAMP),
(12, 5, '1234567890132', 80.0, 'G', CURRENT_TIMESTAMP),
(13, 5, '1234567890130', 150.0, 'G', CURRENT_TIMESTAMP),
(14, 6, '1234567890129', 60.0, 'G', CURRENT_TIMESTAMP),
(15, 6, '1234567890131', 250.0, 'ML', CURRENT_TIMESTAMP);

-- Logged products
INSERT INTO logged_products (id, user_id, ean13_code, quantity, unit, meal_type, log_date, created_at) VALUES
(1, 1, '1234567890123', 100.0, 'G', 'BREAKFAST', CURRENT_DATE - INTERVAL '1 day', CURRENT_TIMESTAMP),
(2, 1, '1234567890130', 150.0, 'G', 'BREAKFAST', CURRENT_DATE - INTERVAL '1 day', CURRENT_TIMESTAMP),
(3, 1, '1234567890125', 120.0, 'G', 'LUNCH', CURRENT_DATE - INTERVAL '1 day', CURRENT_TIMESTAMP),
(4, 1, '1234567890128', 80.0, 'G', 'LUNCH', CURRENT_DATE - INTERVAL '1 day', CURRENT_TIMESTAMP),
(5, 1, '1234567890126', 100.0, 'G', 'DINNER', CURRENT_DATE - INTERVAL '2 days', CURRENT_TIMESTAMP),
(6, 1, '1234567890133', 120.0, 'G', 'DINNER', CURRENT_DATE - INTERVAL '2 days', CURRENT_TIMESTAMP),
(7, 3, '1234567890128', 100.0, 'G', 'LUNCH', CURRENT_DATE - INTERVAL '1 day', CURRENT_TIMESTAMP),
(8, 3, '1234567890132', 60.0, 'G', 'LUNCH', CURRENT_DATE - INTERVAL '1 day', CURRENT_TIMESTAMP),
(9, 3, '1234567890129', 50.0, 'G', 'BREAKFAST', CURRENT_DATE - INTERVAL '3 days', CURRENT_TIMESTAMP),
(10, 3, '1234567890131', 200.0, 'ML', 'BREAKFAST', CURRENT_DATE - INTERVAL '3 days', CURRENT_TIMESTAMP);

-- Bulk uploads
INSERT INTO bulk_uploads (id, file_name, product_count, status, uploaded_by, uploaded_at) VALUES
(1, 'initial_products.json', 15, 'COMPLETED', 2, CURRENT_TIMESTAMP - INTERVAL '1 hour'),
(2, 'supplemental_products.json', 0, 'FAILED', 2, CURRENT_TIMESTAMP - INTERVAL '30 minutes');

-- Reset sequences
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('meals_id_seq', (SELECT MAX(id) FROM meals));
SELECT setval('meal_products_id_seq', (SELECT MAX(id) FROM meal_products));
SELECT setval('logged_products_id_seq', (SELECT MAX(id) FROM logged_products));
SELECT setval('bulk_uploads_id_seq', (SELECT MAX(id) FROM bulk_uploads));
