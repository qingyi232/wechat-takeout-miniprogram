-- 食刻外卖数据库初始化脚本
CREATE DATABASE IF NOT EXISTS shike_takeout DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE shike_takeout;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `openid` VARCHAR(64) DEFAULT NULL COMMENT '微信openid',
    `nick_name` VARCHAR(64) DEFAULT NULL COMMENT '昵称',
    `avatar_url` VARCHAR(512) DEFAULT NULL COMMENT '头像URL',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `status` TINYINT DEFAULT 1 COMMENT '状态 1正常 0禁用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_openid` (`openid`)
) ENGINE=InnoDB COMMENT='用户表';

-- 商家表
CREATE TABLE IF NOT EXISTS `merchant` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(128) NOT NULL COMMENT '商家名称',
    `logo` VARCHAR(512) DEFAULT NULL COMMENT '商家logo',
    `description` VARCHAR(512) DEFAULT NULL COMMENT '商家描述',
    `address` VARCHAR(256) DEFAULT NULL COMMENT '商家地址',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `rating` DECIMAL(2,1) DEFAULT 5.0 COMMENT '评分',
    `monthly_sales` INT DEFAULT 0 COMMENT '月销量',
    `delivery_time` INT DEFAULT 30 COMMENT '配送时间(分钟)',
    `delivery_fee` DECIMAL(10,2) DEFAULT 0 COMMENT '配送费',
    `free_delivery` TINYINT DEFAULT 0 COMMENT '是否免配送费',
    `platform_delivery` TINYINT DEFAULT 1 COMMENT '是否平台配送',
    `category_type` VARCHAR(64) DEFAULT NULL COMMENT '商家分类',
    `business_hours` VARCHAR(64) DEFAULT '08:00-22:00' COMMENT '营业时间',
    `status` TINYINT DEFAULT 1 COMMENT '状态 1营业 0休息 2审核中',
    `account` VARCHAR(64) DEFAULT NULL COMMENT '商家登录账号',
    `password` VARCHAR(128) DEFAULT NULL COMMENT '商家登录密码',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='商家表';

-- 员工表
CREATE TABLE IF NOT EXISTS `employee` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `merchant_id` BIGINT NOT NULL COMMENT '所属商家',
    `name` VARCHAR(64) NOT NULL COMMENT '员工姓名',
    `username` VARCHAR(64) NOT NULL COMMENT '登录账号',
    `password` VARCHAR(128) NOT NULL COMMENT '登录密码',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `role` VARCHAR(32) DEFAULT 'staff' COMMENT '角色 admin/staff',
    `status` TINYINT DEFAULT 1 COMMENT '状态 1启用 0禁用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB COMMENT='员工表';

-- 菜品分类表
CREATE TABLE IF NOT EXISTS `category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `merchant_id` BIGINT NOT NULL COMMENT '所属商家',
    `name` VARCHAR(64) NOT NULL COMMENT '分类名称',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态 1启用 0禁用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB COMMENT='菜品分类表';

-- 菜品表
CREATE TABLE IF NOT EXISTS `dish` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `merchant_id` BIGINT NOT NULL COMMENT '所属商家',
    `category_id` BIGINT NOT NULL COMMENT '所属分类',
    `name` VARCHAR(128) NOT NULL COMMENT '菜品名称',
    `description` VARCHAR(512) DEFAULT NULL COMMENT '菜品描述',
    `image` VARCHAR(512) DEFAULT NULL COMMENT '菜品图片',
    `original_price` DECIMAL(10,2) NOT NULL COMMENT '原价',
    `current_price` DECIMAL(10,2) NOT NULL COMMENT '现价',
    `discount` DECIMAL(3,2) DEFAULT 1.00 COMMENT '折扣',
    `stock` INT DEFAULT 0 COMMENT '库存',
    `sales` INT DEFAULT 0 COMMENT '销量',
    `status` TINYINT DEFAULT 1 COMMENT '状态 1启售 0停售',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB COMMENT='菜品表';

-- 套餐表
CREATE TABLE IF NOT EXISTS `combo` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `merchant_id` BIGINT NOT NULL COMMENT '所属商家',
    `category_id` BIGINT DEFAULT NULL COMMENT '所属分类',
    `name` VARCHAR(128) NOT NULL COMMENT '套餐名称',
    `description` VARCHAR(512) DEFAULT NULL COMMENT '套餐描述',
    `image` VARCHAR(512) DEFAULT NULL COMMENT '套餐图片',
    `price` DECIMAL(10,2) NOT NULL COMMENT '套餐价格',
    `status` TINYINT DEFAULT 1 COMMENT '状态 1启售 0停售',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB COMMENT='套餐表';

-- 套餐菜品关联表
CREATE TABLE IF NOT EXISTS `combo_dish` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `combo_id` BIGINT NOT NULL COMMENT '套餐ID',
    `dish_id` BIGINT NOT NULL COMMENT '菜品ID',
    `quantity` INT DEFAULT 1 COMMENT '数量',
    PRIMARY KEY (`id`),
    KEY `idx_combo_id` (`combo_id`)
) ENGINE=InnoDB COMMENT='套餐菜品关联表';

-- 收货地址表
CREATE TABLE IF NOT EXISTS `address` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `name` VARCHAR(64) NOT NULL COMMENT '联系人',
    `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
    `address` VARCHAR(256) NOT NULL COMMENT '详细地址',
    `is_default` TINYINT DEFAULT 0 COMMENT '是否默认 1是 0否',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB COMMENT='收货地址表';

-- 订单表
CREATE TABLE IF NOT EXISTS `orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_no` VARCHAR(64) NOT NULL COMMENT '订单编号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商家ID',
    `address_id` BIGINT DEFAULT NULL COMMENT '地址ID',
    `address_detail` VARCHAR(256) DEFAULT NULL COMMENT '收货地址详情',
    `contact_name` VARCHAR(64) DEFAULT NULL COMMENT '联系人',
    `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    `discount_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '优惠金额',
    `pay_amount` DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    `delivery_fee` DECIMAL(10,2) DEFAULT 0 COMMENT '配送费',
    `status` TINYINT DEFAULT 1 COMMENT '订单状态 1待支付 2待接单 3配送中 4已完成 5已取消 6已退款',
    `remark` VARCHAR(256) DEFAULT NULL COMMENT '备注',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `accept_time` DATETIME DEFAULT NULL COMMENT '接单时间',
    `delivery_time` DATETIME DEFAULT NULL COMMENT '送达时间',
    `complete_time` DATETIME DEFAULT NULL COMMENT '完成时间',
    `cancel_time` DATETIME DEFAULT NULL COMMENT '取消时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB COMMENT='订单表';

-- 订单明细表
CREATE TABLE IF NOT EXISTS `order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `dish_id` BIGINT DEFAULT NULL COMMENT '菜品ID',
    `combo_id` BIGINT DEFAULT NULL COMMENT '套餐ID',
    `name` VARCHAR(128) NOT NULL COMMENT '名称',
    `image` VARCHAR(512) DEFAULT NULL COMMENT '图片',
    `price` DECIMAL(10,2) NOT NULL COMMENT '单价',
    `quantity` INT NOT NULL COMMENT '数量',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '小计',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB COMMENT='订单明细表';

-- 评价表
CREATE TABLE IF NOT EXISTS `comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商家ID',
    `rating` TINYINT DEFAULT 5 COMMENT '评分 1-5',
    `content` VARCHAR(512) DEFAULT NULL COMMENT '评价内容',
    `tags` VARCHAR(256) DEFAULT NULL COMMENT '评价标签',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB COMMENT='评价表';

-- 收藏表
CREATE TABLE IF NOT EXISTS `favorite` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商家ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_merchant` (`user_id`, `merchant_id`)
) ENGINE=InnoDB COMMENT='收藏表';

-- 系统配置表
CREATE TABLE IF NOT EXISTS `system_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `config_key` VARCHAR(64) NOT NULL COMMENT '配置键',
    `config_value` VARCHAR(256) NOT NULL COMMENT '配置值',
    `description` VARCHAR(256) DEFAULT NULL COMMENT '描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB COMMENT='系统配置表';

-- 管理员表
CREATE TABLE IF NOT EXISTS `admin` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(64) NOT NULL COMMENT '账号',
    `password` VARCHAR(128) NOT NULL COMMENT '密码',
    `name` VARCHAR(64) DEFAULT NULL COMMENT '姓名',
    `status` TINYINT DEFAULT 1 COMMENT '状态 1启用 0禁用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB COMMENT='管理员表';

-- 初始数据
INSERT INTO `admin` (`username`, `password`, `name`) VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员');

INSERT INTO `system_config` (`config_key`, `config_value`, `description`) VALUES
('order_timeout', '30', '订单超时时间(分钟)'),
('default_delivery_fee', '5', '默认配送费(元)'),
('platform_name', '考拉外卖', '平台名称');

INSERT INTO `merchant` (`name`, `logo`, `description`, `address`, `phone`, `rating`, `monthly_sales`, `delivery_time`, `delivery_fee`, `free_delivery`, `platform_delivery`, `category_type`, `account`, `password`, `status`) VALUES
('美味小馆', '', '美味小馆，你想吃的都在这里', '南京西路120号', '13788668899', 4.5, 5, 30, 0, 1, 1, '特色美味', 'merchant1', 'e10adc3949ba59abbe56e057f20f883e', 1),
('快乐炸鸡', '', '新鲜现炸，外酥里嫩', '人民路88号', '13566778899', 4.3, 12, 25, 2, 0, 1, '炸鸡汉堡', 'merchant2', 'e10adc3949ba59abbe56e057f20f883e', 1),
('清新茶语', '', '精选好茶，新鲜现做', '中山路256号', '13899667788', 4.7, 20, 20, 0, 1, 1, '奶茶饮品', 'merchant3', 'e10adc3949ba59abbe56e057f20f883e', 1),
('轻享沙拉', '', '健康轻食，美味不减', '建设路168号', '13677889900', 4.6, 8, 35, 3, 0, 1, '轻食简餐', 'merchant4', 'e10adc3949ba59abbe56e057f20f883e', 1);

INSERT INTO `category` (`merchant_id`, `name`, `sort`) VALUES
(1, '香辣卤味', 1), (1, '地方特色', 2), (1, '家常小菜', 3),
(2, '炸鸡系列', 1), (2, '汉堡系列', 2), (2, '小食饮品', 3),
(3, '招牌奶茶', 1), (3, '果茶系列', 2), (3, '特调饮品', 3),
(4, '沙拉系列', 1), (4, '轻食套餐', 2);

INSERT INTO `dish` (`merchant_id`, `category_id`, `name`, `description`, `original_price`, `current_price`, `discount`, `stock`, `sales`) VALUES
(1, 1, '家常特色卤味', '特色卤味，特别下饭', 19.00, 17.10, 0.90, 50, 2),
(1, 1, '原味特色卤味', '家常美味，特别下饭', 22.00, 19.80, 0.90, 30, 1),
(1, 1, '特色卤味好吃不贵', '家常美味，特别下饭', 22.00, 19.80, 0.90, 40, 2),
(1, 3, '酸辣土豆丝', '经典家常菜，酸辣开胃', 16.00, 14.40, 0.90, 60, 8),
(1, 3, '清炒木耳山药', '营养健康，清淡可口', 22.00, 19.80, 0.90, 25, 3),
(1, 2, '地方特色烤鱼', '鲜嫩多汁，特色风味', 38.00, 34.20, 0.90, 15, 5),
(1, 2, '手工水饺', '皮薄馅大，鲜香可口', 25.00, 22.50, 0.90, 35, 6),
(2, 4, '香辣炸鸡腿', '外酥里嫩，香辣入味', 15.00, 12.80, 0.85, 100, 20),
(2, 4, '原味鸡翅(4只)', '黄金脆皮，原汁原味', 18.00, 15.30, 0.85, 80, 15),
(2, 5, '经典牛肉汉堡', '纯正牛肉饼，经典美味', 22.00, 18.70, 0.85, 50, 10),
(2, 5, '鸡腿堡套餐', '汉堡+薯条+可乐', 32.00, 27.20, 0.85, 40, 8),
(2, 6, '薯条(大份)', '金黄酥脆，美味可口', 12.00, 10.20, 0.85, 120, 25),
(2, 6, '冰可乐(大杯)', '冰凉爽口', 8.00, 6.80, 0.85, 200, 30),
(3, 7, '珍珠奶茶', '经典招牌，Q弹珍珠', 15.00, 12.80, 0.85, 200, 50),
(3, 7, '芋泥波波奶茶', '香浓芋泥，口感丰富', 18.00, 15.30, 0.85, 150, 35),
(3, 8, '满杯百香果', '新鲜百香果，酸甜可口', 16.00, 13.60, 0.85, 180, 28),
(3, 8, '杨枝甘露', '芒果西柚，热带风情', 20.00, 17.00, 0.85, 100, 22),
(4, 10, '凯撒沙拉', '经典配方，营养均衡', 28.00, 25.20, 0.90, 30, 12),
(4, 10, '鸡胸肉沙拉', '高蛋白低脂，健身首选', 32.00, 28.80, 0.90, 25, 9),
(4, 11, '全麦三明治套餐', '全麦面包+沙拉+果汁', 35.00, 31.50, 0.90, 20, 7);

-- 公告活动表
CREATE TABLE IF NOT EXISTS `announcement` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL COMMENT '标题',
  `content` varchar(500) DEFAULT '' COMMENT '内容',
  `type` int DEFAULT 1 COMMENT '类型：1公告 2活动',
  `status` int DEFAULT 1 COMMENT '状态：0下线 1上线',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告活动表';

INSERT INTO `announcement` (`title`, `content`, `type`, `status`) VALUES
('欢迎使用食刻外卖', '新用户下单立减5元，快来体验吧', 1, 1),
('今日特惠活动', '全场满30减5，限时优惠', 2, 1),
('食刻外卖开业大吉', '精选商家入驻，品质保障，配送到家', 1, 1);

-- 地址表添加经纬度
ALTER TABLE `address` ADD COLUMN IF NOT EXISTS `latitude` decimal(10,6) DEFAULT 0 COMMENT '纬度' AFTER `address`;
ALTER TABLE `address` ADD COLUMN IF NOT EXISTS `longitude` decimal(10,6) DEFAULT 0 COMMENT '经度' AFTER `latitude`;

-- 商家入驻申请表添加经纬度
ALTER TABLE `merchant_apply` ADD COLUMN IF NOT EXISTS `latitude` decimal(10,6) DEFAULT 0 COMMENT '纬度' AFTER `address`;
ALTER TABLE `merchant_apply` ADD COLUMN IF NOT EXISTS `longitude` decimal(10,6) DEFAULT 0 COMMENT '经度' AFTER `latitude`;
