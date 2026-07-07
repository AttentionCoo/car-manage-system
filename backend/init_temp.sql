-- 汽车美容店管理系统数据库初始化脚本
CREATE DATABASE IF NOT EXISTS car_beauty DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE car_beauty;

-- 客户表
CREATE TABLE customers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '客户ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender VARCHAR(10) NOT NULL COMMENT '性别',
    phone VARCHAR(11) NOT NULL UNIQUE COMMENT '手机号',
    id_card VARCHAR(18) COMMENT '身份证号',
    address VARCHAR(200) COMMENT '地址',
    email VARCHAR(100) COMMENT '邮箱',
    remark VARCHAR(500) COMMENT '备注',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0-正常 1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB COMMENT='客户信息表';

-- 性别约束（需求7）
ALTER TABLE customers ADD CONSTRAINT chk_gender CHECK (gender IN ('男', '女'));

CREATE INDEX idx_customers_phone ON customers(phone);

-- 车辆表
CREATE TABLE vehicles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '车辆ID',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    plate_number VARCHAR(20) NOT NULL UNIQUE COMMENT '车牌号',
    vin VARCHAR(17) COMMENT '车架号',
    brand VARCHAR(50) NOT NULL COMMENT '品牌',
    model VARCHAR(50) COMMENT '型号',
    color VARCHAR(20) COMMENT '颜色',
    year INT COMMENT '年份',
    engine_number VARCHAR(30) COMMENT '发动机号',
    register_date DATE COMMENT '注册日期',
    is_deleted TINYINT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='车辆信息表';

CREATE INDEX idx_vehicles_customer_id ON vehicles(customer_id);
CREATE INDEX idx_vehicles_plate_number ON vehicles(plate_number);

-- 美容项目表
CREATE TABLE beauty_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '项目ID',
    item_name VARCHAR(50) NOT NULL COMMENT '项目名称',
    item_code VARCHAR(20) NOT NULL UNIQUE COMMENT '项目编码',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    duration INT COMMENT '时长(分钟)',
    description VARCHAR(200) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态 0-停用 1-启用',
    is_deleted TINYINT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='美容项目表';

CREATE INDEX idx_beauty_items_code ON beauty_items(item_code);

-- 美容订单表
CREATE TABLE beauty_orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '订单号',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    vehicle_id BIGINT NOT NULL COMMENT '车辆ID',
    total_amount DECIMAL(10,2) DEFAULT 0 COMMENT '总金额',
    discount_amount DECIMAL(10,2) DEFAULT 0 COMMENT '折扣金额',
    payable_amount DECIMAL(10,2) DEFAULT 0 COMMENT '应付金额',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态 PENDING/IN_PROGRESS/COMPLETED/CANCELLED/PAID',
    appointment_time DATETIME COMMENT '预约时间',
    order_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    complete_time DATETIME COMMENT '完成时间',
    remark VARCHAR(500) COMMENT '备注',
    is_deleted TINYINT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='美容订单表';

CREATE INDEX idx_orders_customer ON beauty_orders(customer_id, order_time);
CREATE INDEX idx_orders_order_no ON beauty_orders(order_no);
CREATE INDEX idx_orders_status ON beauty_orders(status);

-- 订单明细表
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    item_id BIGINT NOT NULL COMMENT '项目ID',
    quantity INT DEFAULT 1 COMMENT '数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    subtotal DECIMAL(10,2) NOT NULL COMMENT '小计',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='订单明细表';

CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_item ON order_items(item_id);

-- 支付记录表
CREATE TABLE payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    payment_no VARCHAR(32) NOT NULL UNIQUE COMMENT '支付流水号',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    discount_amount DECIMAL(10,2) DEFAULT 0 COMMENT '折扣金额',
    paid_amount DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    change_amount DECIMAL(10,2) DEFAULT 0 COMMENT '找零',
    payment_method VARCHAR(20) NOT NULL COMMENT '支付方式 CASH/WECHAT/ALIPAY/CARD/OTHER',
    pay_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '支付时间',
    remark VARCHAR(200) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='支付记录表';

CREATE INDEX idx_payments_order ON payments(order_id);

-- 备份记录表
CREATE TABLE backup_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    backup_id VARCHAR(32) NOT NULL UNIQUE COMMENT '备份ID',
    backup_type VARCHAR(20) NOT NULL COMMENT '备份类型 FULL/INCREMENTAL/PARTIAL',
    file_name VARCHAR(100) NOT NULL COMMENT '文件名',
    file_path VARCHAR(255) NOT NULL COMMENT '文件路径',
    file_size BIGINT COMMENT '文件大小',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态 PENDING/IN_PROGRESS/COMPLETED/FAILED/DELETED',
    tables TEXT COMMENT '备份的表列表(JSON)',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    creator VARCHAR(50) COMMENT '操作人',
    description VARCHAR(200) COMMENT '描述',
    md5_checksum VARCHAR(64) COMMENT 'MD5校验值',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='备份记录表';

-- 插入测试数据
INSERT INTO beauty_items (item_name, item_code, price, duration, description) VALUES
('全车打蜡', 'DM001', 298.00, 60, '使用进口车蜡，全车身抛光打蜡'),
('内饰清洗', 'QN001', 388.00, 90, '深度清洗车内座椅、仪表盘等'),
('玻璃镀膜', 'BL001', 588.00, 120, '前挡风玻璃及侧窗镀膜'),
('发动机舱清洗', 'FD001', 268.00, 45, '发动机舱深度清洁除油'),
('轮毂翻新', 'LG001', 358.00, 90, '轮毂清洁抛光处理');

INSERT INTO customers (name, gender, phone, id_card, address, email) VALUES
('张三', '男', '13800138001', '110101199001011234', '北京市朝阳区xxx路1号', 'zhangsan@test.com'),
('李四', '女', '13900139002', '110101199205055678', '上海市浦东新区xxx路2号', 'lisi@test.com'),
('王五', '男', '13700137003', '110101198803033456', '广州市天河区xxx路3号', 'wangwu@test.com');
