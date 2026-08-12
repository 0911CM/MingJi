package com.mingji.service;

import com.mingji.entity.Todo;
import com.mingji.repository.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 待办事项服务
 */
@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    /** 查询用户的全部待办 */
    public List<Todo> getTodos(Long userId) {
        return todoRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /** 新建待办 */
    @Transactional
    public Todo create(Long userId, String title, String priority) {
        Todo todo = new Todo();
        todo.setUserId(userId);
        todo.setTitle(title);
        todo.setPriority(priority != null && !priority.isEmpty() ? priority : "NORMAL");
        todo.setDone(false);
        return todoRepository.save(todo);
    }

    /** 切换完成状态 */
    @Transactional
    public Todo toggleDone(Long userId, Long id) {
        return todoRepository.findById(id)
                .filter(t -> t.getUserId().equals(userId))
                .map(t -> {
                    t.setDone(!t.getDone());
                    return todoRepository.save(t);
                })
                .orElse(null);
    }

    /** 删除待办 */
    @Transactional
    public boolean delete(Long userId, Long id) {
        return todoRepository.findById(id)
                .filter(t -> t.getUserId().equals(userId))
                .map(t -> {
                    todoRepository.delete(t);
                    return true;
                })
                .orElse(false);
    }

    /** 统计数量 */
    public long countAll(Long userId) {
        return todoRepository.countByUserIdAndDone(userId, false) + todoRepository.countByUserIdAndDone(userId, true);
    }
}