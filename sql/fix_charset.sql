-- ============================================================
-- MingJi · 修复数据库中文乱码
-- 使用 utf8mb4 重新写入正确的用户数据
-- ============================================================

USE mingji;

SET NAMES utf8mb4;

UPDATE `user` SET
  real_name = '刘佳诚',
  nickname = '诚铭',
  school = '湖南商务职业技术学院',
  signature = '记录生活，也记录自己。'
WHERE username = 'mingji';

SELECT id, username, nickname, real_name, school, signature FROM `user`;