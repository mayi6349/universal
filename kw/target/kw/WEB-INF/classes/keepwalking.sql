/*
 Navicat Premium Data Transfer

 Source Server         : inpark
 Source Server Type    : MySQL
 Source Server Version : 50715
 Source Host           : localhost
 Source Database       : keepwalking

 Target Server Type    : MySQL
 Target Server Version : 50715
 File Encoding         : utf-8

 Date: 08/07/2018 15:25:34 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `game_info`
-- ----------------------------
DROP TABLE IF EXISTS `game_info`;
CREATE TABLE `game_info` (
  `game_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `type` char(1) DEFAULT '0' COMMENT '类型（0:用户、1:系统）',
  `user_id` int(11) NOT NULL COMMENT '发起人Id',
  `nick_name` varchar(50) DEFAULT NULL COMMENT '昵称',
  `icon_url` varchar(125) DEFAULT NULL COMMENT '头像地址',
  `title` varchar(125) DEFAULT '' COMMENT '标题',
  `phase_no` varchar(25) NOT NULL DEFAULT '' COMMENT '挑战期号',
  `start_time` varchar(25) DEFAULT NULL COMMENT '开始时间',
  `end_time` varchar(25) DEFAULT NULL COMMENT '结束时间',
  `entry_fee` decimal(11,2) DEFAULT '0.00' COMMENT '报名费',
  `reach_step` int(11) DEFAULT NULL COMMENT '达标步数',
  `entry_amount` decimal(11,2) DEFAULT '0.00' COMMENT '总红包金额',
  `entry_count` int(11) DEFAULT '0' COMMENT '报名人数',
  `reach_count` int(11) DEFAULT '0' COMMENT '达标人数',
  `avg_amount` decimal(11,2) DEFAULT '0.00' COMMENT '平均红包金额',
  `status` char(1) DEFAULT '0' COMMENT '状态（0:开启报名、1:进行中、2:已结束）',
  `is_open` char(1) DEFAULT '0' COMMENT '参赛权限（0:公开、1:自主邀请）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flg` char(1) DEFAULT '0' COMMENT '删除标志（0:未删除，1:删除）',
  PRIMARY KEY (`game_id`),
  KEY `phaseNo` (`phase_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='挑战信息表(根据phase_no分表)';

-- ----------------------------
--  Table structure for `game_order`
-- ----------------------------
DROP TABLE IF EXISTS `game_order`;
CREATE TABLE `game_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `user_id` int(11) NOT NULL COMMENT '用户Id',
  `nick_name` varchar(50) DEFAULT NULL COMMENT '昵称',
  `icon_url` varchar(125) DEFAULT NULL COMMENT '头像地址',
  `order_id` varchar(64) NOT NULL DEFAULT '' COMMENT '挑战订单Id',
  `game_id` int(11) NOT NULL COMMENT '挑战Id',
  `phase_no` varchar(25) NOT NULL DEFAULT '' COMMENT '挑战期号',
  `title` varchar(50) DEFAULT NULL COMMENT '标题',
  `group_no` varchar(25) NOT NULL DEFAULT '' COMMENT '小组期号',
  `entry_fee` decimal(11,2) DEFAULT '0.00' COMMENT '报名费',
  `pay_type` char(1) DEFAULT '0' COMMENT '支付类型（0:余额，1:微信，2:支付宝）',
  `pay_status` char(1) DEFAULT '0' COMMENT '支付状态（0:未支付，1:已支付）',
  `pay_time` varchar(64) DEFAULT '' COMMENT '支付时间',
  `trade_no` varchar(64) DEFAULT '' COMMENT '交易流水号',
  `walk_step` int(11) DEFAULT '0' COMMENT '步数',
  `reach_step` int(11) DEFAULT NULL COMMENT '达标步数',
  `amount` decimal(11,2) DEFAULT '0.00' COMMENT '红包金额',
  `status` char(1) DEFAULT '0' COMMENT '挑战状态（0:未达标、1:已达标、2:未同步）',
  `type` char(1) DEFAULT '0' COMMENT '类型（0:普通、1:特殊）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `upNo` (`user_id`,`phase_no`,`game_id`,`group_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户挑战参赛信息表(根据user_id分表)';

-- ----------------------------
--  Table structure for `sys_account`
-- ----------------------------
DROP TABLE IF EXISTS `sys_account`;
CREATE TABLE `sys_account` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `balance` decimal(11,2) DEFAULT '0.00' COMMENT '总余额',
  `usable_balance` decimal(11,2) DEFAULT '0.00' COMMENT '可用余额',
  `frozen_balance` decimal(11,2) DEFAULT '0.00' COMMENT '冻结余额',
  `sum_pay_count` int(6) DEFAULT '0' COMMENT '支付次数（参赛次数）',
  `sum_pay_amount` decimal(11,2) DEFAULT '0.00' COMMENT '支付总额（参赛总额）',
  `sum_income_count` int(6) DEFAULT '0' COMMENT '收入次数（达标次数）',
  `sum_income_amount` decimal(11,2) DEFAULT '0.00' COMMENT '收入总额（达标总额）',
  `sum_withdraw_count` int(6) DEFAULT '0' COMMENT '提现次数',
  `sum_withdraw_amount` decimal(11,2) DEFAULT '0.00' COMMENT '提现金额',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户账户信息表';

-- ----------------------------
--  Table structure for `user_account`
-- ----------------------------
DROP TABLE IF EXISTS `user_account`;
CREATE TABLE `user_account` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `user_id` int(11) NOT NULL COMMENT '用户Id',
  `from_user_id` int(11) NOT NULL COMMENT '来自用户Id',
  `usable_balance` decimal(11,2) DEFAULT '0.00' COMMENT '可用余额',
  `frozen_balance` decimal(11,2) DEFAULT '0.00' COMMENT '冻结余额',
  `sum_pay_count` int(6) DEFAULT '0' COMMENT '支付次数（参赛次数）',
  `sum_pay_amount` decimal(11,2) DEFAULT '0.00' COMMENT '支付总额（参赛总额）',
  `sum_income_count` int(6) DEFAULT '0' COMMENT '收入次数（达标次数）',
  `sum_income_amount` decimal(11,2) DEFAULT '0.00' COMMENT '收入总额（达标总额）',
  `sum_withdraw_count` int(6) DEFAULT '0' COMMENT '提现次数',
  `sum_withdraw_amount` decimal(11,2) DEFAULT '0.00' COMMENT '提现金额',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `userId` (`user_id`) USING BTREE,
  KEY `fromUserId` (`from_user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户账户信息表';

-- ----------------------------
--  Table structure for `user_account_stream`
-- ----------------------------
DROP TABLE IF EXISTS `user_account_stream`;
CREATE TABLE `user_account_stream` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `user_id` int(11) NOT NULL COMMENT '用户Id',
  `amount` decimal(11,2) DEFAULT '0.00' COMMENT '金额',
  `io_type` char(1) DEFAULT '0' COMMENT '类型（0:参赛报名费，1:达标奖励金，2:奖励金提现）',
  `pay_type` char(1) DEFAULT '0' COMMENT '支付类型（0:余额，1:微信，2:支付宝）',
  `phase_no` varchar(25) NOT NULL DEFAULT '' COMMENT '挑战期号',
  `title` varchar(125) DEFAULT '' COMMENT '标题',
  `order_id` varchar(25) DEFAULT '' COMMENT '订单Id',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `userId` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户账户流水表(根据user_id分表)';

-- ----------------------------
--  Table structure for `user_info`
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户Id',
  `wx_open_id` varchar(125) NOT NULL DEFAULT '' COMMENT '微信唯一识别号',
  `wx_auth_status` char(1) DEFAULT '0' COMMENT '微信授权信息（0:未授权，1:已授权）',
  `wx_follow_status` char(1) DEFAULT '0' COMMENT '微信公众号关注状态（0:未关注，1:已关注）',
  `nick_name` varchar(50) DEFAULT NULL COMMENT '昵称',
  `sex` int(1) DEFAULT '0' COMMENT '性别',
  `icon_url` varchar(125) DEFAULT NULL COMMENT '头像地址',
  `mobile` varchar(11) DEFAULT NULL COMMENT '手机号',
  `base_enroll_count` int(3) DEFAULT '0' COMMENT '基础报名次数',
  `help_enroll_count` int(3) DEFAULT '0' COMMENT '助力报名次数',
  `help_user_count` int(11) DEFAULT NULL COMMENT '助力小伙伴人数',
  `from_user_id` int(11) DEFAULT NULL COMMENT '推荐人',
  `last_login_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '最近登陆时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flg` char(50) DEFAULT '0' COMMENT '是否已冻结（0:正常，1:已冻结）',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `wxopenid` (`wx_open_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户基本信息表';

-- ----------------------------
--  Table structure for `activity_info`
-- ----------------------------
DROP TABLE IF EXISTS `activity_info`;
CREATE TABLE `activity_info` (
  `activity_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '活动Id',
  `nick_name` varchar(50) DEFAULT NULL COMMENT '赞助商名称',
  `icon_url` varchar(125) DEFAULT NULL COMMENT '赞助商头像',
  `address` varchar(125) DEFAULT '' COMMENT '赞助商地址',
  `title` varchar(125) DEFAULT '' COMMENT '标题',
  `type` char(1) DEFAULT '0' COMMENT '类型（0:到点开、1:满开）',
  `start_time` varchar(25) DEFAULT NULL COMMENT '开始时间',
  `end_time` varchar(25) DEFAULT NULL COMMENT '结束时间',
  `entry_count` int(11) DEFAULT '0' COMMENT '报名人数',
  `reach_count` int(11) DEFAULT '0' COMMENT '开奖人数',
  `status` char(1) DEFAULT '0' COMMENT '状态（0:筹备中、1:进行中、2:已结束）',
  `hot_level` char(1) DEFAULT '0' COMMENT '热度',
  `img_1` varchar(125) DEFAULT '' COMMENT '图片1',
  `img_2` varchar(125) DEFAULT '' COMMENT '图片2',
  `img_3` varchar(125) DEFAULT '' COMMENT '图片3',
  `img_4` varchar(125) DEFAULT '' COMMENT '图片4',
  `img_5` varchar(125) DEFAULT '' COMMENT '图片5',
  `img_6` varchar(125) DEFAULT '' COMMENT '图片6',
  `img_7` varchar(125) DEFAULT '' COMMENT '图片7',
  `img_8` varchar(125) DEFAULT '' COMMENT '图片8',
  `img_9` varchar(125) DEFAULT '' COMMENT '图片9',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flg` char(1) DEFAULT '0' COMMENT '是否已冻结（0:正常，1:已冻结）',
  PRIMARY KEY (`activity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动表';

-- ----------------------------
--  Table structure for `activity_order`
-- ----------------------------
DROP TABLE IF EXISTS `activity_order`;
CREATE TABLE `activity_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `user_id` int(11) NOT NULL COMMENT '用户Id',
  `nick_name` varchar(50) DEFAULT NULL COMMENT '昵称',
  `icon_url` varchar(125) DEFAULT NULL COMMENT '头像地址',
  `order_id` varchar(64) NOT NULL DEFAULT '' COMMENT '挑战订单Id',
  `activity_id` int(11) NOT NULL COMMENT '活动Id',
  `title` varchar(50) DEFAULT NULL COMMENT '标题',
  `zz_nick_name` varchar(50) DEFAULT NULL COMMENT '赞助商名称',  
  `img_1` varchar(125) DEFAULT '' COMMENT '图片1',
  `entry_count` int(11) DEFAULT '0' COMMENT '报名人数',
  `status` char(1) DEFAULT '2' COMMENT '挑战状态（0:待开奖、1:中奖了、2:已结束）',
  `type` char(1) DEFAULT NULL COMMENT '类型（0:普通、1:特殊）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `upNo` (`user_id`,`activity_id`,`status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动报名信息表';

SET FOREIGN_KEY_CHECKS = 1;
