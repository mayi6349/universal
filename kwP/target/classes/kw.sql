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

 Date: 06/29/2018 17:36:14 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `city_info`
-- ----------------------------
DROP TABLE IF EXISTS `city_info`;
CREATE TABLE `city_info` (
  `city_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '城市Id',
  `city_name` varchar(25) DEFAULT NULL COMMENT '城市名称',
  `icon_url` varchar(100) DEFAULT '' COMMENT '标志url',
  `background_url` varchar(100) DEFAULT '' COMMENT '背景url',
  `entry_fee` decimal(11,2) DEFAULT '2.00' COMMENT '报名费',
  `reach_step` int(11) DEFAULT '5000' COMMENT '达标步数',
  `del_flg` char(1) DEFAULT '0' COMMENT '删除标志（0:未删除，1:删除）',
  PRIMARY KEY (`city_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='城市赛信息表';

-- ----------------------------
--  Table structure for `game_info_city`
-- ----------------------------
DROP TABLE IF EXISTS `game_info_city`;
CREATE TABLE `game_info_city` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `city_id` int(3) DEFAULT NULL COMMENT '城市Id',
  `city_name` varchar(25) DEFAULT NULL COMMENT '城市名称',
  `phase_no` varchar(25) NOT NULL DEFAULT '' COMMENT '挑战期号',
  `entry_fee` decimal(11,2) DEFAULT '0.00' COMMENT '报名费',
  `reach_step` int(11) DEFAULT NULL COMMENT '达标步数',
  `sum_amount` decimal(11,2) DEFAULT '0.00' COMMENT '总红包金额',
  `sum_count` int(11) DEFAULT '0' COMMENT '报名人数',
  `reach_count` int(11) DEFAULT '0' COMMENT '达标人数',
  `avg_amount` decimal(11,2) DEFAULT '0.00' COMMENT '平均红包金额',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flg` char(1) DEFAULT '0' COMMENT '删除标志（0:未删除，1:删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `phase_no_city_id` (`phase_no`,`city_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='城市赛信息表';

-- ----------------------------
--  Table structure for `game_info_country`
-- ----------------------------
DROP TABLE IF EXISTS `game_info_country`;
CREATE TABLE `game_info_country` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `phase_no` varchar(25) NOT NULL DEFAULT '' COMMENT '挑战期号',
  `entry_fee` decimal(11,2) DEFAULT '0.00' COMMENT '报名费',
  `reach_step` int(11) DEFAULT NULL COMMENT '达标步数',
  `sum_amount` decimal(11,2) DEFAULT '0.00' COMMENT '总红包金额',
  `sum_count` int(11) DEFAULT '0' COMMENT '报名人数',
  `reach_count` int(11) DEFAULT '0' COMMENT '达标人数',
  `avg_amount` decimal(11,2) DEFAULT '0.00' COMMENT '平均红包金额',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flg` char(1) DEFAULT '0' COMMENT '删除标志（0:未删除，1:删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `pNo` (`phase_no`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COMMENT='全国赛信息表';

-- ----------------------------
--  Table structure for `game_info_team`
-- ----------------------------
DROP TABLE IF EXISTS `game_info_team`;
CREATE TABLE `game_info_team` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `user_id` int(11) NOT NULL COMMENT '发起人Id',
  `team_name` varchar(125) DEFAULT '' COMMENT '团名',
  `phase_no` varchar(25) NOT NULL DEFAULT '' COMMENT '挑战期号',
  `start_time` varchar(25) DEFAULT NULL COMMENT '开始时间',
  `end_time` varchar(25) DEFAULT NULL COMMENT '结束时间',
  `entry_fee` decimal(11,2) DEFAULT '0.00' COMMENT '报名费',
  `reach_step` int(11) DEFAULT NULL COMMENT '达标步数',
  `team_count` int(3) DEFAULT '3' COMMENT '最多参赛人数',
  `sum_amount` decimal(11,2) DEFAULT '0.00' COMMENT '总红包金额',
  `sum_count` int(11) DEFAULT '0' COMMENT '报名人数',
  `reach_count` int(11) DEFAULT '0' COMMENT '达标人数',
  `avg_amount` decimal(11,2) DEFAULT '0.00' COMMENT '平均红包金额',
  `status` char(1) DEFAULT '0' COMMENT '状态（0:开启报名、1:进行中、2:已结束）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flg` char(1) DEFAULT '0' COMMENT '删除标志（0:未删除，1:删除）',
  PRIMARY KEY (`id`),
  KEY `phaseNo` (`phase_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团队赛信息表(根据phase_no分表)';

-- ----------------------------
--  Table structure for `group_game_order_city`
-- ----------------------------
DROP TABLE IF EXISTS `group_game_order_city`;
CREATE TABLE `group_game_order_city` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `user_id` int(11) NOT NULL COMMENT '用户Id',
  `nick_name` varchar(50) DEFAULT NULL COMMENT '昵称',
  `icon_url` varchar(125) DEFAULT NULL COMMENT '头像地址',
  `phase_no` varchar(25) NOT NULL DEFAULT '' COMMENT '挑战期号',
  `city_id` int(11) NOT NULL COMMENT '城市Id',
  `city_name` varchar(50) DEFAULT NULL COMMENT '城市名称',
  `group_no` varchar(25) NOT NULL DEFAULT '' COMMENT '小组期号',
  `order_id` varchar(64) NOT NULL DEFAULT '' COMMENT '挑战订单Id',
  `entry_fee` decimal(11,2) DEFAULT '0.00' COMMENT '报名费',
  `pay_type` char(1) DEFAULT '0' COMMENT '支付类型（0:余额，1:微信，2:支付宝）',
  `pay_status` char(1) DEFAULT '0' COMMENT '支付状态（0:未支付，1:已支付）',
  `pay_time` varchar(64) DEFAULT '' COMMENT '支付时间',
  `trade_no` varchar(64) DEFAULT '' COMMENT '交易流水号',
  `walk_step` int(11) DEFAULT '0' COMMENT '步数',
  `reach_step` int(11) DEFAULT NULL COMMENT '达标步数',
  `amount` decimal(11,2) DEFAULT '0.00' COMMENT '红包金额',
  `status` char(1) DEFAULT '0' COMMENT '挑战状态（0:未达标、1:已达标）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `pcgNo` (`phase_no`,`city_id`,`group_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='城市赛参赛信息表(根据phase_no+city_id+group_no分表)';

-- ----------------------------
--  Table structure for `group_game_order_country`
-- ----------------------------
DROP TABLE IF EXISTS `group_game_order_country`;
CREATE TABLE `group_game_order_country` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `user_id` int(11) NOT NULL COMMENT '用户Id',
  `nick_name` varchar(50) DEFAULT NULL COMMENT '昵称',
  `icon_url` varchar(125) DEFAULT NULL COMMENT '头像地址',
  `phase_no` varchar(25) NOT NULL DEFAULT '' COMMENT '挑战期号',
  `group_no` varchar(25) NOT NULL DEFAULT '' COMMENT '小组期号',
  `order_id` varchar(64) NOT NULL DEFAULT '' COMMENT '挑战订单Id',
  `entry_fee` decimal(11,2) DEFAULT '0.00' COMMENT '报名费',
  `pay_type` char(1) DEFAULT '0' COMMENT '支付类型（0:余额，1:微信，2:支付宝）',
  `pay_status` char(1) DEFAULT '0' COMMENT '支付状态（0:未支付，1:已支付）',
  `pay_time` varchar(64) DEFAULT '' COMMENT '支付时间',
  `trade_no` varchar(64) DEFAULT '' COMMENT '交易流水号',
  `walk_step` int(11) DEFAULT '0' COMMENT '步数',
  `reach_step` int(11) DEFAULT NULL COMMENT '达标步数',
  `amount` decimal(11,2) DEFAULT '0.00' COMMENT '红包金额',
  `status` char(1) DEFAULT '0' COMMENT '挑战状态（0:未达标、1:已达标）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `pgNo` (`phase_no`,`group_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='全国赛参赛信息表(根据phase_no+group_no分表)';

-- ----------------------------
--  Table structure for `group_game_order_team`
-- ----------------------------
DROP TABLE IF EXISTS `group_game_order_team`;
CREATE TABLE `group_game_order_team` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `user_id` int(11) NOT NULL COMMENT '用户Id',
  `nick_name` varchar(50) DEFAULT NULL COMMENT '昵称',
  `icon_url` varchar(125) DEFAULT NULL COMMENT '头像地址',
  `phase_no` varchar(25) NOT NULL DEFAULT '' COMMENT '挑战期号',
  `team_id` int(11) NOT NULL COMMENT '团Id（group_game_info_team.id）',
  `team_name` varchar(50) DEFAULT NULL COMMENT '团名称',
  `group_no` varchar(25) NOT NULL DEFAULT '' COMMENT '小组期号',
  `order_id` varchar(64) NOT NULL DEFAULT '' COMMENT '挑战订单Id',
  `entry_fee` decimal(11,2) DEFAULT '0.00' COMMENT '报名费',
  `pay_type` char(1) DEFAULT '0' COMMENT '支付类型（0:余额，1:微信，2:支付宝）',
  `pay_status` char(1) DEFAULT '0' COMMENT '支付状态（0:未支付，1:已支付）',
  `pay_time` varchar(64) DEFAULT '' COMMENT '支付时间',
  `trade_no` varchar(64) DEFAULT '' COMMENT '交易流水号',
  `walk_step` int(11) DEFAULT '0' COMMENT '步数',
  `reach_step` int(11) DEFAULT NULL COMMENT '达标步数',
  `amount` decimal(11,2) DEFAULT '0.00' COMMENT '红包金额',
  `status` char(1) DEFAULT '0' COMMENT '挑战状态（0:未达标、1:已达标）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `utpNo` (`phase_no`,`team_id`,`group_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团队赛参赛信息表(根据phase_no+team_id+group_no分表)';

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
  `pay_country_count` int(6) DEFAULT '0' COMMENT '全国赛支付次数（参赛次数）',
  `pay_country_amount` decimal(11,2) DEFAULT '0.00' COMMENT '全国赛支付总额（参赛总额）',
  `pay_city_count` int(6) DEFAULT '0' COMMENT '城市赛支付次数（参赛次数）',
  `pay_city_amount` decimal(11,2) DEFAULT '0.00' COMMENT '城市赛支付总额（参赛总额）',
  `pay_team_count` int(6) DEFAULT '0' COMMENT '团队赛支付次数（参赛次数）',
  `pay_team_amount` decimal(11,2) DEFAULT '0.00' COMMENT '团队赛支付总额（参赛总额）',
  `sum_income_count` int(6) DEFAULT '0' COMMENT '收入次数（达标次数）',
  `sum_income_amount` decimal(11,2) DEFAULT '0.00' COMMENT '收入总额（达标总额）',
  `income_country_count` int(6) DEFAULT '0' COMMENT '全国赛收入次数（达标次数）',
  `income_country_amount` decimal(11,2) DEFAULT '0.00' COMMENT '全国赛收入总额（达标总额）',
  `income_city_count` int(6) DEFAULT '0' COMMENT '城市赛收入次数（达标次数）',
  `income_city_amount` decimal(11,2) DEFAULT '0.00' COMMENT '城市赛收入总额（达标总额）',
  `income_team_count` int(6) DEFAULT '0' COMMENT '团队赛收入次数（达标次数）',
  `income_team_amount` decimal(11,2) DEFAULT '0.00' COMMENT '团队赛收入总额（达标总额）',
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
  `io_type` char(1) DEFAULT '0' COMMENT '类型（0:支出，1:收入，2:提现）',
  `pay_type` char(1) DEFAULT '0' COMMENT '支付类型（0:余额，1:微信，2:支付宝）',
  `game_type` char(1) DEFAULT '0' COMMENT '支付类型（0:全国，1:城市，2:团队）',
  `phase_no` varchar(25) NOT NULL DEFAULT '' COMMENT '挑战期号',
  `city_name` varchar(25) DEFAULT NULL COMMENT '城市名称',
  `title` varchar(125) DEFAULT '' COMMENT '标题',
  `order_id` varchar(25) DEFAULT '' COMMENT '订单Id',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `userId` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户账户流水表(根据user_id分表)';

-- ----------------------------
--  Table structure for `user_game_order_city`
-- ----------------------------
DROP TABLE IF EXISTS `user_game_order_city`;
CREATE TABLE `user_game_order_city` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `user_id` int(11) NOT NULL COMMENT '用户Id',
  `nick_name` varchar(50) DEFAULT NULL COMMENT '昵称',
  `icon_url` varchar(125) DEFAULT NULL COMMENT '头像地址',
  `phase_no` varchar(25) NOT NULL DEFAULT '' COMMENT '挑战期号',
  `city_id` int(11) NOT NULL COMMENT '城市Id',
  `city_name` varchar(50) DEFAULT NULL COMMENT '城市名称',
  `group_no` varchar(25) NOT NULL DEFAULT '' COMMENT '小组期号',
  `order_id` varchar(64) NOT NULL DEFAULT '' COMMENT '挑战订单Id',
  `entry_fee` decimal(11,2) DEFAULT '0.00' COMMENT '报名费',
  `pay_type` char(1) DEFAULT '0' COMMENT '支付类型（0:余额，1:微信，2:支付宝）',
  `pay_status` char(1) DEFAULT '0' COMMENT '支付状态（0:未支付，1:已支付）',
  `pay_time` varchar(64) DEFAULT '' COMMENT '支付时间',
  `trade_no` varchar(64) DEFAULT '' COMMENT '交易流水号',
  `walk_step` int(11) DEFAULT '0' COMMENT '步数',
  `reach_step` int(11) DEFAULT NULL COMMENT '达标步数',
  `amount` decimal(11,2) DEFAULT '0.00' COMMENT '红包金额',
  `status` char(1) DEFAULT '2' COMMENT '挑战状态（0:未达标、1:已达标、2:未同步）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `upNo` (`user_id`,`phase_no`) USING BTREE,
  KEY `order_id` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='城市赛参赛信息表(根据user_id分表)';

-- ----------------------------
--  Table structure for `user_game_order_country`
-- ----------------------------
DROP TABLE IF EXISTS `user_game_order_country`;
CREATE TABLE `user_game_order_country` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `user_id` int(11) NOT NULL COMMENT '用户Id',
  `nick_name` varchar(50) DEFAULT NULL COMMENT '昵称',
  `icon_url` varchar(125) DEFAULT NULL COMMENT '头像地址',
  `phase_no` varchar(25) NOT NULL DEFAULT '' COMMENT '挑战期号',
  `group_no` varchar(25) NOT NULL DEFAULT '' COMMENT '小组期号',
  `order_id` varchar(64) NOT NULL DEFAULT '' COMMENT '挑战订单Id',
  `entry_fee` decimal(11,2) DEFAULT '0.00' COMMENT '报名费',
  `pay_type` char(1) DEFAULT '0' COMMENT '支付类型（0:余额，1:微信，2:支付宝）',
  `pay_status` char(1) DEFAULT '0' COMMENT '支付状态（0:未支付，1:已支付）',
  `pay_time` varchar(64) DEFAULT '' COMMENT '支付时间',
  `trade_no` varchar(64) DEFAULT '' COMMENT '交易流水号',
  `walk_step` int(11) DEFAULT '0' COMMENT '步数',
  `reach_step` int(11) DEFAULT NULL COMMENT '达标步数',
  `amount` decimal(11,2) DEFAULT '0.00' COMMENT '红包金额',
  `status` char(1) DEFAULT '2' COMMENT '挑战状态（0:未达标、1:已达标、2:未同步）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `upNo` (`user_id`,`phase_no`) USING BTREE,
  KEY `order_id` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='全国赛参赛信息表(根据user_id分表)';

-- ----------------------------
--  Table structure for `user_game_order_team`
-- ----------------------------
DROP TABLE IF EXISTS `user_game_order_team`;
CREATE TABLE `user_game_order_team` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `user_id` int(11) NOT NULL COMMENT '用户Id',
  `nick_name` varchar(50) DEFAULT NULL COMMENT '昵称',
  `icon_url` varchar(125) DEFAULT NULL COMMENT '头像地址',
  `phase_no` varchar(25) NOT NULL DEFAULT '' COMMENT '挑战期号',
  `team_id` int(11) NOT NULL COMMENT '团Id（group_game_info_team.id）',
  `team_name` varchar(50) DEFAULT NULL COMMENT '团名称',
  `group_no` varchar(25) NOT NULL DEFAULT '' COMMENT '小组期号',
  `order_id` varchar(64) NOT NULL DEFAULT '' COMMENT '挑战订单Id',
  `entry_fee` decimal(11,2) DEFAULT '0.00' COMMENT '报名费',
  `pay_type` char(1) DEFAULT '0' COMMENT '支付类型（0:余额，1:微信，2:支付宝）',
  `pay_status` char(1) DEFAULT '0' COMMENT '支付状态（0:未支付，1:已支付）',
  `pay_time` varchar(64) DEFAULT '' COMMENT '支付时间',
  `trade_no` varchar(64) DEFAULT '' COMMENT '交易流水号',
  `walk_step` int(11) DEFAULT '0' COMMENT '步数',
  `reach_step` int(11) DEFAULT NULL COMMENT '达标步数',
  `amount` decimal(11,2) DEFAULT '0.00' COMMENT '红包金额',
  `status` char(1) DEFAULT '2' COMMENT '挑战状态（0:未达标、1:已达标、2:未同步）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `upNo` (`user_id`,`phase_no`) USING BTREE,
  KEY `order_id` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团队赛参赛信息表(根据user_id分表)';

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
  `sex` int(1) DEFAULT 0 COMMENT '性别',
  `icon_url` varchar(125) DEFAULT NULL COMMENT '头像地址',
  `mobile` varchar(11) DEFAULT NULL COMMENT '手机号',
  `enroll_count` int(3) DEFAULT 0 COMMENT '报名数量',
  `share_count` int(3) DEFAULT 0 COMMENT '已分享获取的报名数量',
  `max_share_count` int(3) DEFAULT 0 COMMENT '分享能够获取的最大报名数量',
  `max_enroll_count` int(2) DEFAULT 1 COMMENT '最大报名数量（share_count+关联用户enroll_count）',
  `from_user_id` int(11) DEFAULT NULL COMMENT '推荐人',
  `last_login_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '最近登陆时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flg` char(50) DEFAULT '0' COMMENT '是否已冻结（0:正常，1:已冻结）',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `wxopenid` (`wx_open_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户基本信息表';

-- ----------------------------
--  Table structure for `game_info`
-- ----------------------------
DROP TABLE IF EXISTS `game_info`;
CREATE TABLE `game_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `type` char(1) DEFAULT '0' COMMENT '类型（0:系统、1:用户）',
  `user_id` int(11) NOT NULL COMMENT '发起人Id',
  `nick_name` varchar(50) DEFAULT NULL COMMENT '昵称',
  `icon_url` varchar(125) DEFAULT NULL COMMENT '头像地址',
  `titile` varchar(125) DEFAULT '' COMMENT '标题',
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
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flg` char(1) DEFAULT '0' COMMENT '删除标志（0:未删除，1:删除）',
  PRIMARY KEY (`id`),
  KEY `phaseNo` (`phase_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='挑战信息表(根据phase_no分表)';

-- ----------------------------
--  Table structure for `user_game_order`
-- ----------------------------
DROP TABLE IF EXISTS `user_game_order`;
CREATE TABLE `user_game_order` (
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
  `status` char(1) DEFAULT '2' COMMENT '挑战状态（0:未达标、1:已达标、2:未同步）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `upNo` (`user_id`,`phase_no`) USING BTREE,
  KEY `order_id` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户挑战参赛信息表(根据user_id分表)';

-- ----------------------------
--  Table structure for `group_game_order`
-- ----------------------------
DROP TABLE IF EXISTS `group_game_order`;
CREATE TABLE `group_game_order` (
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
  `status` char(1) DEFAULT '2' COMMENT '挑战状态（0:未达标、1:已达标、2:未同步）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `upNo` (`phase_no`,`game_id`,`group_no`) USING BTREE,
  KEY `order_id` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户小组挑战参赛信息表(根据phase_no+game_id+group_no分表)';

SET FOREIGN_KEY_CHECKS = 1;
