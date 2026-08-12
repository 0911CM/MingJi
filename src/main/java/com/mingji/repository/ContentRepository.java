package com.mingji.repository;

import com.mingji.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 内容仓库
 */
public interface ContentRepository extends JpaRepository<Content, Long> {

    /** 按用户、类型、状态查询（发布内容，分页） */
    Page<Content> findByUserIdAndContentTypeAndStatusOrderByCreatedAtDesc(
            Long userId, String contentType, String status, Pageable pageable);

    /** 按用户、状态查询全部类型内容（分页） */
    Page<Content> findByUserIdAndStatusOrderByCreatedAtDesc(
            Long userId, String status, Pageable pageable);

    /** 按用户、状态、收藏查询 */
    Page<Content> findByUserIdAndStatusAndFavoriteOrderByCreatedAtDesc(
            Long userId, String status, Boolean favorite, Pageable pageable);

    /** 按用户、状态、置顶查询 */
    Page<Content> findByUserIdAndStatusAndPinnedOrderByCreatedAtDesc(
            Long userId, String status, Boolean pinned, Pageable pageable);

    /** 按用户、类型、状态查询（列表） */
    List<Content> findByUserIdAndContentTypeAndStatusOrderByCreatedAtDesc(
            Long userId, String contentType, String status);

    /** 统计用户指定类型、指定状态的内容数量 */
    long countByUserIdAndContentTypeAndStatus(Long userId, String contentType, String status);

    /** 统计用户收藏数量 */
    long countByUserIdAndStatusAndFavorite(Long userId, String status, Boolean favorite);
}