package com.mingji.repository;

import com.mingji.entity.ContentImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 内容图片仓库
 */
public interface ContentImageRepository extends JpaRepository<ContentImage, Long> {

    List<ContentImage> findByContentIdOrderBySortOrderAsc(Long contentId);

    void deleteByContentId(Long contentId);
}