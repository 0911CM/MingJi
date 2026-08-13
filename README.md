# MingJi 铭记 · 个人数字生活空间

> 记录生活，也记录自己。

「铭记」是一个以个人使用为第一目标的数字生活记录空间——私人日记、个人朋友圈、灵感收集箱、名句收藏夹、待办记录、照片故事。

## ✨ 核心理念

> **首页负责记录。系统负责整理。日常负责保存生活。灵感负责保存思想。我的负责保存「我」。**

打开首页，直接写下内容，选择分类保存，系统自动整理到对应页面。

## 🚀 快速开始

### 环境要求

| 依赖 | 版本 |
|------|------|
| JDK | 17+ |
| MySQL | 8.0+（本机 26.7 已验证） |
| Maven | 使用项目自带的 Maven Wrapper（无需全局安装） |

### 1. 初始化数据库

```sql
-- 在 MySQL 中执行（root 密码为空）
mysql -u root < sql/init.sql
```

脚本会自动创建 `mingji` 数据库、7 张核心表，并插入默认用户。

### 2. 启动应用

应用启用了整站密码门禁。启动前需要设置站点密码的 BCrypt 哈希和一个固定的
Remember-Me 签名密钥；二者均不得提交到 Git：

```text
MINGJI_ACCESS_PASSWORD_HASH='$2a$...'
MINGJI_REMEMBER_KEY=至少32位随机字符串
```

验证成功后浏览器会保持一年登录状态。网站内不提供修改密码功能；需要更换时，
修改服务器环境变量并重启应用。修改任一配置都会使已有的长期登录 Cookie 失效。

```bash
# Windows
mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

### 3. 访问

打开浏览器访问：**http://localhost:8080**

## 📱 页面与功能（Phase 1）

| 路由 | 页面 | 说明 |
|------|------|------|
| `/` | 首页 | 核心记录工作台：大输入框 + 分类选择 + 置顶/最近记录 |
| `/daily` | 日常 | 我的日常 · 只属于自己的朋友圈（Phase 2 接入数据） |
| `/inspiration` | 灵感 | 创意 / 待办 / 随手记 / 句子 四个分类空间（Phase 3 接入数据） |
| `/me` | 我的 | 个人资料 · 数据统计 · 功能入口 |

### 设计亮点

- 🔵 **移动端优先**：底部固定导航，大尺寸触控按钮，适配 320px ~ 1440px+
- 🎨 **高级简约**：黑白灰基础色 + 陶土暖色强调，摄影作品集风格
- 🌗 **主题支持**：CSS 变量实现浅色/深色模式（Phase 4 完善切换逻辑）
- 📱 **响应式**：移动端底部导航，PC 端顶部导航

## 🗃 数据库设计

采用**可扩展内容模型**——不把日记/创意/句子拆成独立满表，而是统一存入 `content` 表：

```
content_type: DIARY / IDEA / NOTE / QUOTE
```

未来可轻松扩展 `BOOK / TRAVEL / PROJECT / DREAM` 等类型。

| 表 | 说明 |
|------|------|
| `user` | 用户（单人使用） |
| `content` | 内容主表（日记/创意/随手记/句子） |
| `content_image` | 内容图片 |
| `todo` | 待办事项 |
| `tag` | 标签 |
| `content_tag` | 内容-标签关联 |
| `file` | 文件记录 |

## 🏗 技术栈

- **Spring Boot 3.3.5**
- **Spring MVC**（页面路由）
- **Spring Data JPA**（数据访问）
- **Thymeleaf**（服务端模板）
- **MySQL 8+**
- **Maven Wrapper**（无需全局 Maven）

## 📁 项目结构

```
MingJi/
├── sql/
│   ├── init.sql          # 数据库初始化脚本（可重复执行）
│   └── verify.sql        # 验证脚本
├── src/
│   ├── main/
│   │   ├── java/com/mingji/
│   │   │   ├── config/       # Web 配置（上传目录映射）
│   │   │   ├── controller/   # 页面控制器
│   │   │   ├── entity/       # JPA 实体
│   │   │   └── repository/   # Spring Data 仓库
│   │   └── resources/
│   │       ├── static/       # CSS / JS
│   │       ├── templates/    # Thymeleaf 模板
│   │       └── application.yml
│   └── test/
├── mvnw / mvnw.cmd       # Maven Wrapper
└── pom.xml
```

## 🗺 开发路线

| 阶段 | 内容 | 状态 |
|------|------|------|
| **Phase 1** | 项目骨架 · 基础页面 · 底部导航 · 响应式 | ✅ 已完成 |
| Phase 2 | 登录 · 日记 · 草稿 · 收藏 · 置顶 · 图片上传 | ⏳ 待开发 |
| Phase 3 | 创意 · 待办 · 随手记 · 句子 | ⏳ 待开发 |
| Phase 4 | REST API · 数据统计 · 分享 · 深色模式 | ⏳ 待开发 |
| Phase 5 | 性能优化 · 安全 · 部署 · GitHub · Docker | ⏳ 待开发 |

## 👤 默认用户（Phase 1 占位）

- 用户名：`mingji`
- 密码：`mingji123`（占位，Phase 2 实现登录后处理）
- 昵称：MingJi

> ⚠️ 密码/资料将在 Phase 2 登录功能完成后正式处理。

---

Made with ❤️ by MingJi