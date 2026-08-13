package com.mingji;

import com.mingji.entity.User;
import com.mingji.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * 铭记 MingJi · 个人数字生活空间
 * 应用启动入口
 */
@SpringBootApplication
public class MingJiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MingJiApplication.class, args);
    }

    /**
     * 启动时自动创建默认用户（云端数据库为空时）
     * 用户名: mingji / 密码: mingji123（占位，Phase 2 登录功能完善后替换）
     */
    @Bean
    public CommandLineRunner initDefaultUser(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("mingji").isEmpty()) {
                User user = new User();
                user.setUsername("mingji");
                user.setPassword("{noop}mingji123");
                user.setNickname("MingJi");
                user.setRealName("MingJi");
                user.setSchool(null);
                user.setSignature("记录生活，也记录自己。");
                userRepository.save(user);
                System.out.println("[MingJi] 默认用户已创建: mingji / mingji123");
            }
        };
    }
}