# 🚗 汽车美容店管理系统

一个基于 Spring Boot + Vue 3 的汽车美容店管理系统，支持美容项目管理、客户车辆管理、美容登记收费、统计分析及数据备份恢复等功能。

## 📋 功能模块

| 模块 | 说明 |
|------|------|
| **美容项目管理** | 美容服务项目的增删改查及价格管理 |
| **客户信息管理** | 客户基本信息维护 |
| **车辆管理** | 客户车辆信息管理，关联客户 |
| **美容登记与收费** | 美容订单创建、服务项目关联、收费管理 |
| **统计分析** | 月度/年度经营数据统计报表（基于存储过程） |
| **数据备份恢复** | 数据库备份与恢复，保障数据安全 |

## 🛠 技术栈

### 后端

- **Java 1.8** + **Spring Boot 2.7.18**
- **MyBatis-Plus 3.5.5** — ORM 框架
- **MySQL 8.0** — 数据库
- **Druid 1.2.20** — 数据库连接池
- **Fastjson 2.0.43** — JSON 处理
- **Lombok** — 代码简化

### 前端

- **Vue 3.4** + **Vite 5**
- **Element Plus 2.5** — UI 组件库
- **Vue Router 4** — 路由管理
- **Axios 1.6** — HTTP 请求

## 📁 项目结构

```
car-manage-system/
├── backend/                          # 后端项目 (Spring Boot)
│   ├── pom.xml                       # Maven 配置
│   ├── init_temp.sql                 # 建表脚本
│   └── src/main/
│       ├── java/com/carmanage/
│       │   ├── CarManageApplication.java    # 启动类
│       │   ├── common/                      # 通用类（全局异常、分页、响应）
│       │   ├── config/                      # 配置类（CORS、MyBatis-Plus）
│       │   ├── controller/                  # 控制器层
│       │   ├── entity/                      # 实体类
│       │   ├── mapper/                      # Mapper 接口
│       │   └── service/                     # 服务层接口与实现
│       └── resources/
│           ├── application.yml              # 应用配置
│           ├── db/                          # 数据库初始化脚本
│           └── mapper/                      # MyBatis XML 映射
├── frontend/                         # 前端项目 (Vue 3)
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── main.js                   # 入口文件
│       ├── App.vue                   # 根组件
│       ├── api/                      # API 请求封装
│       ├── router/                   # 路由配置
│       ├── components/               # 公共组件
│       └── views/                    # 页面视图
│           ├── Dashboard.vue         # 首页看板
│           ├── BeautyItemManage.vue  # 美容项目管理
│           ├── CustomerManage.vue    # 客户管理
│           ├── VehicleManage.vue     # 车辆管理
│           ├── OrderManage.vue       # 订单管理
│           ├── Statistics.vue        # 统计分析
│           └── BackupManage.vue      # 数据备份
├── API接口文档.md                    # 接口文档
└── README.md
```

## 🚀 运行指南

### 环境要求

- **JDK** 1.8+
- **Maven** 3.6+
- **Node.js** 16+
- **MySQL** 8.0+

### 1. 数据库初始化

1. 创建数据库：
```sql
CREATE DATABASE car_beauty DEFAULT CHARACTER SET utf8mb4;
```

2. 执行建表脚本 `backend/init_temp.sql`，创建表结构和存储过程。

### 2. 启动后端

```bash
cd backend

# 修改 application.yml 中的数据库连接信息
# spring.datasource.url=jdbc:mysql://localhost:3306/car_beauty
# spring.datasource.username=root
# spring.datasource.password=your_password

# 启动
mvn spring-boot:run
```

后端启动后运行在 `http://localhost:8080`。

### 3. 启动前端

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端启动后运行在 `http://localhost:5173`。

### 4. 访问系统

打开浏览器访问 `http://localhost:5173` 即可使用系统。

## 📡 API 接口

接口基础路径：`http://localhost:8080/api/v1`

详细接口文档请查看 [API接口文档.md](./API接口文档.md)，包含以下模块：

- 美容项目管理 — `/api/v1/beauty-items`
- 客户信息管理 — `/api/v1/customers`
- 车辆管理 — `/api/v1/vehicles`
- 美容订单管理 — `/api/v1/beauty-orders`
- 统计报表 — `/api/v1/statistics`
- 数据备份恢复 — `/api/v1/backups`

通用响应格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

## 📝 License

MIT
