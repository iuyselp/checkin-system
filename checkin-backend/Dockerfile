# 后端 Dockerfile
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# 复制 pom.xml
COPY pom.xml .

# 下载依赖
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 构建项目
RUN mvn clean package -DskipTests -B

# 运行阶段
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 复制构建好的 jar 包
COPY --from=builder /app/target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]
