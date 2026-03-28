# 充电宝租赁管理系统

## 快速启动

### Docker Compose（推荐）

```bash
docker-compose up --build -d
```

等待所有服务启动完成后即可访问。

### 本地开发

1. 启动 MySQL 8.0，执行 `backend/src/main/resources/schema.sql` 建表
2. 启动后端：
```bash
cd backend
mvn spring-boot:run
```
3. 启动用户端前端：
```bash
cd frontend-user
npm install
npm run dev
```
4. 启动管理后台前端：
```bash
cd frontend-admin
npm install
npm run dev
```

## 服务地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 用户端 | http://localhost:8081 | 用户租赁充电宝 |
| 管理后台 | http://localhost:8082 | 管理员管理系统 |
| 后端 API | http://localhost:8080 | Spring Boot API |
| MySQL | localhost:3307 | 数据库 |

## 测试账号

| 角色 | 账号 | 密码 |
|------|------|------|
| 管理员 | admin | admin123 |
| 普通用户 | testuser | user123 |

## 功能说明

### 用户端功能
- 可用充电宝列表，显示费用
- 历史订单，显示使用时间、应付金额、支付状态、超时状态
- 归还充电宝，显示当前进行中订单
- 个人信息编辑（修改用户名、实名信息、修改密码）
- 按编号查询充电宝状态

### 管理后台功能
- 仪表盘（统计数据、最近订单）
- 用户管理（增删改查、按字段搜索）
- 充电宝管理（增删改、状态变更、按编号查询）
- 订单管理（查看所有订单、按用户搜索）
- 个人中心

## 技术栈

- Backend: Java 17 + Spring Boot 3.2 + MyBatis-Plus + MySQL 8.0
- Frontend User: Vue 3 + Vite + 自定义组件 + Scss + Pinia
- Frontend Admin: Vue 3 + Vite + Element Plus + Scss + Pinia
- Deploy: Docker + Docker Compose

## 项目文档

- [项目设计文档](docs/project_design.md)
# power_bank
# power_bank
