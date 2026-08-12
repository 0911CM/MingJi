/* ============================================================
   MingJi 铭记 · 全局脚本
   ============================================================ */

(function () {
    'use strict';

    /* ---------- 主题 ---------- */
    function initTheme() {
        const savedTheme = localStorage.getItem('mingji-theme') || 'light';
        document.documentElement.setAttribute('data-theme', savedTheme);
    }

    /* ---------- 首页：发布功能 ---------- */
    function initComposer() {
        const textarea = document.querySelector('.composer-textarea');
        if (!textarea) return;

        const picker = document.getElementById('categoryPicker');
        const publishBtn = document.getElementById('btnPublish');
        const draftBtn = document.getElementById('btnDraft');
        const categoryBtns = picker ? picker.querySelectorAll('.category-btn') : [];

        // 分类选择
        categoryBtns.forEach(btn => {
            btn.addEventListener('click', function () {
                categoryBtns.forEach(b => b.classList.remove('selected'));
                this.classList.add('selected');
            });
        });

        // 提取标题（第一行前 30 字）
        function extractTitle(text) {
            const firstLine = text.split('\n')[0].trim();
            return firstLine.length > 30 ? firstLine.substring(0, 30) + '…' : firstLine;
        }

        // 发送到后端
        function send(action, category, text, callback) {
            const title = extractTitle(text);
            const body = JSON.stringify({
                contentType: category,
                title: title,
                content: text,
                location: '',
                quoteAuthor: '',
                quoteSource: ''
            });

            fetch('/api/content/' + action, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json; charset=utf-8' },
                body: body
            })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    alert(data.message);
                    if (textarea) textarea.value = '';
                    categoryBtns.forEach(b => b.classList.remove('selected'));
                    if (callback) callback();
                } else {
                    alert('保存失败：' + (data.message || '未知错误'));
                }
            })
            .catch(err => {
                alert('网络错误：' + err.message);
            });
        }

        // 发布
        if (publishBtn) {
            publishBtn.addEventListener('click', function () {
                const text = textarea.value.trim();
                if (!text) {
                    alert('写点内容再发布吧');
                    return;
                }
                const selected = picker.querySelector('.category-btn.selected');
                if (!selected) {
                    alert('请选择分类：日记 / 灵感 / 待办 / 名句');
                    return;
                }
                const category = selected.dataset.type;
                send('publish', category, text, function () {
                    // 记住当前分类，刷新后回到对应页面
                    localStorage.setItem('mingji-last-category', category);
                    window.location.reload();
                });
            });
        }

        // 保存草稿
        if (draftBtn) {
            draftBtn.addEventListener('click', function () {
                const text = textarea.value.trim();
                if (!text) {
                    alert('写点内容再保存草稿吧');
                    return;
                }
                const selected = picker.querySelector('.category-btn.selected');
                const category = selected ? selected.dataset.type : 'DIARY';
                send('draft', category, text);
            });
        }
    }

    /* ---------- 灵感页：分类标签切换 + 记住当前 tab ---------- */
    function initInspirationTabs() {
        const tabs = document.querySelector('.inspiration-tabs');
        if (!tabs) return;

        const tabBtns = tabs.querySelectorAll('.tab-btn');
        const panels = document.querySelectorAll('.tab-panel');

        function switchTab(tab) {
            tabBtns.forEach(b => b.classList.remove('active'));
            panels.forEach(p => p.classList.remove('active'));

            const btn = tabs.querySelector('[data-tab="' + tab + '"]');
            const panel = document.querySelector('[data-panel="' + tab + '"]');
            if (btn) btn.classList.add('active');
            if (panel) panel.classList.add('active');

            // 记住当前 tab
            localStorage.setItem('mingji-inspiration-tab', tab);
        }

        // 从 localStorage 恢复上次的 tab
        const savedTab = localStorage.getItem('mingji-inspiration-tab');
        if (savedTab && tabs.querySelector('[data-tab="' + savedTab + '"]')) {
            switchTab(savedTab);
        }

        tabBtns.forEach(btn => {
            btn.addEventListener('click', function () {
                switchTab(this.dataset.tab);
            });
        });
    }

    /* ---------- 灵感页：待办增删改 ---------- */
    function initTodos() {
        const todoInput = document.getElementById('todoInput');
        const todoAddBtn = document.getElementById('todoAddBtn');
        if (!todoInput || !todoAddBtn) return;

        // 添加待办
        function addTodo() {
            const text = todoInput.value.trim();
            if (!text) {
                alert('输入待办内容');
                return;
            }
            fetch('/api/todos', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json; charset=utf-8' },
                body: JSON.stringify({ title: text, priority: 'NORMAL' })
            })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    // 记住停留在待办 tab
                    localStorage.setItem('mingji-inspiration-tab', 'todos');
                    alert('待办已添加');
                    window.location.reload();
                } else {
                    alert('添加失败：' + (data.message || '未知错误'));
                }
            })
            .catch(err => alert('网络错误：' + err.message));
        }

        todoAddBtn.addEventListener('click', addTodo);
        todoInput.addEventListener('keydown', function (e) {
            if (e.key === 'Enter') addTodo();
        });

        // 切换完成
        document.querySelectorAll('.todo-check').forEach(btn => {
            btn.addEventListener('click', function () {
                const id = this.dataset.id;
                localStorage.setItem('mingji-inspiration-tab', 'todos');
                fetch('/api/todos/' + id + '/toggle', { method: 'PUT' })
                    .then(res => res.json())
                    .then(data => {
                        if (data.success) window.location.reload();
                    })
                    .catch(err => alert('网络错误：' + err.message));
            });
        });

        // 删除
        document.querySelectorAll('.todo-delete').forEach(btn => {
            btn.addEventListener('click', function () {
                const id = this.dataset.id;
                if (!confirm('确定删除这条待办？')) return;
                localStorage.setItem('mingji-inspiration-tab', 'todos');
                fetch('/api/todos/' + id, { method: 'DELETE' })
                    .then(res => res.json())
                    .then(data => {
                        if (data.success) window.location.reload();
                    })
                    .catch(err => alert('网络错误：' + err.message));
            });
        });
    }

    /* ---------- 初始化 ---------- */
    document.addEventListener('DOMContentLoaded', function () {
        initTheme();
        initComposer();
        initInspirationTabs();
        initTodos();
    });
})();