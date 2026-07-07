# 汽车美容店管理系统 - 前后端接口文档

## 目录
1. [系统概述](#1-系统概述)
2. [通用规范](#2-通用规范)
3. [美容项目管理模块](#3-美容项目管理模块)
4. [客户信息管理模块](#4-客户信息管理模块)
5. [客户车辆管理模块](#5-客户车辆管理模块)
6. [美容登记与收费管理模块](#6-美容登记与收费管理模块)
7. [统计报表模块（存储过程）](#7-统计报表模块存储过程)
8. [数据备份与恢复模块](#8-数据备份与恢复模块)

---

## 1. 系统概述

### 1.1 系统功能
- 美容项目及价格信息管理
- 客户信息、客户车辆信息管理
- 美容登记和收费管理
- 统计分析功能（月度/年度）
- 数据备份和恢复功能

### 1.2 技术栈建议
- **前端**: Vue.js / React + Element UI / Ant Design
- **后端**: Spring Boot / Node.js + Express
- **数据库**: MySQL / PostgreSQL
- **认证方式**: JWT Token

---

## 2. 通用规范

### 2.1 基础URL
```
开发环境: http://localhost:8080/api/v1
生产环境: https://your-domain.com/api/v1
```

### 2.2 请求头
```
Content-Type: application/json
Authorization: Bearer {token}
```

### 2.3 通用响应格式
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### 2.4 错误码说明
| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 2.5 分页参数
```json
{
  "page": 1,
  "pageSize": 10,
  "total": 100,
  "pages": 10
}
```

---

## 3. 美容项目管理模块

### 3.1 获取美容项目列表

**请求**
```
GET /beauty-items
```

**查询参数**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| keyword | String | 否 | 搜索关键词（项目名称） |
| status | Integer | 否 | 状态：0-停用，1-启用 |

**响应示例**
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "records": [
      {
        "id": 1,
        "itemName": "全车打蜡",
        "itemCode": "DM001",
        "price": 298.00,
        "duration": 60,
        "description": "使用进口车蜡，全车身抛光打蜡",
        "status": 1,
        "createTime": "2024-01-01T08:00:00Z",
        "updateTime": "2024-01-01T08:00:00Z"
      }
    ],
    "total": 20,
    "page": 1,
    "pageSize": 10
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### 3.2 获取美容项目详情

**请求**
```
GET /beauty-items/{id}
```

**路径参数**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 项目ID |

### 3.3 新增美容项目

**请求**
```
POST /beauty-items
```

**请求体**
```json
{
  "itemName": "内饰清洗",
  "itemCode": "QN001",
  "price": 388.00,
  "duration": 90,
  "description": "深度清洗车内座椅、仪表盘等",
  "status": 1
}
```

**字段验证规则**
| 字段 | 规则 |
|------|------|
| itemName | 非空，长度2-50字符 |
| itemCode | 非空，唯一，长度2-20字符 |
| price | 必须>0，最多2位小数 |
| duration | 必须>0，单位：分钟 |
| description | 可选，最大200字符 |
| status | 必填，0或1 |

### 3.4 修改美容项目

**请求**
```
PUT /beauty-items/{id}
```

**路径参数**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 项目ID |

**请求体**
```json
{
  "itemName": "内饰清洗（升级版）",
  "price": 458.00,
  "duration": 120,
  "status": 1
}
```

### 3.5 删除美容项目

**请求**
```
DELETE /beauty-items/{id}
```

**路径参数**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 项目ID |

**业务规则**: 如果该项目已有美容记录关联，禁止删除，返回错误提示。

### 3.6 批量修改状态

**请求**
```
PUT /beauty-items/status/batch
```

**请求体**
```json
{
  "ids": [1, 2, 3],
  "status": 0
}
```

---

## 4. 客户信息管理模块

### 4.1 获取客户列表

**请求**
```
GET /customers
```

**查询参数**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | Integer | 否 | 页码 |
| pageSize | Integer | 否 | 每页数量 |
| name | String | 否 | 客户姓名（模糊搜索） |
| phone | String | 否 | 手机号（精确匹配） |
| gender | String | 否 | 性别：男/女 |
| startDate | String | 否 | 注册开始日期 |
| endDate | String | 否 | 注册结束日期 |

**响应示例**
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "张三",
        "gender": "男",
        "phone": "13800138000",
        "idCard": "110101199001011234",
        "address": "北京市朝阳区xxx路xxx号",
        "email": "zhangsan@example.com",
        "remark": "VIP客户",
        "createTime": "2024-01-15T10:30:00Z"
      }
    ],
    "total": 150,
    "page": 1,
    "pageSize": 10
  }
}
```

### 4.2 获取客户详情

**请求**
```
GET /customers/{id}
```

**路径参数**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 客户ID |

**响应示例**
```json
{
  "code": 200,
  "data": {
    "customer": {
      "id": 1,
      "name": "张三",
      "gender": "男",
      "phone": "13800138000",
      "idCard": "110101199001011234",
      "address": "北京市朝阳区xxx路xxx号",
      "email": "zhangsan@example.com",
      "remark": "VIP客户",
      "createTime": "2024-01-15T10:30:00Z"
    },
    "vehicles": [
      {
        "id": 101,
        "plateNumber": "京A12345",
        "brand": "宝马",
        "model": "5系",
        "color": "黑色",
        "year": 2022
      }
    ]
  }
}
```

### 4.3 新增客户

**请求**
```
POST /customers
```

**请求体**
```json
{
  "name": "李四",
  "gender": "女",
  "phone": "13900139000",
  "idCard": "110101199205052345",
  "address": "上海市浦东新区xxx路xxx号",
  "email": "lisi@example.com",
  "remark": ""
}
```

**字段验证规则**
| 字段 | 规则 | 说明 |
|------|------|------|
| name | 非空，2-50字符 | 姓名 |
| **gender** | **必须为"男"或"女"** | **性别约束（规则7）** |
| phone | 非空，11位手机号 | 手机号唯一 |
| idCard | 可选，18位身份证号 | 身份证号 |
| address | 可选，最大200字符 | 地址 |
| email | 可选，邮箱格式 | 邮箱 |
| remark | 可选，最大500字符 | 备注 |

**特殊约束说明（需求7）**:
- 性别字段只能接受"男"或"女"两个值
- 数据库层面通过CHECK约束实现
- 接口层进行参数校验
- 输入其他值返回400错误：
```json
{
  "code": 400,
  "message": "性别必须为'男'或'女'"
}
```

### 4.4 修改客户信息

**请求**
```
PUT /customers/{id}
```

**路径参数**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 客户ID |

**请求体**
```json
{
  "name": "李四",
  "phone": "13900139001",
  "address": "上海市黄浦区xxx路xxx号"
}
```

### 4.5 删除客户

**请求**
```
DELETE /customers/{id}
```

**业务规则**: 
- 如果客户有关联车辆或美容记录，需先删除关联数据或做逻辑删除
- 建议采用逻辑删除（软删除），设置isDeleted=1

### 4.6 检查手机号是否已存在

**请求**
```
GET /customers/check-phone?phone={phone}&excludeId={excludeId}
```

**用途**: 新增/编辑时校验手机号唯一性

---

## 5. 客户车辆管理模块

### 5.1 获取车辆列表

**请求**
```
GET /vehicles
```

**查询参数**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| customerId | Long | 否 | 客户ID |
| plateNumber | String | 否 | 车牌号（模糊） |
| brand | String | 否 | 品牌 |
| page | Integer | 否 | 页码 |
| pageSize | Integer | 否 | 每页数量 |

**响应示例**
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 101,
        "customerId": 1,
        "customerName": "张三",
        "plateNumber": "京A12345",
        "vin": "LSVAU2A02JN000001",
        "brand": "宝马",
        "model": "5系",
        "color": "黑色",
        "year": 2022,
        "engineNumber": "B48B20A12345",
        "registerDate": "2022-03-15",
        "createTime": "2024-01-15T10:35:00Z"
      }
    ],
    "total": 80
  }
}
```

### 5.2 获取车辆详情

**请求**
```
GET /vehicles/{id}
```

### 5.3 新增车辆

**请求**
```
POST /vehicles
```

**请求体**
```json
{
  "customerId": 1,
  "plateNumber": "沪B88888",
  "vin": "LSVAA1234567890123",
  "brand": "奔驰",
  "model": "E300L",
  "color": "白色",
  "year": 2023,
  "engineNumber": "27492012345",
  "registerDate": "2023-06-20"
}
```

**字段验证规则**
| 字段 | 规则 | 说明 |
|------|------|------|
| customerId | 非空，客户必须存在 | 关联客户ID |
| plateNumber | 非空，唯一 | 车牌号 |
| vin | 可选，17位 | 车架号 |
| brand | 非空 | 品牌 |
| model | 可选 | 型号 |
| color | 可选 | 颜色 |
| year | 可选 | 年份 |
| engineNumber | 可选 | 发动机号 |
| registerDate | 可选 | 注册日期 |

### 5.4 修改车辆信息

**请求**
```
PUT /vehicles/{id}
```

**请求体**
```json
{
  "plateNumber": "沪B88888",
  "color": "银色",
  "year": 2024
}
```

### 5.5 删除车辆

**请求**
```
DELETE /vehicles/{id}
```

**业务规则**: 如果车辆有未完成的美容订单，禁止删除

### 5.6 根据客户ID获取车辆列表

**请求**
```
GET /customers/{customerId}/vehicles
```

**用途**: 在客户详情页展示该客户的所有车辆

---

## 6. 美容登记与收费管理模块

### 6.1 创建美容订单

**请求**
```
POST /beauty-orders
```

**请求体**
```json
{
  "customerId": 1,
  "vehicleId": 101,
  "items": [
    {
      "itemId": 1,
      "quantity": 1
    },
    {
      "itemId": 3,
      "quantity": 1
    }
  ],
  "appointmentTime": "2024-01-20T14:00:00Z",
  "remark": "希望下午完成"
}
```

**字段说明**
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| customerId | Long | 是 | 客户ID |
| vehicleId | Long | 是 | 车辆ID（必须是该客户的车辆） |
| items | Array | 是 | 美容项目列表 |
| items[].itemId | Long | 是 | 项目ID |
| items[].quantity | Integer | 是 | 数量，默认1 |
| appointmentTime | DateTime | 否 | 预约时间 |
| remark | String | 否 | 备注 |

**响应示例**
```json
{
  "code": 200,
  "message": "订单创建成功",
  "data": {
    "orderId": "ORD202401150001",
    "customerId": 1,
    "vehicleId": 101,
    "items": [
      {
        "itemId": 1,
        "itemName": "全车打蜡",
        "price": 298.00,
        "quantity": 1,
        "subtotal": 298.00
      },
      {
        "itemId": 3,
        "itemName": "内饰清洗",
        "price": 388.00,
        "quantity": 1,
        "subtotal": 388.00
      }
    ],
    "totalAmount": 686.00,
    "discountAmount": 0.00,
    "payableAmount": 686.00,
    "status": "PENDING",
    "orderTime": "2024-01-15T09:30:00Z",
    "appointmentTime": "2024-01-20T14:00:00Z"
  }
}
```

**业务规则**:
1. 自动计算总金额 = Σ(单价 × 数量)
2. 订单初始状态为 PENDING（待处理）
3. 校验车辆是否属于该客户
4. 校验项目是否有效且启用

### 6.2 获取订单列表

**请求**
```
GET /beauty-orders
```

**查询参数**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | Integer | 否 | 页码 |
| pageSize | Integer | 否 | 每页数量 |
| orderNo | String | 否 | 订单号 |
| customerId | Long | 否 | 客户ID |
| status | String | 否 | 订单状态 |
| startDate | String | 否 | 开始日期 |
| endDate | String | 否 | 结束日期 |

**订单状态枚举**
| 状态值 | 说明 |
|--------|------|
| PENDING | 待处理 |
| IN_PROGRESS | 进行中 |
| COMPLETED | 已完成 |
| CANCELLED | 已取消 |

### 6.3 获取订单详情

**请求**
```
GET /beauty-orders/{orderId}
```

**响应包含**:
- 订单基本信息
- 客户信息
- 车辆信息
- 项目明细列表
- 支付记录
- 操作日志

### 6.4 更新订单状态

**请求**
```
PUT /beauty-orders/{orderId}/status
```

**请求体**
```json
{
  "status": "IN_PROGRESS",
  "operator": "管理员",
  "remark": "开始施工"
}
```

**状态流转规则**:
```
PENDING → IN_PROGRESS → COMPLETED
                    ↘ CANCELLED
```

### 6.5 订单结算/收费

**请求**
```
POST /beauty-orders/{orderId}/payment
```

**请求体**
```json
{
  "paymentMethod": "CASH",
  "paidAmount": 686.00,
  "discountAmount": 50.00,
  "remark": "会员折扣"
}
```

**支付方式枚举**
| 方式 | 说明 |
|------|------|
| CASH | 现金 |
| WECHAT | 微信支付 |
| ALIPAY | 支付宝 |
| CARD | 刷卡 |
| OTHER | 其他 |

**响应示例**
```json
{
  "code": 200,
  "message": "支付成功",
  "data": {
    "orderId": "ORD202401150001",
    "paymentId": "PAY202401150001",
    "totalAmount": 686.00,
    "discountAmount": 50.00,
    "payableAmount": 636.00,
    "paidAmount": 636.00,
    "changeAmount": 0.00,
    "paymentMethod": "CASH",
    "paymentTime": "2024-01-20T16:30:00Z",
    "status": "PAID"
  }
}
```

**业务规则**:
1. 只有COMPLETED状态的订单才能进行支付
2. 支付金额不能小于应付金额（除非有折扣）
3. 支付成功后自动更新订单状态为PAID
4. 生成支付记录

### 6.6 取消订单

**请求**
```
PUT /beauty-orders/{orderId}/cancel
```

**请求体**
```json
{
  "reason": "客户临时取消"
}
```

**业务规则**:
- 只有PENDING和IN_PROGRESS状态的订单可以取消
- 已支付的订单取消需要走退款流程

### 6.7 获取今日待处理订单

**请求**
```
GET /beauty-orders/today/pending
```

**用途**: 工作台展示当日待办事项

### 6.8 获取订单统计概览

**请求**
```
GET /beauty-orders/statistics/overview
```

**查询参数**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| date | String | 否 | 日期，默认今天 |

**响应示例**
```json
{
  "code": 200,
  "data": {
    "todayOrders": 25,
    "pendingOrders": 8,
    "inProgressOrders": 12,
    "completedOrders": 5,
    "todayRevenue": 15280.00,
    "monthRevenue": 286450.00,
    "averageOrderAmount": 611.20
  }
}
```

---

## 7. 统计报表模块（存储过程）

### 7.1 月度美容项目次数统计（需求4）

**接口**
```
GET /statistics/monthly-item-count
```

**查询参数**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| year | Integer | 是 | 年份，如2024 |
| month | Integer | 是 | 月份，1-12 |

**对应存储过程**
```sql
-- 存储过程名称: sp_statistics_monthly_item_count
CREATE PROCEDURE sp_statistics_monthly_item_count(
    IN p_year INT,
    IN p_month INT
)
BEGIN
    SELECT 
        bi.id AS itemId,
        bi.item_name AS projectName,
        bi.price AS unitPrice,
        COUNT(oi.item_id) AS serviceCount,
        SUM(oi.quantity) AS totalQuantity,
        SUM(oi.subtotal) AS totalRevenue,
        ROUND(AVG(oi.subtotal), 2) AS averageRevenue
    FROM beauty_items bi
    LEFT JOIN order_items oi ON bi.id = oi.item_id
    LEFT JOIN beauty_orders bo ON oi.order_id = bo.id
    WHERE YEAR(bo.order_time) = p_year 
      AND MONTH(bo.order_time) = p_month
      AND bo.status IN ('COMPLETED', 'PAID')
    GROUP BY bi.id, bi.item_name, bi.price
    ORDER BY serviceCount DESC;
END;
```

**响应示例**
```json
{
  "code": 200,
  "data": {
    "statistics": [
      {
        "itemId": 1,
        "projectName": "全车打蜡",
        "unitPrice": 298.00,
        "serviceCount": 156,
        "totalQuantity": 156,
        "totalRevenue": 46488.00,
        "averageRevenue": 298.00
      },
      {
        "itemId": 3,
        "projectName": "内饰清洗",
        "unitPrice": 388.00,
        "serviceCount": 98,
        "totalQuantity": 98,
        "totalRevenue": 38024.00,
        "averageRevenue": 388.00
      }
    ],
    "summary": {
      "totalServices": 320,
      "totalRevenue": 125680.00,
      "period": "2024年1月"
    }
  }
}
```

### 7.2 年度客户美容次数统计（需求5）

**接口**
```
GET /statistics/yearly-customer-count
```

**查询参数**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| year | Integer | 是 | 年份 |

**对应存储过程**
```sql
-- 存储过程名称: sp_statistics_yearly_customer_count
CREATE PROCEDURE sp_statistics_yearly_customer_count(
    IN p_year INT
)
BEGIN
    SELECT 
        c.id AS customerId,
        c.name AS customerName,
        c.phone AS customerPhone,
        c.gender,
        COUNT(DISTINCT bo.id) AS totalOrders,
        SUM(oi.quantity) AS totalServiceCount,
        SUM(bo.payable_amount) AS totalSpent,
        MAX(bo.order_time) AS lastVisitDate,
        CASE 
            WHEN SUM(bo.payable_amount) >= 10000 THEN 'VIP'
            WHEN SUM(bo.payable_amount) >= 5000 THEN '金卡'
            WHEN SUM(bo.payable_amount) >= 2000 THEN '银卡'
            ELSE '普通'
        END AS customerLevel
    FROM customers c
    LEFT JOIN beauty_orders bo ON c.id = bo.customer_id
    LEFT JOIN order_items oi ON bo.id = oi.order_id
    WHERE (bo.order_time IS NULL OR YEAR(bo.order_time) = p_year)
      AND (bo.status IS NULL OR bo.status IN ('COMPLETED', 'PAID'))
    GROUP BY c.id, c.name, c.phone, c.gender
    ORDER BY totalSpent DESC;
END;
```

**响应示例**
```json
{
  "code": 200,
  "data": {
    "statistics": [
      {
        "customerId": 1,
        "customerName": "张三",
        "customerPhone": "138****8000",
        "gender": "男",
        "totalOrders": 12,
        "totalServiceCount": 18,
        "totalSpent": 12860.00,
        "lastVisitDate": "2024-12-28",
        "customerLevel": "VIP"
      },
      {
        "customerId": 5,
        "customerName": "王五",
        "customerPhone": "137****9999",
        "gender": "男",
        "totalOrders": 8,
        "totalServiceCount": 10,
        "totalSpent": 6580.00,
        "lastVisitDate": "2024-12-25",
        "customerLevel": "金卡"
      }
    ],
    "summary": {
      "totalCustomers": 256,
      "activeCustomers": 189,
      "totalRevenue": 896520.00,
      "avgSpentPerCustomer": 4743.54,
      "year": 2024
    }
  }
}
```

### 7.3 月度收入统计（需求6）

**接口**
```
GET /statistics/monthly-revenue
```

**查询参数**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| year | Integer | 是 | 年份 |
| month | Integer | 是 | 月份 |

**对应存储过程**
```sql
-- 存储过程名称: sp_statistics_monthly_revenue
CREATE PROCEDURE sp_statistics_monthly_revenue(
    IN p_year INT,
    IN p_month INT
)
BEGIN
    -- 基础统计
    SELECT 
        COUNT(*) AS totalOrders,
        SUM(CASE WHEN status IN ('COMPLETED', 'PAID') THEN 1 ELSE 0 END) AS completedOrders,
        SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelledOrders,
        SUM(payable_amount) AS grossRevenue,
        SUM(discount_amount) AS totalDiscount,
        SUM(CASE WHEN payment_method = 'CASH' THEN paid_amount ELSE 0 END) AS cashRevenue,
        SUM(CASE WHEN payment_method = 'WECHAT' THEN paid_amount ELSE 0 END) AS wechatRevenue,
        SUM(CASE WHEN payment_method = 'ALIPAY' THEN paid_amount ELSE 0 END) AS alipayRevenue,
        SUM(CASE WHEN payment_method = 'CARD' THEN paid_amount ELSE 0 END) AS cardRevenue,
        AVG(payable_amount) AS avgOrderValue,
        MAX(payable_amount) AS maxOrderValue,
        MIN(payable_amount) AS minOrderValue
    FROM beauty_orders
    WHERE YEAR(order_time) = p_year 
      AND MONTH(order_time) = p_month;
    
    -- 日收入趋势
    SELECT 
        DAY(order_time) AS day,
        COUNT(*) AS dailyOrders,
        SUM(payable_amount) AS dailyRevenue
    FROM beauty_orders
    WHERE YEAR(order_time) = p_year 
      AND MONTH(order_time) = p_month
      AND status IN ('COMPLETED', 'PAID')
    GROUP BY DAY(order_time)
    ORDER BY day;
    
    -- 项目收入占比
    SELECT 
        bi.item_name,
        COUNT(oi.id) AS orderCount,
        SUM(oi.subtotal) AS itemRevenue,
        ROUND(SUM(oi.subtotal) * 100.0 / (SELECT SUM(payable_amount) 
            FROM beauty_orders 
            WHERE YEAR(order_time) = p_year 
              AND MONTH(order_time) = p_month
              AND status IN ('COMPLETED', 'PAID')), 2) AS percentage
    FROM beauty_items bi
    JOIN order_items oi ON bi.id = oi.item_id
    JOIN beauty_orders bo ON oi.order_id = bo.id
    WHERE YEAR(bo.order_time) = p_year 
      AND MONTH(bo.order_time) = p_month
      AND bo.status IN ('COMPLETED', 'PAID')
    GROUP BY bi.item_name
    ORDER BY itemRevenue DESC;
END;
```

**响应示例**
```json
{
  "code": 200,
  "data": {
    "overview": {
      "period": "2024年1月",
      "totalOrders": 320,
      "completedOrders": 298,
      "cancelledOrders": 22,
      "grossRevenue": 198560.00,
      "totalDiscount": 8520.00,
      "netRevenue": 190040.00,
      "cashRevenue": 85000.00,
      "wechatRevenue": 65000.00,
      "alipayRevenue": 32040.00,
      "cardRevenue": 8000.00,
      "avgOrderValue": 637.84,
      "maxOrderValue": 2680.00,
      "minOrderValue": 168.00
    },
    "dailyTrend": [
      {"day": 1, "dailyOrders": 12, "dailyRevenue": 7890.00},
      {"day": 2, "dailyOrders": 15, "dailyRevenue": 9560.00},
      {"day": 3, "dailyOrders": 8, "dailyRevenue": 5120.00}
    ],
    "itemRevenueDistribution": [
      {
        "itemName": "全车打蜡",
        "orderCount": 156,
        "itemRevenue": 46488.00,
        "percentage": 24.46
      },
      {
        "itemName": "内饰清洗",
        "orderCount": 98,
        "itemRevenue": 38024.00,
        "percentage": 20.01
      }
    ]
  }
}
```

### 7.4 自定义时间范围统计

**接口**
```
GET /statistics/custom-range
```

**查询参数**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| startDate | String | 是 | 开始日期 YYYY-MM-DD |
| endDate | String | 是 | 结束日期 YYYY-MM-DD |
| type | String | 否 | 统计类型：revenue/customer/item |

---

## 8. 数据备份与恢复模块（需求8）

### 8.1 创建数据备份

**请求**
```
POST /backup/create
```

**请求体**
```json
{
  "backupType": "FULL",
  "description": "月末完整备份",
  "tables": ["customers", "vehicles", "beauty_items", "beauty_orders", "order_items", "payments"]
}
```

**备份类型**
| 类型 | 说明 |
|------|------|
| FULL | 全量备份 |
| INCREMENTAL | 增量备份 |
| PARTIAL | 部分表备份 |

**响应示例**
```json
{
  "code": 200,
  "message": "备份任务已创建",
  "data": {
    "backupId": "BK202401310001",
    "backupType": "FULL",
    "fileName": "backup_20240131_143022.sql",
    "fileSize": 15728640,
    "status": "IN_PROGRESS",
    "startTime": "2024-01-31T14:30:22Z",
    "estimatedTime": 120,
    "tables": ["customers", "vehicles", "beauty_items", "beauty_orders", "order_items", "payments"]
  }
}
```

### 8.2 查询备份列表

**请求**
```
GET /backup/list
```

**查询参数**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | Integer | 否 | 页码 |
| pageSize | Integer | 否 | 每页数量 |
| status | String | 否 | 备份状态 |

**响应示例**
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "backupId": "BK202401310001",
        "backupType": "FULL",
        "fileName": "backup_20240131_143022.sql",
        "fileSize": 15728640,
        "fileSizeDisplay": "15.00 MB",
        "status": "COMPLETED",
        "startTime": "2024-01-31T14:30:22Z",
        "endTime": "2024-01-31T14:32:15Z",
        "duration": 113,
        "creator": "admin",
        "description": "月末完整备份"
      }
    ],
    "total": 25
  }
}
```

**备份状态枚举**
| 状态 | 说明 |
|------|------|
| PENDING | 等待执行 |
| IN_PROGRESS | 进行中 |
| COMPLETED | 完成 |
| FAILED | 失败 |
| DELETED | 已删除 |

### 8.3 查询备份详情

**请求**
```
GET /backup/{backupId}
```

**响应包含**:
- 备份基本信息
- 包含的表及记录数
- 备份文件MD5校验值
- 操作日志

### 8.4 下载备份文件

**请求**
```
GET /backup/{backupId}/download
```

**响应**: 文件流（application/octet-stream）

**权限要求**: 仅管理员可下载

### 8.5 数据恢复

**请求**
```
POST /backup/{backupId}/restore
```

**请求体**
```json
{
  "confirmPassword": "admin123",
  "restoreType": "OVERWRITE",
  "selectedTables": ["customers", "vehicles"]
}
```

**恢复类型**
| 类型 | 说明 |
|------|------|
| OVERWRITE | 覆盖恢复（危险操作） |
| MERGE | 合并恢复（推荐） |
| PARTIAL | 部分表恢复 |

**安全措施**:
1. 需要输入管理员密码确认
2. 恢复前自动创建当前数据的快照备份
3. 发送恢复通知给所有管理员
4. 记录详细操作日志

**响应示例**
```json
{
  "code": 200,
  "message": "恢复任务已创建，请等待执行完成",
  "data": {
    "restoreId": "RS202401310001",
    "sourceBackupId": "BK202401280001",
    "restoreType": "OVERWRITE",
    "snapshotBackupId": "BK202401310002",
    "status": "PENDING",
    "estimatedTime": 180,
    "warning": "此操作将覆盖当前数据，请确认！"
  }
}
```

### 8.6 查询恢复进度

**请求**
```
GET /backup/restore/{restoreId}/status
```

**响应示例**
```json
{
  "code": 200,
  "data": {
    "restoreId": "RS202401310001",
    "status": "IN_PROGRESS",
    "progress": 65,
    "currentTable": "beauty_orders",
    "restoredTables": ["customers", "vehicles", "beauty_items"],
    "remainingTables": ["beauty_orders", "order_items", "payments"],
    "startTime": "2024-01-31T15:00:00Z",
    "elapsedTime": 98,
    "estimatedRemainingTime": 52,
    "logs": [
      {"time": "15:00:01", "message": "开始恢复..."},
      {"time": "15:00:15", "message": "正在恢复表 customers..."},
      {"time": "15:00:32", "message": "表 customers 恢复完成，影响 150 行"}
    ]
  }
}
```

### 8.7 删除备份

**请求**
```
DELETE /backup/{backupId}
```

**请求体**
```json
{
  "confirmPassword": "admin123"
}
```

**业务规则**:
- 只能删除状态为COMPLETED或FAILED的备份
- 删除前需要密码确认
- 同时删除物理文件

### 8.8 设置自动备份策略

**请求**
```
POST /backup/schedule
```

**请求体**
```json
{
  "scheduleName": "每日增量备份",
  "backupType": "INCREMENTAL",
  "cronExpression": "0 0 2 * * ?",
  "retentionDays": 30,
  "enabled": true,
  "notificationEmails": ["admin@example.com"]
}
```

**cron表达式说明**: 每天凌晨2点执行

### 8.9 获取备份策略列表

**请求**
```
GET /backup/schedule/list
```

---

## 附录

### A. 数据库ER关系图（简化版）

```
┌─────────────┐     ┌─────────────┐     ┌─────────────────┐
│  customers  │     │   vehicles  │     │  beauty_items   │
├─────────────┤     ├─────────────┤     ├─────────────────┤
│ id (PK)     │◄──┐ │ id (PK)     │     │ id (PK)         │
│ name        │   └─│ customer_id │     │ item_name       │
│ gender      │     │ plate_number│     │ price           │
│ phone       │     │ brand       │     │ duration        │
│ id_card     │     │ model       │     │ status          │
│ address     │     │ color       │     └────────┬────────┘
│ email       │     │ year        │              │
└─────────────┘     └──────┬──────┘              │
                           │                     │
                           ▼                     │
                   ┌──────────────┐              │
                   │ beauty_orders │              │
                   ├──────────────┤              │
                   │ id (PK)      │              │
                   │ order_no     │              │
                   │ customer_id  │              │
                   │ vehicle_id   │              │
                   │ status       │              │
                   │ total_amount │              │
                   │ payable_amt  │              │
                   │ discount_amt │              │
                   └──────┬───────┘              │
                          │                      │
                          ▼                      │
                   ┌─────────────┐               │
                   │ order_items │───────────────┘
                   ├─────────────┤
                   │ id (PK)     │
                   │ order_id    │
                   │ item_id     │
                   │ quantity    │
                   │ subtotal    │
                   └──────┬──────┘
                          │
                          ▼
                   ┌─────────────┐
                   │  payments   │
                   ├─────────────┤
                   │ id (PK)     │
                   │ order_id    │
                   │ amount      │
                   │ method      │
                   │ pay_time    │
                   └─────────────┘
```

### B. 性能优化建议

1. **索引设计**:
   - customers(phone) 唯一索引
   - vehicles(plate_number) 唯一索引
   - vehicles(customer_id) 普通索引
   - beauty_orders(customer_id, order_time) 复合索引
   - order_items(order_id) 普通索引
   - order_items(item_id) 普通索引

2. **缓存策略**:
   - 美容项目列表使用Redis缓存
   - 统计报表结果缓存1小时
   - 客户基础信息缓存30分钟

3. **分页优化**:
   - 大数据量查询使用游标分页
   - 避免深分页（LIMIT 1000000, 10）

### C. 安全性考虑

1. **API安全**:
   - 所有接口JWT认证
   - 敏感操作二次验证（密码确认）
   - 接口限流防止滥用

2. **数据安全**:
   - 手机号、身份证脱敏显示
   - 备份文件加密存储
   - 操作日志审计追踪

3. **SQL注入防护**:
   - 使用参数化查询
   - 存储过程输入参数校验
   - 最小权限原则

### D. 版本历史

| 版本 | 日期 | 作者 | 说明 |
|------|------|------|------|
| v1.0 | 2024-01-31 | 系统架构师 | 初始版本创建 |

---

**文档结束**

*本文档由汽车美容店管理系统开发团队维护*
*如有疑问请联系技术支持*
