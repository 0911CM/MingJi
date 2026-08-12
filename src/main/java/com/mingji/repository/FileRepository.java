package com.mingji.repository;

import com.mingji.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 文件仓库
 */
public interface FileRepository extends JpaRepository<File, Long> {

    long countByUserId(Long userId);
}