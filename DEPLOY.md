# 部署指南 - 允许外部访问

## 🌐 网络配置

### 前端开发服务器

已配置允许外部访问，其他服务器可以通过 IP 地址访问。

**启动命令：**
```bash
cd checkin-frontend
npm install
npm run dev
```

**访问地址：**
- 本机：`http://localhost:5173`
- 局域网：`http://你的内网IP:5173`
- 外网：`http://服务器公网IP:5173`

### 后端服务器

**启动命令：**
```bash
cd checkin-backend
mvn spring-boot:run
# 或
./mvnw spring-boot:run
```

**访问地址：**
- API 地址：`http://服务器IP:8080/api`

---

## 🔥 防火墙配置

### Linux (firewalld)
```bash
# 开放前端端口
sudo firewall-cmd --permanent --add-port=5173/tcp
# 开放后端端口
sudo firewall-cmd --permanent --add-port=8080/tcp
# 重载配置
sudo firewall-cmd --reload
```

### Linux (ufw)
```bash
sudo ufw allow 5173/tcp
sudo ufw allow 8080/tcp
sudo ufw reload
```

### Linux (iptables)
```bash
sudo iptables -A INPUT -p tcp --dport 5173 -j ACCEPT
sudo iptables -A INPUT -p tcp --dport 8080 -j ACCEPT
sudo service iptables save
```

### 云服务器安全组

**阿里云/腾讯云/华为云：**
1. 进入控制台 → 安全组
2. 添加入站规则：
   - 端口：5173，协议：TCP，授权对象：0.0.0.0/0
   - 端口：8080，协议：TCP，授权对象：0.0.0.0/0

---

## 🐳 Docker 部署（推荐）

### 使用 Docker Compose

```bash
cd checkin-system/docker
docker-compose up -d
```

**服务端口：**
- 前端：http://服务器IP:80
- 后端：http://服务器IP:8080
- MySQL：服务器IP:3306
- Redis：服务器IP:6379

### 修改配置

编辑 `docker-compose.yml` 中的环境变量：

```yaml
services:
  backend:
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/checkin
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: your_password
      SPRING_DATA_REDIS_HOST: redis
```

---

## 📝 生产环境配置

### 1. 修改数据库配置

编辑 `checkin-backend/src/main/resources/application-prod.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://你的数据库地址:3306/checkin
    username: 你的用户名
    password: 你的密码
```

### 2. 修改 Redis 配置

```yaml
spring:
  data:
    redis:
      host: 你的 Redis 地址
      port: 6379
      password: 你的密码（如果有）
```

### 3. 修改 JWT 密钥

```yaml
jwt:
  secret: 你的强密钥（至少 32 位）
```

### 4. 打包部署

**后端打包：**
```bash
cd checkin-backend
mvn clean package -DskipTests -Pprod
```

**前端打包：**
```bash
cd checkin-frontend
npm install
npm run build
```

**运行：**
```bash
# 后端
java -jar target/checkin-backend-1.0.0.jar --spring.profiles.active=prod

# 前端（使用 Nginx）
# 将 dist/ 目录部署到 Nginx 的 html 目录
```

---

## 🔧 Nginx 配置（生产环境）

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 前端静态文件
    location / {
        root /usr/share/nginx/html;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    # 后端 API 代理
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # 超时配置
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # 缓存静态资源
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # Gzip 压缩
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml;
}
```

---

## ✅ 验证部署

### 1. 检查服务状态
```bash
# 检查端口监听
netstat -tlnp | grep -E '5173|8080'

# 检查进程
ps aux | grep -E 'node|java'
```

### 2. 测试 API
```bash
# 测试后端健康检查
curl http://服务器IP:8080/api/actuator/health

# 测试登录接口
curl -X POST http://服务器IP:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### 3. 访问前端
浏览器打开：`http://服务器IP:5173`

---

## 🚨 常见问题

### 问题 1：无法访问前端
**解决：**
```bash
# 检查防火墙
sudo firewall-cmd --list-ports

# 检查 Vite 配置
cat checkin-frontend/vite.config.ts
# 确保 host: '0.0.0.0'
```

### 问题 2：跨域错误
**解决：** 确保后端配置了 CORS（已在配置中启用）

### 问题 3：API 请求失败
**解决：** 检查代理配置和后端服务是否启动

---

## 📞 技术支持

如有问题，请查看：
- 前端日志：终端输出
- 后端日志：`logs/checkin-system.log`
- Nginx 日志：`/var/log/nginx/error.log`
