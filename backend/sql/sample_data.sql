USE shike_takeout;

-- 用户数据（5个用户）
INSERT IGNORE INTO `user` (id, openid, nick_name, avatar_url, phone, status) VALUES
(1, 'wx_user_test1', '小明', 'https://randomuser.me/api/portraits/men/1.jpg', '13800138001', 1),
(2, 'wx_user_test2', '小红', 'https://randomuser.me/api/portraits/women/2.jpg', '13800138002', 1),
(3, 'wx_user_test3', '张三', 'https://randomuser.me/api/portraits/men/3.jpg', '13800138003', 1),
(4, 'wx_user_test4', '李四', 'https://randomuser.me/api/portraits/women/4.jpg', '13800138004', 1),
(5, 'wx_user_test5', '王五', 'https://randomuser.me/api/portraits/men/5.jpg', '13800138005', 1);

-- 地址数据
INSERT IGNORE INTO `address` (id, user_id, name, phone, address, is_default) VALUES
(1, 1, '小明', '13800138001', '四川省成都市武侯区天府大道120号', 1),
(2, 1, '小明(公司)', '13800138001', '四川省成都市高新区软件园A座8楼', 0),
(3, 2, '小红', '13800138002', '四川省成都市锦江区春熙路88号', 1),
(4, 3, '张三', '13800138003', '四川省成都市青羊区宽窄巷子15号', 1),
(5, 4, '李四', '13800138004', '四川省成都市金牛区交大路168号', 1),
(6, 5, '王五', '13800138005', '四川省成都市成华区建设路256号', 1);

-- 订单数据（覆盖近7天，各种状态，4个商家都有）
-- status: 1待支付 2待接单 3配送中 4已完成 5已取消

-- 商家1的订单（美味小厨）
INSERT INTO `orders` (order_no, user_id, merchant_id, address_id, address_detail, contact_name, contact_phone, total_amount, discount_amount, pay_amount, delivery_fee, status, remark, pay_time, accept_time, delivery_time, complete_time, create_time) VALUES
('SH2026031101001', 1, 1, 1, '四川省成都市武侯区天府大道120号', '小明', '13800138001', 45.00, 5.00, 40.00, 0.00, 4, '少放辣', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
('SH2026031101002', 2, 1, 3, '四川省成都市锦江区春熙路88号', '小红', '13800138002', 68.00, 0.00, 68.00, 0.00, 4, '', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
('SH2026031101003', 3, 1, 4, '四川省成都市青羊区宽窄巷子15号', '张三', '13800138003', 32.00, 0.00, 32.00, 0.00, 4, '多加醋', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
('SH2026031101004', 4, 1, 5, '四川省成都市金牛区交大路168号', '李四', '13800138004', 55.00, 5.00, 50.00, 0.00, 4, '', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
('SH2026031101005', 1, 1, 2, '四川省成都市高新区软件园A座8楼', '小明', '13800138001', 78.00, 0.00, 78.00, 0.00, 3, '不要香菜', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), NULL, NULL, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('SH2026031101006', 5, 1, 6, '四川省成都市成华区建设路256号', '王五', '13800138005', 42.00, 0.00, 42.00, 0.00, 2, '', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, NULL, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('SH2026031101007', 2, 1, 3, '四川省成都市锦江区春熙路88号', '小红', '13800138002', 35.00, 0.00, 35.00, 0.00, 4, '', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 商家2的订单（快乐炸鸡）
INSERT INTO `orders` (order_no, user_id, merchant_id, address_id, address_detail, contact_name, contact_phone, total_amount, discount_amount, pay_amount, delivery_fee, status, remark, pay_time, accept_time, delivery_time, complete_time, create_time) VALUES
('SH2026031102001', 1, 2, 1, '四川省成都市武侯区天府大道120号', '小明', '13800138001', 56.00, 0.00, 56.00, 2.00, 4, '', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
('SH2026031102002', 3, 2, 4, '四川省成都市青羊区宽窄巷子15号', '张三', '13800138003', 38.00, 0.00, 38.00, 2.00, 4, '多加番茄酱', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
('SH2026031102003', 5, 2, 6, '四川省成都市成华区建设路256号', '王五', '13800138005', 72.00, 5.00, 67.00, 2.00, 4, '', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
('SH2026031102004', 2, 2, 3, '四川省成都市锦江区春熙路88号', '小红', '13800138002', 45.00, 0.00, 45.00, 2.00, 3, '', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, NULL, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('SH2026031102005', 4, 2, 5, '四川省成都市金牛区交大路168号', '李四', '13800138004', 29.00, 0.00, 29.00, 2.00, 5, '', NULL, NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 2 DAY));

-- 商家3的订单（清新茶语）
INSERT INTO `orders` (order_no, user_id, merchant_id, address_id, address_detail, contact_name, contact_phone, total_amount, discount_amount, pay_amount, delivery_fee, status, remark, pay_time, accept_time, delivery_time, complete_time, create_time) VALUES
('SH2026031103001', 2, 3, 3, '四川省成都市锦江区春熙路88号', '小红', '13800138002', 25.00, 0.00, 25.00, 0.00, 4, '少冰', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
('SH2026031103002', 4, 3, 5, '四川省成都市金牛区交大路168号', '李四', '13800138004', 18.00, 0.00, 18.00, 0.00, 4, '半糖', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
('SH2026031103003', 1, 3, 1, '四川省成都市武侯区天府大道120号', '小明', '13800138001', 36.00, 0.00, 36.00, 0.00, 4, '', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
('SH2026031103004', 3, 3, 4, '四川省成都市青羊区宽窄巷子15号', '张三', '13800138003', 22.00, 0.00, 22.00, 0.00, 2, '', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, NULL, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('SH2026031103005', 5, 3, 6, '四川省成都市成华区建设路256号', '王五', '13800138005', 42.00, 0.00, 42.00, 0.00, 4, '无糖', NOW(), NOW(), NOW(), NOW(), NOW());

-- 商家4的订单（轻享沙拉）
INSERT INTO `orders` (order_no, user_id, merchant_id, address_id, address_detail, contact_name, contact_phone, total_amount, discount_amount, pay_amount, delivery_fee, status, remark, pay_time, accept_time, delivery_time, complete_time, create_time) VALUES
('SH2026031104001', 3, 4, 4, '四川省成都市青羊区宽窄巷子15号', '张三', '13800138003', 48.00, 0.00, 48.00, 3.00, 4, '', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
('SH2026031104002', 1, 4, 1, '四川省成都市武侯区天府大道120号', '小明', '13800138001', 35.00, 0.00, 35.00, 3.00, 4, '不要洋葱', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
('SH2026031104003', 5, 4, 6, '四川省成都市成华区建设路256号', '王五', '13800138005', 62.00, 5.00, 57.00, 3.00, 4, '', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
('SH2026031104004', 2, 4, 3, '四川省成都市锦江区春熙路88号', '小红', '13800138002', 28.00, 0.00, 28.00, 3.00, 2, '', NOW(), NOW(), NULL, NULL, NOW());

-- 更新商家月销量
UPDATE merchant SET monthly_sales = 35 WHERE id = 1;
UPDATE merchant SET monthly_sales = 28 WHERE id = 2;
UPDATE merchant SET monthly_sales = 22 WHERE id = 3;
UPDATE merchant SET monthly_sales = 18 WHERE id = 4;

-- 订单项数据（每个订单至少2个菜品）
SET @oid = (SELECT id FROM orders WHERE order_no = 'SH2026031101001');
INSERT INTO order_item (order_id, dish_id, name, image, price, quantity, amount) VALUES
(@oid, 1, '招牌麻辣烫', 'https://images.unsplash.com/photo-1623689048105-a17b1e1936b8?w=400&h=400&fit=crop', 25.00, 1, 25.00),
(@oid, 4, '酸辣粉丝汤', 'https://images.unsplash.com/photo-1585032226651-759b368d7246?w=400&h=400&fit=crop', 20.00, 1, 20.00);

SET @oid = (SELECT id FROM orders WHERE order_no = 'SH2026031101002');
INSERT INTO order_item (order_id, dish_id, name, image, price, quantity, amount) VALUES
(@oid, 2, '原味麻辣烫', 'https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=400&h=400&fit=crop', 22.00, 1, 22.00),
(@oid, 5, '清炒木耳山药', 'https://images.unsplash.com/photo-1543339308-d595f1bec0d3?w=400&h=400&fit=crop', 18.00, 1, 18.00),
(@oid, 6, '地方特色面条', 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=400&h=400&fit=crop', 28.00, 1, 28.00);

SET @oid = (SELECT id FROM orders WHERE order_no = 'SH2026031101003');
INSERT INTO order_item (order_id, dish_id, name, image, price, quantity, amount) VALUES
(@oid, 3, '麻辣烫好吃不贵', 'https://images.unsplash.com/photo-1569058242253-92a9c755a0ec?w=400&h=400&fit=crop', 16.00, 2, 32.00);

SET @oid = (SELECT id FROM orders WHERE order_no = 'SH2026031101004');
INSERT INTO order_item (order_id, dish_id, name, image, price, quantity, amount) VALUES
(@oid, 1, '招牌麻辣烫', 'https://images.unsplash.com/photo-1623689048105-a17b1e1936b8?w=400&h=400&fit=crop', 25.00, 1, 25.00),
(@oid, 7, '手工水饺', 'https://images.unsplash.com/photo-1496116218417-1a781b1c416c?w=400&h=400&fit=crop', 15.00, 2, 30.00);

SET @oid = (SELECT id FROM orders WHERE order_no = 'SH2026031101005');
INSERT INTO order_item (order_id, dish_id, name, image, price, quantity, amount) VALUES
(@oid, 6, '地方特色面条', 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=400&h=400&fit=crop', 28.00, 2, 56.00),
(@oid, 4, '酸辣粉丝汤', 'https://images.unsplash.com/photo-1585032226651-759b368d7246?w=400&h=400&fit=crop', 22.00, 1, 22.00);

SET @oid = (SELECT id FROM orders WHERE order_no = 'SH2026031101006');
INSERT INTO order_item (order_id, dish_id, name, image, price, quantity, amount) VALUES
(@oid, 5, '清炒木耳山药', 'https://images.unsplash.com/photo-1543339308-d595f1bec0d3?w=400&h=400&fit=crop', 18.00, 1, 18.00),
(@oid, 3, '麻辣烫好吃不贵', 'https://images.unsplash.com/photo-1569058242253-92a9c755a0ec?w=400&h=400&fit=crop', 16.00, 1.5, 24.00);

SET @oid = (SELECT id FROM orders WHERE order_no = 'SH2026031101007');
INSERT INTO order_item (order_id, dish_id, name, image, price, quantity, amount) VALUES
(@oid, 7, '手工水饺', 'https://images.unsplash.com/photo-1496116218417-1a781b1c416c?w=400&h=400&fit=crop', 15.00, 1, 15.00),
(@oid, 2, '原味麻辣烫', 'https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=400&h=400&fit=crop', 20.00, 1, 20.00);

-- 商家2订单项
SET @oid = (SELECT id FROM orders WHERE order_no = 'SH2026031102001');
INSERT INTO order_item (order_id, dish_id, name, image, price, quantity, amount) VALUES
(@oid, 8, '黄金炸鸡腿', 'https://images.unsplash.com/photo-1562967914-608f82629710?w=400&h=400&fit=crop', 28.00, 2, 56.00);

SET @oid = (SELECT id FROM orders WHERE order_no = 'SH2026031102002');
INSERT INTO order_item (order_id, dish_id, name, image, price, quantity, amount) VALUES
(@oid, 9, '原味鸡翅(4只)', 'https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=400&h=400&fit=crop', 18.00, 1, 18.00),
(@oid, 10, '经典牛肉汉堡', 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400&h=400&fit=crop', 20.00, 1, 20.00);

SET @oid = (SELECT id FROM orders WHERE order_no = 'SH2026031102003');
INSERT INTO order_item (order_id, dish_id, name, image, price, quantity, amount) VALUES
(@oid, 8, '黄金炸鸡腿', 'https://images.unsplash.com/photo-1562967914-608f82629710?w=400&h=400&fit=crop', 28.00, 1, 28.00),
(@oid, 10, '经典牛肉汉堡', 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400&h=400&fit=crop', 20.00, 2, 40.00);

SET @oid = (SELECT id FROM orders WHERE order_no = 'SH2026031102004');
INSERT INTO order_item (order_id, dish_id, name, image, price, quantity, amount) VALUES
(@oid, 11, '辣翅拼薯条', 'https://images.unsplash.com/photo-1586816001966-79b736744398?w=400&h=400&fit=crop', 25.00, 1, 25.00),
(@oid, 9, '原味鸡翅(4只)', 'https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=400&h=400&fit=crop', 20.00, 1, 20.00);

-- 商家3订单项
SET @oid = (SELECT id FROM orders WHERE order_no = 'SH2026031103001');
INSERT INTO order_item (order_id, dish_id, name, image, price, quantity, amount) VALUES
(@oid, 12, '奶茶(大杯)', 'https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=400&h=400&fit=crop', 15.00, 1, 15.00),
(@oid, 13, '果汁杯(中)', 'https://images.unsplash.com/photo-1581636625402-29b2a704ef13?w=400&h=400&fit=crop', 10.00, 1, 10.00);

SET @oid = (SELECT id FROM orders WHERE order_no = 'SH2026031103002');
INSERT INTO order_item (order_id, dish_id, name, image, price, quantity, amount) VALUES
(@oid, 17, '荔枝甘露', 'https://images.unsplash.com/photo-1615478503562-ec2d8aa0a24a?w=400&h=400&fit=crop', 18.00, 1, 18.00);

SET @oid = (SELECT id FROM orders WHERE order_no = 'SH2026031103003');
INSERT INTO order_item (order_id, dish_id, name, image, price, quantity, amount) VALUES
(@oid, 12, '奶茶(大杯)', 'https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=400&h=400&fit=crop', 15.00, 1, 15.00),
(@oid, 17, '荔枝甘露', 'https://images.unsplash.com/photo-1615478503562-ec2d8aa0a24a?w=400&h=400&fit=crop', 18.00, 1, 18.00);

SET @oid = (SELECT id FROM orders WHERE order_no = 'SH2026031103004');
INSERT INTO order_item (order_id, dish_id, name, image, price, quantity, amount) VALUES
(@oid, 18, '芒果沙冰', 'https://images.unsplash.com/photo-1550304943-4f24f54ddde9?w=400&h=400&fit=crop', 12.00, 1, 12.00),
(@oid, 13, '果汁杯(中)', 'https://images.unsplash.com/photo-1581636625402-29b2a704ef13?w=400&h=400&fit=crop', 10.00, 1, 10.00);

SET @oid = (SELECT id FROM orders WHERE order_no = 'SH2026031103005');
INSERT INTO order_item (order_id, dish_id, name, image, price, quantity, amount) VALUES
(@oid, 12, '奶茶(大杯)', 'https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=400&h=400&fit=crop', 15.00, 2, 30.00),
(@oid, 18, '芒果沙冰', 'https://images.unsplash.com/photo-1550304943-4f24f54ddde9?w=400&h=400&fit=crop', 12.00, 1, 12.00);

-- 商家4订单项
SET @oid = (SELECT id FROM orders WHERE order_no = 'SH2026031104001');
INSERT INTO order_item (order_id, dish_id, name, image, price, quantity, amount) VALUES
(@oid, 14, '凯撒沙拉', 'https://images.unsplash.com/photo-1540420773420-3366772f4999?w=400&h=400&fit=crop', 28.00, 1, 28.00),
(@oid, 16, '烤蔬菜拼盘', 'https://images.unsplash.com/photo-1546173159-315724a31696?w=400&h=400&fit=crop', 20.00, 1, 20.00);

SET @oid = (SELECT id FROM orders WHERE order_no = 'SH2026031104002');
INSERT INTO order_item (order_id, dish_id, name, image, price, quantity, amount) VALUES
(@oid, 19, '水果酸奶沙拉', 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&h=400&fit=crop', 35.00, 1, 35.00);

SET @oid = (SELECT id FROM orders WHERE order_no = 'SH2026031104003');
INSERT INTO order_item (order_id, dish_id, name, image, price, quantity, amount) VALUES
(@oid, 15, '牛油果鲜蔬沙拉', 'https://images.unsplash.com/photo-1627435601361-ec25f5b1d0e5?w=400&h=400&fit=crop', 32.00, 1, 32.00),
(@oid, 14, '凯撒沙拉', 'https://images.unsplash.com/photo-1540420773420-3366772f4999?w=400&h=400&fit=crop', 28.00, 1, 28.00);

SET @oid = (SELECT id FROM orders WHERE order_no = 'SH2026031104004');
INSERT INTO order_item (order_id, dish_id, name, image, price, quantity, amount) VALUES
(@oid, 16, '烤蔬菜拼盘', 'https://images.unsplash.com/photo-1546173159-315724a31696?w=400&h=400&fit=crop', 20.00, 1, 20.00);

-- 评论数据（已完成订单的评价）
INSERT INTO `comment` (user_id, order_id, merchant_id, rating, content, tags) VALUES
(1, (SELECT id FROM orders WHERE order_no = 'SH2026031101001'), 1, 5, '麻辣烫很好吃，分量足，配送也快，下次还点！', '分量足,配送快,味道好'),
(2, (SELECT id FROM orders WHERE order_no = 'SH2026031101002'), 1, 4, '味道不错，就是有点咸了，下次少放点盐', '味道好,稍微咸'),
(3, (SELECT id FROM orders WHERE order_no = 'SH2026031101003'), 1, 5, '性价比很高，好吃不贵，推荐！', '性价比高,推荐'),
(4, (SELECT id FROM orders WHERE order_no = 'SH2026031101004'), 1, 4, '手工水饺皮薄馅大，很不错', '水饺好吃,皮薄馅大'),
(2, (SELECT id FROM orders WHERE order_no = 'SH2026031101007'), 1, 5, '老顾客了，一如既往地好吃', '味道好,常来'),

(1, (SELECT id FROM orders WHERE order_no = 'SH2026031102001'), 2, 5, '炸鸡腿外酥里嫩，非常好吃！', '外酥里嫩,好吃'),
(3, (SELECT id FROM orders WHERE order_no = 'SH2026031102002'), 2, 4, '鸡翅和汉堡都不错，就是等了稍微久了点', '味道好,等待时间长'),
(5, (SELECT id FROM orders WHERE order_no = 'SH2026031102003'), 2, 5, '炸鸡和汉堡的搭配太棒了，分量很足', '搭配好,分量足'),

(2, (SELECT id FROM orders WHERE order_no = 'SH2026031103001'), 3, 5, '奶茶很香醇，果汁也很新鲜，喜欢', '香醇,新鲜'),
(4, (SELECT id FROM orders WHERE order_no = 'SH2026031103002'), 3, 4, '荔枝甘露好好喝，甜度刚好', '好喝,甜度适中'),
(1, (SELECT id FROM orders WHERE order_no = 'SH2026031103003'), 3, 5, '店铺饮品都不错，品质稳定', '品质好,稳定'),
(5, (SELECT id FROM orders WHERE order_no = 'SH2026031103005'), 3, 4, '奶茶香浓，沙冰也很爽口', '香浓,爽口'),

(3, (SELECT id FROM orders WHERE order_no = 'SH2026031104001'), 4, 5, '沙拉新鲜健康，蔬菜很脆，很好吃！', '新鲜健康,口感好'),
(1, (SELECT id FROM orders WHERE order_no = 'SH2026031104002'), 4, 4, '水果酸奶沙拉很清爽，适合夏天', '清爽,健康'),
(5, (SELECT id FROM orders WHERE order_no = 'SH2026031104003'), 4, 5, '牛油果沙拉超赞，食材新鲜，分量也可以', '超赞,食材新鲜');

-- 收藏数据
INSERT IGNORE INTO `favorite` (user_id, merchant_id) VALUES
(1, 1), (1, 3),
(2, 1), (2, 2),
(3, 2), (3, 4),
(4, 3), (4, 1),
(5, 4), (5, 2);

-- 员工数据（给商家添加员工）
INSERT IGNORE INTO `employee` (id, merchant_id, name, username, password, phone, role, status) VALUES
(1, 1, '赵师傅', 'zhao01', 'e10adc3949ba59abbe56e057f20f883e', '13911111111', 'chef', 1),
(2, 1, '钱阿姨', 'qian01', 'e10adc3949ba59abbe56e057f20f883e', '13922222222', 'staff', 1),
(3, 1, '孙师傅', 'sun01', 'e10adc3949ba59abbe56e057f20f883e', '13933333333', 'staff', 1),
(4, 2, '周师傅', 'zhou01', 'e10adc3949ba59abbe56e057f20f883e', '13944444444', 'chef', 1),
(5, 2, '吴小弟', 'wu01', 'e10adc3949ba59abbe56e057f20f883e', '13955555555', 'delivery', 1),
(6, 3, '郑姐', 'zheng01', 'e10adc3949ba59abbe56e057f20f883e', '13966666666', 'staff', 1),
(7, 4, '冯师傅', 'feng01', 'e10adc3949ba59abbe56e057f20f883e', '13977777777', 'chef', 1);
