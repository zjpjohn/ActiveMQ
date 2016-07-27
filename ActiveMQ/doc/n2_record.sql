/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50710
Source Host           : localhost:3306
Source Database       : mq_test2

Target Server Type    : MYSQL
Target Server Version : 50710
File Encoding         : 65001

Date: 2016-07-27 14:49:01
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for n2_record
-- ----------------------------
DROP TABLE IF EXISTS `n2_record`;
CREATE TABLE `n2_record` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `business_mark` varchar(127) COLLATE utf8mb4_bin NOT NULL COMMENT '业务标识',
  `time_stamp` bigint(25) unsigned NOT NULL DEFAULT '0' COMMENT '时间戳',
  `dest_name` varchar(127) COLLATE utf8mb4_bin NOT NULL COMMENT '队列名称',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_business_mark` (`business_mark`,`dest_name`) USING BTREE COMMENT '业务标识唯一'
) ENGINE=InnoDB AUTO_INCREMENT=202 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
