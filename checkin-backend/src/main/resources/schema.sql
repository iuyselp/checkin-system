-- 打卡签到系统数据库初始化脚本
-- 数据库：checkin
-- 字符集：utf8mb4

CREATE DATABASE IF NOT EXISTS `checkin` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `checkin`;

-- 用户表
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码 (加密)',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像 URL',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 0-禁用 1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 打卡点表
DROP TABLE IF EXISTS `t_check_point`;
CREATE TABLE `t_check_point` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `name` varchar(100) NOT NULL COMMENT '打卡点名称',
  `address` varchar(255) NOT NULL COMMENT '详细地址',
  `latitude` decimal(10,8) NOT NULL COMMENT '纬度',
  `longitude` decimal(11,8) NOT NULL COMMENT '经度',
  `radius` int(11) NOT NULL DEFAULT '100' COMMENT '有效半径 (米)',
  `work_start_time` time DEFAULT NULL COMMENT '上班开始时间',
  `work_end_time` time DEFAULT NULL COMMENT '下班结束时间',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 0-禁用 1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打卡点表';

-- 考勤规则表
DROP TABLE IF EXISTS `t_attend_rule`;
CREATE TABLE `t_attend_rule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `name` varchar(100) NOT NULL COMMENT '规则名称',
  `work_start_time` time NOT NULL COMMENT '上班时间',
  `work_end_time` time NOT NULL COMMENT '下班时间',
  `late_grace_minutes` int(11) DEFAULT '0' COMMENT '迟到容忍分钟数',
  `early_grace_minutes` int(11) DEFAULT '0' COMMENT '早退容忍分钟数',
  `work_days` varchar(20) DEFAULT '1,2,3,4,5' COMMENT '工作日 1-7 表示周一到周日',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 0-禁用 1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤规则表';

-- 打卡记录表
DROP TABLE IF EXISTS `t_check_record`;
CREATE TABLE `t_check_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `point_id` bigint(20) NOT NULL COMMENT '打卡点 ID',
  `check_type` tinyint(4) NOT NULL COMMENT '打卡类型 1-上班 2-下班',
  `check_time` datetime NOT NULL COMMENT '打卡时间',
  `latitude` decimal(10,8) NOT NULL COMMENT '打卡纬度',
  `longitude` decimal(11,8) NOT NULL COMMENT '打卡经度',
  `distance` int(11) DEFAULT NULL COMMENT '与打卡点距离 (米)',
  `status` tinyint(4) NOT NULL COMMENT '状态 1-正常 2-迟到 3-早退 4-缺卡',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_time` (`user_id`, `check_time`),
  KEY `idx_point_time` (`point_id`, `check_time`),
  KEY `idx_check_date` (`check_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打卡记录表';

-- 考勤统计表
DROP TABLE IF EXISTS `t_attend_statistics`;
CREATE TABLE `t_attend_statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `stat_month` varchar(7) NOT NULL COMMENT '统计月份 YYYY-MM',
  `work_days` int(11) DEFAULT '0' COMMENT '应出勤天数',
  `actual_days` int(11) DEFAULT '0' COMMENT '实际出勤天数',
  `late_count` int(11) DEFAULT '0' COMMENT '迟到次数',
  `early_count` int(11) DEFAULT '0' COMMENT '早退次数',
  `absent_count` int(11) DEFAULT '0' COMMENT '缺勤次数',
  `normal_count` int(11) DEFAULT '0' COMMENT '正常次数',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 0-无效 1-有效',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_month` (`user_id`, `stat_month`),
  KEY `idx_month` (`stat_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤统计表';

-- 初始化数据

-- 默认管理员账号（密码：admin123）
INSERT INTO `t_user` (`username`, `password`, `phone`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '13800138000', 1);

-- 默认考勤规则
INSERT INTO `t_attend_rule` (`name`, `work_start_time`, `work_end_time`, `late_grace_minutes`, `early_grace_minutes`, `work_days`, `status`) VALUES
('标准考勤规则', '09:00:00', '18:00:00', 10, 10, '1,2,3,4,5', 1);

-- 默认打卡点（北京中关村）
INSERT INTO `t_check_point` (`name`, `address`, `latitude`, `longitude`, `radius`, `work_start_time`, `work_end_time`, `status`) VALUES
('中关村办公区', '北京市海淀区中关村大街 1 号', 39.982395, 116.317399, 100, '09:00:00', '18:00:00', 1),
('望京办公区', '北京市朝阳区望京东园四区', 40.008485, 116.478769, 100, '09:00:00', '18:00:00', 1);
