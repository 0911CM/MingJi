package com.mingji.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件实体
 */
@Data
@Entity
@Table(name = "file")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属用户 */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 原始文件名 */
    @Column(name = "original_name", nullable = false)
    private String originalName;

    /** 存储文件名（唯一） */
    @Column(name = "file_name", nullable = false, unique = true, length = 191)
    private String fileName;

    /** 存储路径 */
    @Column(name = "file_path", nullable = false)
    private String filePath;

    /** 文件类型: JPG/JPEG/PNG/WEBP */
    @Column(name = "file_type", nullable = false, length = 20)
    private String fileType;

    /** 文件大小（字节） */
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

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