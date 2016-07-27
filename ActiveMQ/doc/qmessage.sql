/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50710
Source Host           : localhost:3306
Source Database       : mq_test2

Target Server Type    : MYSQL
Target Server Version : 50710
File Encoding         : 65001

Date: 2016-07-27 14:49:14
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for qmessage
-- ----------------------------
DROP TABLE IF EXISTS `qmessage`;
CREATE TABLE `qmessage` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `message_id` varchar(127) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '消息id',
  `business_mark` varchar(127) COLLATE utf8mb4_bin DEFAULT '' COMMENT '业务标识',
  `message_content` text COLLATE utf8mb4_bin NOT NULL COMMENT '消息内容',
  `status` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '消息状态',
  `destination` varchar(127) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '消息目的地',
  `dest_type` tinyint(2) unsigned NOT NULL DEFAULT '0' COMMENT '消息类型',
  `time_stamp` bigint(25) unsigned NOT NULL DEFAULT '0' COMMENT '时间戳',
  `retry` int(11) unsigned NOT NULL DEFAULT '1' COMMENT '重发次数',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_message_id` (`message_id`) USING BTREE COMMENT '消息id唯一'
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
