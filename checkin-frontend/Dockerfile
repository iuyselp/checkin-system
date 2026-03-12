# 前端 Dockerfile
FROM node:18-alpine AS builder

WORKDIR /app

# 复制 package.json
COPY package.json .

# 安装依赖
RUN npm install --registry=https://registry.npmmirror.com

# 复制源代码
COPY . .

# 构建项目
RUN npm run build

# Nginx 运行阶段
FROM nginx:alpine

# 复制自定义 nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 复制构建好的静态文件
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
