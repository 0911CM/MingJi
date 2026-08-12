#!/bin/bash
# ============================================================
# MingJi 一键部署脚本（阿里云 Ubuntu 服务器）
# 用法：复制整段执行即可
# ============================================================
set -e

echo "========================================="
echo "  MingJi 一键部署脚本"
echo "========================================="

# ---------- 1. 检查 root 权限 ----------
if [ "$EUID" -ne 0 ]; then
  echo "请使用 root 用户执行！"
  exit 1
fi

# ---------- 2. 安装 Docker ----------
if ! command -v docker &> /dev/null; then
  echo "[1/5] 安装 Docker..."
  curl -fsSL https://get.docker.com | sh
  systemctl enable docker
  systemctl start docker
else
  echo "[1/5] Docker 已安装 ✅"
fi

docker --version

# ---------- 3. 安装 Docker Compose 插件 ----------
if ! docker compose version &> /dev/null; then
  echo "[2/5] 安装 Docker Compose..."
  apt-get update -y
  apt-get install -y docker-compose-plugin
else
  echo "[2/5] Docker Compose 已安装 ✅"
fi

# ---------- 4. 克隆项目 ----------
if [ ! -d /opt/MingJi ]; then
  echo "[3/5] 克隆项目..."
  apt-get install -y git
  mkdir -p /opt
  cd /opt
  git clone https://github.com/0911CM/MingJi.git
else
  echo "[3/5] 项目已存在，更新代码..."
  cd /opt/MingJi
  git pull
fi

cd /opt/MingJi

# ---------- 5. 创建数据库数据目录 ----------
mkdir -p /opt/mingji-db
mkdir -p /opt/mingji-uploads

# ---------- 6. 创建 docker-compose.yml ----------
echo "[4/5] 创建 docker-compose.yml..."

# 如果已存在则备份
if [ -f docker-compose.yml ]; then
  cp docker-compose.yml docker-compose.yml.bak
fi

cat > docker-compose.yml << 'EOF'
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: mingji-mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: Mingji123456!
      MYSQL_DATABASE: mingji
      MYSQL_USER: mingji
      MYSQL_PASSWORD: Mingji123456!
    volumes:
      - /opt/mingji-db:/var/lib/mysql
    ports:
      - "3306:3306"
    command: --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-umingji", "-pMingji123456!"]
      interval: 10s
      timeout: 5s
      retries: 10

  app:
    build: .
    container_name: mingji-app
    restart: always
    depends_on:
      mysql:
        condition: service_healthy
    environment:
      MYSQL_URL: jdbc:mysql://mysql:3306/mingji?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8&allowPublicKeyRetrieval=true
      MYSQL_USER: mingji
      MYSQL_PASSWORD: Mingji123456!
      MINGJI_UPLOAD_DIR: /var/data/mingji/uploads/
      SPRING_PROFILES_ACTIVE: prod
    ports:
      - "8080:8080"
EOF

echo "========================================="
echo "  docker-compose.yml 已创建 ✅"
echo "========================================="

# ---------- 7. 启动 ----------
echo "[5/5] 构建并启动应用（首次构建需要 3-5 分钟）..."
docker compose down --remove-orphans 2>/dev/null || true
docker compose up -d --build

echo ""
echo "========================================="
echo "  🚀 部署完成！"
echo "========================================="
echo ""
echo "  📱 手机访问地址："
echo "  http://$(curl -s ifconfig.me):8080"
echo ""
echo "  👤 默认用户：mingji"
echo "  🔑 默认密码：mingji123"
echo ""
echo "  📊 查看日志命令："
echo "  sudo docker compose -f /opt/MingJi/docker-compose.yml logs -f app"
echo ""
echo "  ⚠️  如果打不开，请检查阿里云安全组是否放行 8080 和 3306 端口！"
echo ""