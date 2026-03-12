# 打卡签到系统

基于位置的员工考勤打卡系统，支持 GPS 定位打卡、考勤统计等功能。

## 技术栈

### 后端
- Spring Boot 3.2
- MyBatis-Plus 3.5
- MySQL 8.0
- Redis 7.x
- JWT 认证

### 前端
- Vue 3 + TypeScript
- Vite 5.x
- Element Plus
- Pinia 状态管理
- Vue Router

## 项目结构

```
checkin-system/
├── docs/                    # 文档
│   ├── 01-需求文档.md
│   └── 02-设计文档.md
├── checkin-backend/         # 后端项目
│   ├── src/main/java/com/checkin/
│   │   ├── common/          # 公共类
│   │   ├── config/          # 配置类
│   │   ├── controller/      # 控制器
│   │   ├── dto/             # 数据传输对象
│   │   ├── entity/          # 实体类
│   │   ├── mapper/          # Mapper 接口
│   │   ├── service/         # 服务层
│   │   └── utils/           # 工具类
│   └── src/main/resources/
│       ├── application.yml  # 配置文件
│       └── schema.sql       # 数据库脚本
├── checkin-frontend/        # 前端项目
│   ├── src/
│   │   ├── api/             # API 接口
│   │   ├── router/          # 路由
│   │   ├── views/           # 页面组件
│   │   └── main.ts          # 入口文件
│   └── package.json
└── README.md
```

## 快速开始

### 环境要求
- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Redis 7.x

### 1. 数据库初始化

```bash
# 登录 MySQL
mysql -u root -p

# 执行初始化脚本
source checkin-backend/src/main/resources/schema.sql
```

### 2. 启动后端

```bash
cd checkin-backend

# 修改配置文件 application.yml 中的数据库连接信息

# 启动
mvn spring-boot:run
# 或
./mvnw spring-boot:run
```

后端服务将运行在：http://localhost:8080/api

### 3. 启动前端

```bash
cd checkin-frontend

# 安装依赖
npm install

# 启动开发服务器（允许外部访问）
npm run dev
```

前端服务将运行在：`http://0.0.0.0:5173`

**允许外部访问：**
- 本机访问：http://localhost:5173
- 局域网访问：http://你的IP:5173
- 其他服务器：http://服务器IP:5173

**注意：** 确保防火墙开放 5173 端口

## 默认账号

- 用户名：`admin`
- 密码：`admin123`

## API 接口

### 用户模块
- `POST /api/user/register` - 用户注册
- `POST /api/user/login` - 用户登录
- `GET /api/user/info` - 获取用户信息

### 打卡模块
- `POST /api/checkin/work` - 上班打卡
- `POST /api/checkin/off` - 下班打卡
- `GET /api/checkin/records` - 查询打卡记录
- `GET /api/checkin/today/status` - 获取今日打卡状态

### 打卡点模块
- `GET /api/point/list` - 获取打卡点列表
- `POST /api/point` - 创建打卡点
- `PUT /api/point/{id}` - 更新打卡点
- `DELETE /api/point/{id}` - 删除打卡点
- `POST /api/point/check` - 校验位置

### 统计模块
- `GET /api/stat/personal` - 个人考勤统计

## 配置说明

### 后端配置 (application.yml)

```yaml
# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/checkin
    username: root
    password: your_password

# Redis 配置
  data:
    redis:
      host: localhost
      port: 6379

# JWT 配置
jwt:
  secret: your-secret-key
  expiration: 7200000  # 2 小时

# 打卡配置
checkin:
  default-radius: 100  # 默认打卡半径（米）
  late-grace-minutes: 10  # 迟到容忍时间
```

### 前端配置

前端代理配置在 `vite.config.ts` 中：

```typescript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## 功能特性

- ✅ GPS 定位打卡
- ✅ 打卡范围校验
- ✅ 上下班打卡
- ✅ 迟到/早退判断
- ✅ 打卡记录查询
- ✅ 考勤统计
- ✅ 多打卡点管理
- ✅ JWT 认证
- ✅ 响应式设计

## 开发计划

- [ ] 支持 WiFi 打卡
- [ ] 支持人脸识别
- [ ] 支持请假申请
- [ ] 支持审批流程
- [ ] 导出 Excel 报表
- [ ] 移动端适配优化
- [ ] 消息通知

## 注意事项

1. 首次使用需要初始化数据库
2. 打卡功能需要浏览器支持地理位置 API
3. 生产环境请修改 JWT 密钥
4. 建议配置 HTTPS 以保证位置信息安全

## License

MIT
