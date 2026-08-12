package com.mingji.controller;

import com.mingji.entity.Todo;
import com.mingji.service.TodoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 待办事项 API 控制器
 */
@RestController
@RequestMapping("/api/todos")
public class TodoApiController {

    private static final Long DEFAULT_USER_ID = 1L;

    private final TodoService todoService;

    public TodoApiController(TodoService todoService) {
        this.todoService = todoService;
    }

    /** 查询全部待办 */
    @GetMapping
    public ResponseEntity<List<Todo>> list() {
        return ResponseEntity.ok(todoService.getTodos(DEFAULT_USER_ID));
    }

    /** 新建待办 */
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, String> body) {
        String title = body.get("title");
        String priority = body.get("priority");

        Map<String, Object> result = new HashMap<>();
        if (title == null || title.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "待办内容不能为空");
            return ResponseEntity.badRequest().body(result);
        }

        Todo todo = todoService.create(DEFAULT_USER_ID, title.trim(), priority);
        result.put("success", true);
        result.put("message", "待办已添加");
        result.put("id", todo.getId());
        return ResponseEntity.ok(result);
    }

    /** 切换完成状态 */
    @PutMapping("/{id}/toggle")
    public ResponseEntity<Map<String, Object>> toggle(@PathVariable Long id) {
        Todo todo = todoService.toggleDone(DEFAULT_USER_ID, id);
        Map<String, Object> result = new HashMap<>();
        if (todo == null) {
            result.put("success", false);
            result.put("message", "待办不存在");
            return ResponseEntity.notFound().build();
        }
        result.put("success", true);
        result.put("done", todo.getDone());
        return ResponseEntity.ok(result);
    }

    /** 删除待办 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        boolean deleted = todoService.delete(DEFAULT_USER_ID, id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", deleted);
        result.put("message", deleted ? "已删除" : "待办不存在");
        return deleted ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }
}