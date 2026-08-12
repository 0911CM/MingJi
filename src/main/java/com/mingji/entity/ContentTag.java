package com.mingji.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容-标签关联实体
 */
@Data
@Entity
@Table(name = "content_tag")
public class ContentTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 内容 ID */
    @Column(name = "content_id", nullable = false)
    private Long contentId;

    /** 标签 ID */
    @Column(name = "tag_id", nullable = false)
    private Long tagId;

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