package com.mingji.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容主表实体（可扩展内容模型）
 * content_type: DIARY / IDEA / NOTE / QUOTE
 */
@Data
@Entity
@Table(name = "content")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属用户 */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 内容类型: DIARY/IDEA/NOTE/QUOTE */
    @Column(name = "content_type", nullable = false, length = 20)
    private String contentType;

    /** 标题 */
    private String title;

    /** 正文内容 */
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    /** 封面图片路径 */
    @Column(name = "cover_image")
    private String coverImage;

    /** 地点 */
    private String location;

    /** 隐私: PRIVATE/FAMILY/PUBLIC */
    @Column(nullable = false, length = 20)
    private String privacy = "PRIVATE";

    /** 状态: PUBLISHED/DRAFT */
    @Column(nullable = false, length = 20)
    private String status = "PUBLISHED";

    /** 是否收藏 */
    @Column(name = "is_favorite", nullable = false)
    private Boolean favorite = false;

    /** 是否置顶 */
    @Column(name = "is_pinned", nullable = false)
    private Boolean pinned = false;

    /** 句子来源（QUOTE 类型） */
    @Column(name = "quote_source")
    private String quoteSource;

    /** 句子作者（QUOTE 类型） */
    @Column(name = "quote_author", length = 100)
    private String quoteAuthor;

    /** 我的备注（QUOTE 类型） */
    @Column(name = "quote_note", columnDefinition = "TEXT")
    private String quoteNote;

    /** 创建时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}