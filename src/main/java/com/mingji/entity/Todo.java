package com.mingji.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 待办事项实体
 */
@Data
@Entity
@Table(name = "todo")
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属用户 */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 待办内容 */
    @Column(nullable = false)
    private String title;

    /** 优先级: HIGH/MEDIUM/NORMAL/LOW */
    @Column(nullable = false, length = 10)
    private String priority = "NORMAL";

    /** 是否完成 */
    @Column(name = "is_done", nullable = false)
    private Boolean done = false;

    /** 截止日期 */
    @Column(name = "due_date")
    private LocalDate dueDate;

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