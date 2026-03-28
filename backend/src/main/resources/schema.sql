SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection = utf8mb4;

CREATE DATABASE IF NOT EXISTS powerbank DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE powerbank;

SET time_zone = '+08:00';

DROP TABLE IF EXISTS rental_order;
DROP TABLE IF EXISTS power_bank;
DROP TABLE IF EXISTS sys_user;

CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '账户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    id_card VARCHAR(18) NOT NULL COMMENT '身份证号',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    role INT NOT NULL DEFAULT 0 COMMENT '角色 0-用户 1-管理员',
    credit_score INT NOT NULL DEFAULT 100 COMMENT '信誉分',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE TABLE power_bank (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(32) NOT NULL UNIQUE COMMENT '充电宝编号',
    status INT NOT NULL DEFAULT 0 COMMENT '状态 0-空闲 1-占用 2-故障',
    hourly_rate DECIMAL(10,2) NOT NULL DEFAULT 2.00 COMMENT '每小时费用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='充电宝表';

CREATE TABLE rental_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    power_bank_id BIGINT NOT NULL COMMENT '充电宝ID',
    rental_time DATETIME NOT NULL COMMENT '租赁时间',
    return_time DATETIME DEFAULT NULL COMMENT '归还时间',
    deposit DECIMAL(10,2) NOT NULL DEFAULT 99.00 COMMENT '押金',
    deposit_status INT NOT NULL DEFAULT 1 COMMENT '押金状态 0-未付 1-已付 2-已退',
    total_fee DECIMAL(10,2) DEFAULT NULL COMMENT '总费用',
    payment_status INT NOT NULL DEFAULT 0 COMMENT '支付状态 0-未支付 1-已支付',
    status INT NOT NULL DEFAULT 0 COMMENT '订单状态 0-进行中 1-已归还 2-超时',
    overtime INT NOT NULL DEFAULT 0 COMMENT '是否超时 0-否 1-是',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
    CONSTRAINT fk_order_powerbank FOREIGN KEY (power_bank_id) REFERENCES power_bank(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租赁订单表';

-- ========== 初始数据 ==========

-- 管理员账号 密码: admin123 (BCrypt 加密)
INSERT INTO sys_user (username, password, real_name, id_card, phone, role, credit_score) VALUES
('admin', '$2b$10$o09ivur64eT.Rjzbkkizp.tLajY3wAcQnGvOpmdOdpu.JRt03kkNO', '系统管理员', '110101199001011234', '13800000000', 1, 100);

-- 测试用户 (7个用户，确保各页面数据充足)
-- testuser 密码: user123 (BCrypt 加密)
INSERT INTO sys_user (username, password, real_name, id_card, phone, role, credit_score) VALUES
('testuser', '$2b$10$/4TIfKQgHuD/BbxnMsYBa.TJ8mGnFxnWsMuALpxobxPIthoJSARAu', '张三', '110101199501011234', '13900001111', 0, 100);
-- user1 密码: pass123 (BCrypt 加密)
INSERT INTO sys_user (username, password, real_name, id_card, phone, role, credit_score) VALUES
('user1', '$2b$10$h3Bl5upPRhmWYSXcs/.i2OYBtHFg6wN4Yfz1ZVqLuUuEmfn2VceT6', '李四', '320102199203156789', '13700002222', 0, 95);
-- user2 密码: pass456 (BCrypt 加密)
INSERT INTO sys_user (username, password, real_name, id_card, phone, role, credit_score) VALUES
('user2', '$2b$10$QWZ1b7jc0KzpffMaO9JMLOov3BsoNvfY/Yckj2e3RBEmjlVPT1St6', '王五', '440103198807201234', '13600003333', 0, 88);
-- user3 密码: pass789 (BCrypt 加密)
INSERT INTO sys_user (username, password, real_name, id_card, phone, role, credit_score) VALUES
('user3', '$2b$10$Iziy321nV10U5xzYoJv52u6VKPwUDp5Ds5eJL3e8eg/6uTXpGYO6K', '赵六', '510104199612085678', '13500004444', 0, 72);
-- user4 密码: pass012 (BCrypt 加密)
INSERT INTO sys_user (username, password, real_name, id_card, phone, role, credit_score) VALUES
('user4', '$2b$10$wvmSWbIhCOKVUMG8J5l4SO0OtSNPl3yA8FMApQNnUhBBr7KhB3OX6', '孙七', '330105200001149012', '13400005555', 0, 60);
-- user5 密码: pass345 (BCrypt 加密)
INSERT INTO sys_user (username, password, real_name, id_card, phone, role, credit_score) VALUES
('user5', '$2b$10$mMY1gJ7oWTatjeRUz80mp.bOypeucw.IsD4sWFHc5Ceovgtik8L1y', '周八', '210106199709223456', '13300006666', 0, 45);

-- 充电宝数据 (10个)
INSERT INTO power_bank (code, status, hourly_rate) VALUES
('PB-001', 0, 2.00),
('PB-002', 1, 2.00),
('PB-003', 0, 3.00),
('PB-004', 0, 2.50),
('PB-005', 2, 2.00),
('PB-006', 0, 1.50),
('PB-007', 0, 2.00),
('PB-008', 0, 3.50),
('PB-009', 0, 2.00),
('PB-010', 2, 1.50);

-- 租赁订单数据 (testuser的订单 - 含各种状态)
-- 订单1: testuser 已归还已支付
INSERT INTO rental_order (user_id, power_bank_id, rental_time, return_time, deposit, deposit_status, total_fee, payment_status, status, overtime, created_at) VALUES
(2, 1, '2026-02-10 09:00:00', '2026-02-10 11:30:00', 99.00, 2, 6.00, 1, 1, 0, '2026-02-10 09:00:00');
-- 订单2: testuser 已归还未支付
INSERT INTO rental_order (user_id, power_bank_id, rental_time, return_time, deposit, deposit_status, total_fee, payment_status, status, overtime, created_at) VALUES
(2, 3, '2026-02-12 14:00:00', '2026-02-12 17:20:00', 99.00, 1, 12.00, 0, 1, 0, '2026-02-12 14:00:00');
-- 订单3: testuser 已归还已支付
INSERT INTO rental_order (user_id, power_bank_id, rental_time, return_time, deposit, deposit_status, total_fee, payment_status, status, overtime, created_at) VALUES
(2, 6, '2026-02-14 08:00:00', '2026-02-14 09:15:00', 99.00, 2, 3.00, 1, 1, 0, '2026-02-14 08:00:00');
-- 订单4: testuser 超时已归还未支付
INSERT INTO rental_order (user_id, power_bank_id, rental_time, return_time, deposit, deposit_status, total_fee, payment_status, status, overtime, created_at) VALUES
(2, 7, '2026-02-15 10:00:00', '2026-02-16 14:00:00', 99.00, 1, 56.00, 0, 2, 1, '2026-02-15 10:00:00');
-- 订单5: testuser 进行中 (PB-002 被占用)
INSERT INTO rental_order (user_id, power_bank_id, rental_time, return_time, deposit, deposit_status, total_fee, payment_status, status, overtime, created_at) VALUES
(2, 2, '2026-02-17 08:30:00', NULL, 99.00, 1, NULL, 0, 0, 0, '2026-02-17 08:30:00');

-- 其他用户的订单
-- user1 的订单
INSERT INTO rental_order (user_id, power_bank_id, rental_time, return_time, deposit, deposit_status, total_fee, payment_status, status, overtime, created_at) VALUES
(3, 4, '2026-02-11 10:00:00', '2026-02-11 12:00:00', 99.00, 2, 5.00, 1, 1, 0, '2026-02-11 10:00:00');
INSERT INTO rental_order (user_id, power_bank_id, rental_time, return_time, deposit, deposit_status, total_fee, payment_status, status, overtime, created_at) VALUES
(3, 8, '2026-02-13 15:00:00', '2026-02-13 16:30:00', 99.00, 2, 7.00, 1, 1, 0, '2026-02-13 15:00:00');

-- user2 的订单
INSERT INTO rental_order (user_id, power_bank_id, rental_time, return_time, deposit, deposit_status, total_fee, payment_status, status, overtime, created_at) VALUES
(4, 1, '2026-02-09 11:00:00', '2026-02-09 13:45:00', 99.00, 2, 6.00, 1, 1, 0, '2026-02-09 11:00:00');
INSERT INTO rental_order (user_id, power_bank_id, rental_time, return_time, deposit, deposit_status, total_fee, payment_status, status, overtime, created_at) VALUES
(4, 9, '2026-02-16 09:00:00', '2026-02-16 10:20:00', 99.00, 1, 4.00, 0, 1, 0, '2026-02-16 09:00:00');

-- user3 的订单
INSERT INTO rental_order (user_id, power_bank_id, rental_time, return_time, deposit, deposit_status, total_fee, payment_status, status, overtime, created_at) VALUES
(5, 3, '2026-02-08 16:00:00', '2026-02-09 20:00:00', 99.00, 1, 84.00, 0, 2, 1, '2026-02-08 16:00:00');

-- user4 的订单
INSERT INTO rental_order (user_id, power_bank_id, rental_time, return_time, deposit, deposit_status, total_fee, payment_status, status, overtime, created_at) VALUES
(6, 6, '2026-02-10 13:00:00', '2026-02-10 14:30:00', 99.00, 2, 3.00, 1, 1, 0, '2026-02-10 13:00:00');

-- user5 的订单
INSERT INTO rental_order (user_id, power_bank_id, rental_time, return_time, deposit, deposit_status, total_fee, payment_status, status, overtime, created_at) VALUES
(7, 7, '2026-02-11 08:00:00', '2026-02-11 09:00:00', 99.00, 2, 2.00, 1, 1, 0, '2026-02-11 08:00:00');
