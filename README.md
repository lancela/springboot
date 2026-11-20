# Spring Boot CRM 示例项目

基于 Spring Boot 3 的客户管理（CRM）学习示例项目，使用 `JdbcTemplate` 完成数据访问，包含客户 CRUD、条件分页查询、参数校验、统一响应/异常处理、事务演示以及 Swagger 接口文档。

## 技术栈

| 技术 | 说明 |
| --- | --- |
| Spring Boot 3.5.16 | 基础框架 |
| Spring Web | RESTful 接口 |
| Spring JDBC (`JdbcTemplate`) | 数据访问，未使用 ORM |
| MySQL 8.x | 数据库 |
| Bean Validation (Jakarta) | 请求参数校验 |
| springdoc-openapi 2.8.13 | Swagger / OpenAPI 接口文档 |
| Maven | 构建工具 |
| JDK 17+ | 运行环境（Spring Boot 3 最低要求） |

## 功能特性

- 客户管理：新增、修改、删除、按 ID 查询、列表查询
- 分页查询：普通分页 + 按名称/公司/手机号条件分页（`LIMIT ? OFFSET ?`）
- 分层结构：Controller → Service → DAO → MySQL
- DTO / Entity / VO 分离，并提供 `CustomerConverter` 转换工具
- 统一响应体 `Result<T>` 与分页结果 `PageResult<T>`
- 全局异常处理：业务异常、参数校验异常、算术异常、兜底异常
- `@Transactional` 事务与回滚演示（客户更新、订单+订单明细）
- 跨域配置（默认放行 `http://localhost:5173`）
- Swagger UI 在线接口文档

## 项目结构

```
springBoot
├── pom.xml
├── http-test.http                 # 接口测试请求示例
└── src
    ├── main
    │   ├── java/org/example/springboot
    │   │   ├── Application.java            # 启动类
    │   │   ├── common                      # Result、PageResult、Converter
    │   │   ├── config                      # WebConfig（CORS）
    │   │   ├── controller                  # Customer/Order/Hello 控制器
    │   │   ├── dao                         # JdbcTemplate 数据访问
    │   │   ├── demos/web                   # Spring MVC 入门示例
    │   │   ├── dto                         # 请求对象（含校验注解）
    │   │   ├── entity                      # 数据库实体
    │   │   ├── exception                   # 业务异常与全局异常处理
    │   │   ├── service                     # 业务逻辑与事务
    │   │   └── vo                          # 响应视图对象
    │   └── resources
    │       ├── application.properties      # 端口与数据源配置
    │       └── static/index.html
    └── test/java/org/example/springboot/ApplicationTests.java
```

## 快速开始

### 1. 环境要求

- JDK 17 或以上
- Maven 3.6+
- MySQL 8.x

### 2. 初始化数据库

创建数据库 `crm_db` 并执行以下建表语句：

```sql
CREATE DATABASE IF NOT EXISTS crm_db
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE crm_db;

CREATE TABLE IF NOT EXISTS customer (
    id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    name    VARCHAR(50)  NOT NULL COMMENT '客户名称',
    phone   VARCHAR(20)  NOT NULL COMMENT '手机号',
    email   VARCHAR(100)          COMMENT '邮箱',
    company VARCHAR(100) NOT NULL COMMENT '公司名称',
    address VARCHAR(255)          COMMENT '地址',
    status  TINYINT      NOT NULL COMMENT '状态：1 正常，0 停用'
);

CREATE TABLE IF NOT EXISTS orders (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no     VARCHAR(64)    NOT NULL COMMENT '订单号',
    customer_id  BIGINT         NOT NULL COMMENT '客户 ID',
    total_amount DECIMAL(12, 2) NOT NULL COMMENT '订单总额'
);

CREATE TABLE IF NOT EXISTS order_item (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id     BIGINT         NOT NULL COMMENT '订单 ID',
    product_name VARCHAR(100)   NOT NULL COMMENT '产品名称',
    quantity     INT            NOT NULL COMMENT '数量',
    price        DECIMAL(12, 2) NOT NULL COMMENT '单价'
);

-- 可选：初始化一条客户数据，用于事务/订单演示
INSERT INTO customer (name, phone, email, company, address, status)
VALUES ('张三', '13800000001', 'zhangsan@example.com', '张三科技有限公司', '浙江宁波', 1);
```

> 订单事务演示接口中固定校验 `customer_id = 6`，如需测试 `POST /orders/test`，请保证 `customer` 表存在 `id = 6` 的记录，或自行调整 `OrderService` 中的校验条件。

### 3. 修改数据源配置

编辑 `src/main/resources/application.properties`：

```properties
server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/crm_db?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8
spring.datasource.username=root
spring.datasource.password=你的数据库密码
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### 4. 启动项目

```bash
# 方式一：Maven 运行
mvn spring-boot:run

# 方式二：打包后运行（pom 中 repackage 默认被跳过，这里显式开启）
mvn clean package -Dspring-boot.repackage.skip=false
java -jar target/springBoot-0.0.1-SNAPSHOT.jar
```

启动成功后访问：

- 应用地址：<http://localhost:8080>
- Swagger UI：<http://localhost:8080/swagger-ui/index.html>
- OpenAPI JSON：<http://localhost:8080/v3/api-docs>

## 接口说明

### 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

- `code = 200` 表示成功，`code = 500` 表示失败
- 分页接口的 `data` 为 `PageResult`：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "pageNum": 1,
    "pageSize": 10,
    "total": 25,
    "pages": 3,
    "records": []
  }
}
```

### 客户管理接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/customers` | 查询全部客户 |
| GET | `/customers/{id}` | 根据 ID 查询客户详情 |
| POST | `/customers` | 新增客户 |
| PUT | `/customers/{id}` | 修改客户 |
| DELETE | `/customers/{id}` | 删除客户 |
| GET | `/customers/pageAll?pageNum=1&pageSize=10` | 分页查询全部客户 |
| GET | `/customers/page?name=&company=&phone=&pageNum=1&pageSize=10` | 条件分页查询 |
| PUT | `/customers/test-transaction/{id}` | 事务回滚演示（修改后抛出异常） |

请求体示例（新增/修改客户，字段受 Bean Validation 约束）：

```json
{
  "name": "张三",
  "phone": "13800000001",
  "email": "zhangsan@example.com",
  "company": "张三科技有限公司",
  "address": "浙江宁波",
  "status": 1
}
```

校验规则：

| 字段 | 规则 |
| --- | --- |
| `name` | 不能为空 |
| `phone` | 不能为空，且匹配 `^1[3-9]\d{9}$` |
| `email` | 需符合邮箱格式（可为空） |
| `company` | 不能为空 |
| `status` | 不能为空 |

分页参数说明：`pageNum` 默认 1，`pageSize` 默认 10，最大 100。

### 订单与事务接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/orders/test` | 演示一个事务内同时写入订单主表和订单明细 |

### 其他示例接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/db/test` | 校验数据库连接，返回当前数据库名 |
| GET | `/test/error` | 演示算术异常的统一处理 |
| GET | `/test/getNull` | 演示异常处理 |
| GET | `/customersId/{id}` | `@PathVariable` 示例 |
| GET | `/customers2?name=张三` | `@RequestParam` 示例 |
| GET | `/customers/search1?name=张三&status=1` | 多参数示例 |
| GET | `/customers/search2?name=张三&status=1&sex=男` | 可选参数示例 |
| POST | `/addCustomer` | `@RequestBody` 接收实体示例 |
| GET | `/hello?name=lisi` | Spring MVC 入门示例 |
| GET | `/user`、`/save_user`、`/html` | Spring MVC 入门示例 |
| GET | `/user/{userId}/roles/{roleId}`、`/javabeat/{regexp1}` | 路径变量与正则示例 |

## 快速验证

```bash
# 数据库连接检查
curl http://localhost:8080/db/test

# 新增客户
curl -X POST http://localhost:8080/customers \
  -H "Content-Type: application/json" \
  -d '{"name":"张三","phone":"13800000001","email":"zhangsan@example.com","company":"张三科技有限公司","address":"浙江宁波","status":1}'

# 分页查询
curl "http://localhost:8080/customers/page?pageNum=1&pageSize=10"

# 条件分页查询
curl "http://localhost:8080/customers/page?name=张三&company=科技&pageNum=1&pageSize=10"
```

## 测试

```bash
mvn test
```

## 说明

- 跨域仅放行 `http://localhost:5173`，如需对接其他前端地址，请修改 `config/WebConfig.java`。
- `pom.xml` 中 `spring-boot-maven-plugin` 配置了 `<skip>true</skip>`，因此直接 `mvn package` 生成的是普通 jar。需要可执行 jar 时，请使用 `-Dspring-boot.repackage.skip=false` 打包，或将配置改为 `<skip>false</skip>`。
- 数据库密码请通过本地配置或环境变量管理，不要提交到仓库。
