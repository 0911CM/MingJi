package com.mingji.repository;

import com.mingji.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 标签仓库
 */
public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Tag> findByUserIdAndName(Long userId, String name);

    long countByUserId(Long userId);
}