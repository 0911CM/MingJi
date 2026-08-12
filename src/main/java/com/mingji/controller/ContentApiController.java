package com.mingji.controller;

import com.mingji.entity.Content;
import com.mingji.service.ContentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 内容 API 控制器
 * 发布 / 查询内容（Phase 1 快速实现发布功能）
 */
@RestController
@RequestMapping("/api/content")
public class ContentApiController {

    /** 默认用户 ID（Phase 1 单人使用） */
    private static final Long DEFAULT_USER_ID = 1L;

    private final ContentService contentService;

    public ContentApiController(ContentService contentService) {
        this.contentService = contentService;
    }

    /**
     * 发布内容
     * POST /api/content/publish
     */
    @PostMapping("/publish")
    public ResponseEntity<Map<String, Object>> publish(@RequestBody Map<String, String> body) {
        String contentType = body.getOrDefault("contentType", "DIARY");
        String title = body.get("title");
        String content = body.get("content");
        String location = body.get("location");
        String quoteAuthor = body.get("quoteAuthor");
        String quoteSource = body.get("quoteSource");

        Content saved = contentService.publish(
                DEFAULT_USER_ID, contentType, title, content, location, quoteAuthor, quoteSource);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "发布成功");
        result.put("id", saved.getId());
        result.put("contentType", saved.getContentType());
        return ResponseEntity.ok(result);
    }

    /**
     * 保存草稿
     * POST /api/content/draft
     */
    @PostMapping("/draft")
    public ResponseEntity<Map<String, Object>> saveDraft(@RequestBody Map<String, String> body) {
        String contentType = body.getOrDefault("contentType", "DIARY");
        String title = body.get("title");
        String content = body.get("content");
        String location = body.get("location");
        String quoteAuthor = body.get("quoteAuthor");
        String quoteSource = body.get("quoteSource");

        Content saved = contentService.saveDraft(
                DEFAULT_USER_ID, contentType, title, content, location, quoteAuthor, quoteSource);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "草稿已保存");
        result.put("id", saved.getId());
        return ResponseEntity.ok(result);
    }

    /**
     * 查询内容列表
     * GET /api/content?type=DIARY
     */
    @GetMapping
    public ResponseEntity<List<Content>> list(@RequestParam(required = false) String type) {
        List<Content> list;
        if (type != null && !type.isEmpty()) {
            list = contentService.getPublishedByType(DEFAULT_USER_ID, type);
        } else {
            list = contentService.getPublished(DEFAULT_USER_ID);
        }
        return ResponseEntity.ok(list);
    }
}