# ============================================================
# MingJi · Docker 镜像（用于 Render / 云服务器部署）
# ============================================================

# ---------- 阶段 1：编译 ----------
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# 先复制 pom 缓存依赖（加速构建）
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# 复制源码并打包
COPY src ./src
RUN mvn clean package -DskipTests

# ---------- 阶段 2：运行 ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# 复制 jar
COPY --from=builder /app/target/mingji-0.1.0.jar app.jar

# 创建上传目录
RUN mkdir -p /var/data/mingji/uploads

# 暴露端口（Render 通过 PORT 环境变量指定）
EXPOSE 8080

# 启动
# 生产环境通过 SPRING_PROFILES_ACTIVE=prod 激活云端配置
CMD ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]