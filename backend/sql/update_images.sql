USE shike_takeout;

-- 更新商家logo
UPDATE merchant SET logo = 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=200&h=200&fit=crop' WHERE id = 1;
UPDATE merchant SET logo = 'https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58?w=200&h=200&fit=crop' WHERE id = 2;
UPDATE merchant SET logo = 'https://images.unsplash.com/photo-1558857563-b371033873b8?w=200&h=200&fit=crop' WHERE id = 3;
UPDATE merchant SET logo = 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=200&h=200&fit=crop' WHERE id = 4;

-- 更新菜品图片
-- 美味小馆
UPDATE dish SET image = 'https://images.unsplash.com/photo-1623689048105-a17b1e1936b8?w=400&h=300&fit=crop' WHERE id = 1;
UPDATE dish SET image = 'https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=400&h=300&fit=crop' WHERE id = 2;
UPDATE dish SET image = 'https://images.unsplash.com/photo-1569058242253-92a9c755a0ec?w=400&h=300&fit=crop' WHERE id = 3;
UPDATE dish SET image = 'https://images.unsplash.com/photo-1585032226651-759b368d7246?w=400&h=300&fit=crop' WHERE id = 4;
UPDATE dish SET image = 'https://images.unsplash.com/photo-1543339308-d595f1bec0d3?w=400&h=300&fit=crop' WHERE id = 5;
UPDATE dish SET image = 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=400&h=300&fit=crop' WHERE id = 6;
UPDATE dish SET image = 'https://images.unsplash.com/photo-1496116218417-1a781b1c416c?w=400&h=300&fit=crop' WHERE id = 7;
-- 快乐炸鸡
UPDATE dish SET image = 'https://images.unsplash.com/photo-1562967914-608f82629710?w=400&h=300&fit=crop' WHERE id = 8;
UPDATE dish SET image = 'https://images.unsplash.com/photo-1527477396000-e27163b4bbed?w=400&h=300&fit=crop' WHERE id = 9;
UPDATE dish SET image = 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400&h=300&fit=crop' WHERE id = 10;
UPDATE dish SET image = 'https://images.unsplash.com/photo-1586816001966-79b736744398?w=400&h=300&fit=crop' WHERE id = 11;
UPDATE dish SET image = 'https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=400&h=300&fit=crop' WHERE id = 12;
UPDATE dish SET image = 'https://images.unsplash.com/photo-1581636625402-29b2a704ef13?w=400&h=300&fit=crop' WHERE id = 13;
-- 清新茶语
UPDATE dish SET image = 'https://images.unsplash.com/photo-1558857563-b371033873b8?w=400&h=300&fit=crop' WHERE id = 14;
UPDATE dish SET image = 'https://images.unsplash.com/photo-1627435601361-ec25f5b1d0e5?w=400&h=300&fit=crop' WHERE id = 15;
UPDATE dish SET image = 'https://images.unsplash.com/photo-1546173159-315724a31696?w=400&h=300&fit=crop' WHERE id = 16;
UPDATE dish SET image = 'https://images.unsplash.com/photo-1615478503562-ec2d8aa0a24a?w=400&h=300&fit=crop' WHERE id = 17;
-- 轻享沙拉
UPDATE dish SET image = 'https://images.unsplash.com/photo-1550304943-4f24f54ddde9?w=400&h=300&fit=crop' WHERE id = 18;
UPDATE dish SET image = 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&h=300&fit=crop' WHERE id = 19;
UPDATE dish SET image = 'https://images.unsplash.com/photo-1528735602780-2552fd46c7af?w=400&h=300&fit=crop' WHERE id = 20;

-- 更新平台名称
UPDATE system_config SET config_value = '食刻' WHERE config_key = 'platform_name';
