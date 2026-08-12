package com.mingji.repository;

import com.mingji.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 待办事项仓库
 */
public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Todo> findByUserIdAndDoneOrderByCreatedAtDesc(Long userId, Boolean done);

    long countByUserIdAndDone(Long userId, Boolean done);
}