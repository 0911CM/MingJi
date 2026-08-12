-- ============================================================
-- MingJi 铭记 · 个人数字生活空间
-- 数据库初始化脚本（Phase 1 基础结构）
-- ============================================================

CREATE DATABASE IF NOT EXISTS mingji
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE mingji;

-- ============================================================
-- 1. 用户表
-- ============================================================
CREATE TABLE IF NOT EXISTS `user` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username`     VARCHAR(50)  NOT NULL COMMENT '用户名（登录名）',
  `password`     VARCHAR(255) NOT NULL COMMENT '密码（BCrypt 加密）',
  `nickname`     VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
  `real_name`    VARCHAR(50)  DEFAULT NULL COMMENT '真实姓名',
  `avatar`       VARCHAR(255) DEFAULT NULL COMMENT '头像文件路径',
  `school`       VARCHAR(100) DEFAULT NULL COMMENT '学校',
  `signature`    VARCHAR(255) DEFAULT NULL COMMENT '个性签名',
  `bio`          TEXT         DEFAULT NULL COMMENT '个人简介',
  `theme`        VARCHAR(20)  DEFAULT 'light' COMMENT '主题: light/dark',
  `created_at`   DATETIME     NOT NULL COMMENT '创建时间（由应用层填充）',
  `updated_at`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================================
-- 2. 内容主表（可扩展内容模型）
--    content_type: DIARY / IDEA / NOTE / QUOTE（未来可扩展）
-- ============================================================
CREATE TABLE IF NOT EXISTS `content` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`      BIGINT       NOT NULL COMMENT '所属用户',
  `content_type` VARCHAR(20)  NOT NULL COMMENT '内容类型: DIARY/IDEA/NOTE/QUOTE',
  `title`        VARCHAR(255) DEFAULT NULL COMMENT '标题',
  `content`      LONGTEXT     DEFAULT NULL COMMENT '正文内容',
  `cover_image`  VARCHAR(255) DEFAULT NULL COMMENT '封面图片路径',
  `location`     VARCHAR(255) DEFAULT NULL COMMENT '地点',
  `privacy`      VARCHAR(20)  NOT NULL DEFAULT 'PRIVATE' COMMENT '隐私: PRIVATE/FAMILY/PUBLIC',
  `status`       VARCHAR(20)  NOT NULL DEFAULT 'PUBLISHED' COMMENT '状态: PUBLISHED/DRAFT',
  `is_favorite`  TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否收藏',
  `is_pinned`    TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否置顶',
  `quote_source` VARCHAR(255) DEFAULT NULL COMMENT '句子来源（QUOTE 类型）',
  `quote_author` VARCHAR(100) DEFAULT NULL COMMENT '句子作者（QUOTE 类型）',
  `quote_note`   TEXT         DEFAULT NULL COMMENT '我的备注（QUOTE 类型）',
  `created_at`   DATETIME     NOT NULL COMMENT '创建时间（由应用层填充）',
  `updated_at`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_type` (`user_id`, `content_type`),
  KEY `idx_user_status` (`user_id`, `status`),
  KEY `idx_favorite` (`is_favorite`),
  KEY `idx_pinned` (`is_pinned`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容主表（日记/创意/随手记/句子）';

-- ============================================================
-- 3. 内容图片表
-- ============================================================
CREATE TABLE IF NOT EXISTS `content_image` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `content_id`  BIGINT       NOT NULL COMMENT '内容 ID',
  `file_path`   VARCHAR(255) NOT NULL COMMENT '图片文件路径',
  `caption`     VARCHAR(255) DEFAULT NULL COMMENT '图片说明',
  `sort_order`  INT          NOT NULL DEFAULT 0 COMMENT '排序（从小到大）',
  `created_at`  DATETIME     NOT NULL COMMENT '创建时间（由应用层填充）',
  PRIMARY KEY (`id`),
  KEY `idx_content` (`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容图片表';

-- ============================================================
-- 4. 待办事项表
-- ============================================================
CREATE TABLE IF NOT EXISTS `todo` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`     BIGINT       NOT NULL COMMENT '所属用户',
  `title`       VARCHAR(255) NOT NULL COMMENT '待办内容',
  `priority`    VARCHAR(10)  NOT NULL DEFAULT 'NORMAL' COMMENT '优先级: HIGH/MEDIUM/NORMAL/LOW',
  `is_done`     TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否完成',
  `due_date`    DATE         DEFAULT NULL COMMENT '截止日期',
  `created_at`  DATETIME     NOT NULL COMMENT '创建时间（由应用层填充）',
  `updated_at`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='待办事项表';

-- ============================================================
-- 5. 标签表
-- ============================================================
CREATE TABLE IF NOT EXISTS `tag` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`     BIGINT       NOT NULL COMMENT '所属用户',
  `name`        VARCHAR(50)  NOT NULL COMMENT '标签名称',
  `created_at`  DATETIME     NOT NULL COMMENT '创建时间（由应用层填充）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_name` (`user_id`, `name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- ============================================================
-- 6. 内容-标签关联表
-- ============================================================
CREATE TABLE IF NOT EXISTS `content_tag` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `content_id`  BIGINT   NOT NULL COMMENT '内容 ID',
  `tag_id`      BIGINT   NOT NULL COMMENT '标签 ID',
  `created_at`  DATETIME NOT NULL COMMENT '创建时间（由应用层填充）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_content_tag` (`content_id`, `tag_id`),
  KEY `idx_tag` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容-标签关联表';

-- ============================================================
-- 7. 文件表
-- ============================================================
CREATE TABLE IF NOT EXISTS `file` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`       BIGINT       NOT NULL COMMENT '所属用户',
  `original_name` VARCHAR(255) NOT NULL COMMENT '原始文件名',
  `file_name`     VARCHAR(191) NOT NULL COMMENT '存储文件名（唯一）',
  `file_path`     VARCHAR(500) NOT NULL COMMENT '存储路径',
  `file_type`     VARCHAR(20)  NOT NULL COMMENT '类型: JPG/JPEG/PNG/WEBP',
  `file_size`     BIGINT       NOT NULL COMMENT '文件大小（字节）',
  `created_at`    DATETIME     NOT NULL COMMENT '创建时间（由应用层填充）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_file_name` (`file_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件表';

-- ============================================================
-- 8. 默认用户（Phase 1 单人使用，密码为占位）
--    用户名: mingji / 密码: 将在 Phase 2 实现登录时正式处理
-- ============================================================
INSERT IGNORE INTO `user` (`username`, `password`, `nickname`, `real_name`, `school`, `signature`, `created_at`)
VALUES ('mingji', '{noop}mingji123', '诚铭', '刘佳诚', '湖南商务职业技术学院', '记录生活，也记录自己。', NOW());
