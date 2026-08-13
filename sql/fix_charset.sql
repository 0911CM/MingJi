-- ============================================================
-- MingJi · 修复数据库中文乱码
-- 使用 utf8mb4 重新写入正确的用户数据
-- ============================================================

USE mingji;

SET NAMES utf8mb4;

UPDATE `user` SET
  real_name = NULL,
  nickname = 'MingJi',
  school = NULL,
  signature = '记录生活，也记录自己。'
WHERE username = 'mingji';

SELECT id, username, nickname, real_name, school, signature FROM `user`;