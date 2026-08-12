package com.mingji.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容图片实体
 */
@Data
@Entity
@Table(name = "content_image")
public class ContentImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 内容 ID */
    @Column(name = "content_id", nullable = false)
    private Long contentId;

    /** 图片文件路径 */
    @Column(name = "file_path", nullable = false)
    private String filePath;

    /** 图片说明 */
    private String caption;

    /** 排序（从小到大） */
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    /** 创建时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}