package com.mingji.service;

import com.mingji.entity.Content;
import com.mingji.repository.ContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 内容服务
 */
@Service
public class ContentService {

    private final ContentRepository contentRepository;

    public ContentService(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    /**
     * 发布内容（日记/创意/随手记/句子）
     */
    @Transactional
    public Content publish(Long userId, String contentType, String title, String contentText,
                           String location, String quoteAuthor, String quoteSource) {
        Content content = new Content();
        content.setUserId(userId);
        content.setContentType(contentType);
        content.setTitle(title);
        content.setContent(contentText);
        content.setLocation(location);
        content.setQuoteAuthor(quoteAuthor);
        content.setQuoteSource(quoteSource);
        content.setStatus("PUBLISHED");
        content.setPrivacy("PRIVATE");
        return contentRepository.save(content);
    }

    /**
     * 保存草稿
     */
    @Transactional
    public Content saveDraft(Long userId, String contentType, String title, String contentText,
                             String location, String quoteAuthor, String quoteSource) {
        Content content = new Content();
        content.setUserId(userId);
        content.setContentType(contentType != null ? contentType : "DIARY");
        content.setTitle(title);
        content.setContent(contentText);
        content.setLocation(location);
        content.setQuoteAuthor(quoteAuthor);
        content.setQuoteSource(quoteSource);
        content.setStatus("DRAFT");
        content.setPrivacy("PRIVATE");
        return contentRepository.save(content);
    }

    /**
     * 查询用户指定类型的发布内容
     */
    public List<Content> getPublishedByType(Long userId, String contentType) {
        return contentRepository.findByUserIdAndContentTypeAndStatusOrderByCreatedAtDesc(
                userId, contentType, "PUBLISHED");
    }

    /**
     * 查询用户全部发布内容
     */
    public List<Content> getPublished(Long userId) {
        return contentRepository.findByUserIdAndStatusOrderByCreatedAtDesc(
                userId, "PUBLISHED", org.springframework.data.domain.PageRequest.of(0, 20)).getContent();
    }
}