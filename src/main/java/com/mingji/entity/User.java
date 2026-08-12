package com.mingji.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户名（登录名） */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** 密码 */
    @Column(nullable = false)
    private String password;

    /** 昵称 */
    private String nickname;

    /** 真实姓名 */
    @Column(name = "real_name")
    private String realName;

    /** 头像路径 */
    private String avatar;

    /** 学校 */
    private String school;

    /** 个性签名 */
    private String signature;

    /** 个人简介 */
    @Column(columnDefinition = "TEXT")
    private String bio;

    /** 主题: light/dark */
    private String theme = "light";

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
    }
}