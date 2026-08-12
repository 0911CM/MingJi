package com.mingji.controller;

import com.mingji.entity.Content;
import com.mingji.entity.Todo;
import com.mingji.entity.User;
import com.mingji.repository.UserRepository;
import com.mingji.service.ContentService;
import com.mingji.service.TodoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 页面控制器
 * 首页 / 日记 / 灵感 / 我的
 */
@Controller
public class PageController {

    /** 默认用户 ID（Phase 1 单人使用） */
    private static final Long DEFAULT_USER_ID = 1L;

    private final UserRepository userRepository;
    private final ContentService contentService;
    private final TodoService todoService;

    public PageController(UserRepository userRepository, ContentService contentService, TodoService todoService) {
        this.userRepository = userRepository;
        this.contentService = contentService;
        this.todoService = todoService;
    }

    /**
     * 首页 - 我的记录工作台
     */
    @GetMapping("/")
    public String index(Model model) {
        User user = getUser();
        List<Content> recent = contentService.getPublished(DEFAULT_USER_ID);
        model.addAttribute("user", user);
        model.addAttribute("recentContents", recent);
        model.addAttribute("activeNav", "home");
        return "index";
    }

    /**
     * 日记
     */
    @GetMapping("/daily")
    public String daily(Model model) {
        User user = getUser();
        List<Content> diaries = contentService.getPublishedByType(DEFAULT_USER_ID, "DIARY");
        model.addAttribute("user", user);
        model.addAttribute("diaries", diaries);
        model.addAttribute("activeNav", "daily");
        return "daily";
    }

    /**
     * 灵感
     */
    @GetMapping("/inspiration")
    public String inspiration(Model model) {
        User user = getUser();
        List<Content> inspirations = contentService.getPublishedByType(DEFAULT_USER_ID, "INSPIRATION");
        List<Content> quotes = contentService.getPublishedByType(DEFAULT_USER_ID, "QUOTE");
        List<Todo> todos = todoService.getTodos(DEFAULT_USER_ID);
        model.addAttribute("user", user);
        model.addAttribute("inspirations", inspirations);
        model.addAttribute("quotes", quotes);
        model.addAttribute("todos", todos);
        model.addAttribute("activeNav", "inspiration");
        return "inspiration";
    }

    /**
     * 我的
     */
    @GetMapping("/me")
    public String me(Model model) {
        User user = getUser();

        long diaryCount = contentService.getPublishedByType(DEFAULT_USER_ID, "DIARY").size();
        long inspirationCount = contentService.getPublishedByType(DEFAULT_USER_ID, "INSPIRATION").size();
        long quoteCount = contentService.getPublishedByType(DEFAULT_USER_ID, "QUOTE").size();
        long todoCount = todoService.countAll(DEFAULT_USER_ID);
        long favoriteCount = contentService.getPublished(DEFAULT_USER_ID).stream()
                .filter(Content::getFavorite).count();

        model.addAttribute("user", user);
        model.addAttribute("diaryCount", diaryCount);
        model.addAttribute("inspirationCount", inspirationCount);
        model.addAttribute("quoteCount", quoteCount);
        model.addAttribute("todoCount", todoCount);
        model.addAttribute("favoriteCount", favoriteCount);
        model.addAttribute("activeNav", "me");
        return "me";
    }

    /**
     * Phase 1 临时获取默认用户，Phase 2 登录后改为从 Session 获取
     */
    private User getUser() {
        return userRepository.findById(DEFAULT_USER_ID).orElse(null);
    }
}