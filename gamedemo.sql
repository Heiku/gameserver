-- --------------------------------------------------------
-- 主机:                           127.0.0.1
-- 服务器版本:                        8.0.16 - MySQL Community Server - GPL
-- 服务器OS:                        Win64
-- HeidiSQL 版本:                  10.2.0.5599
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for gamedemo
CREATE DATABASE IF NOT EXISTS `gamedemo` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `gamedemo`;

-- Dumping structure for table gamedemo.chat_record
CREATE TABLE IF NOT EXISTS `chat_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `from_role` bigint(20) NOT NULL DEFAULT '0',
  `to_role` bigint(20) NOT NULL DEFAULT '0',
  `content` varchar(50) NOT NULL DEFAULT '0',
  `send_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='聊天记录表';

-- Data exporting was unselected.

-- Dumping structure for table gamedemo.email_goods
CREATE TABLE IF NOT EXISTS `email_goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `eid` bigint(20) NOT NULL DEFAULT '0' COMMENT '邮件id',
  `gid` bigint(20) NOT NULL DEFAULT '0' COMMENT '物品id',
  `num` int(11) NOT NULL DEFAULT '0' COMMENT '物品数量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='邮件中的物品信息';

-- Data exporting was unselected.

-- Dumping structure for table gamedemo.face_trans
CREATE TABLE IF NOT EXISTS `face_trans` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `promoter` bigint(20) NOT NULL COMMENT '申请人',
  `receiver` bigint(20) NOT NULL COMMENT '接收人',
  `goods_id` bigint(20) NOT NULL COMMENT '物品id',
  `num` int(11) NOT NULL DEFAULT '0' COMMENT '交易数量',
  `amount` int(11) NOT NULL DEFAULT '0' COMMENT '交易金额',
  `success` int(11) NOT NULL DEFAULT '0' COMMENT '交易状态：0：未确认，1：确认',
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='面对面交易记录表';

-- Data exporting was unselected.

-- Dumping structure for table gamedemo.guild
CREATE TABLE IF NOT EXISTS `guild` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '公会id',
  `name` varchar(50) NOT NULL DEFAULT '0' COMMENT '公会名称',
  `bulletin` varchar(200) NOT NULL DEFAULT '0' COMMENT '公告',
  `level` int(11) NOT NULL DEFAULT '0' COMMENT '公会等级',
  `num` int(11) NOT NULL DEFAULT '0' COMMENT '公会人数',
  `max_num` int(11) NOT NULL DEFAULT '0' COMMENT '公会最大人数',
  `president` bigint(20) NOT NULL DEFAULT '0' COMMENT '会长',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公会具体信息';

-- Data exporting was unselected.

-- Dumping structure for table gamedemo.guild_apply
CREATE TABLE IF NOT EXISTS `guild_apply` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '申请id',
  `guild_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '公会id',
  `role_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '玩家id',
  `approver` bigint(20) DEFAULT NULL COMMENT '审批人id',
  `process` int(11) NOT NULL DEFAULT '0' COMMENT '进度：0：未审批，1：成功，2：失败',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公会申请信息';

-- Data exporting was unselected.

-- Dumping structure for table gamedemo.guild_goods_record
CREATE TABLE IF NOT EXISTS `guild_goods_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录id',
  `role_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '玩家id',
  `guild_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '公会id',
  `goods_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '物品id',
  `num` int(11) NOT NULL DEFAULT '0' COMMENT '物品数量',
  `type` int(11) NOT NULL DEFAULT '0' COMMENT '类型：1：取出，2：存入',
  `create_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公会物品记录';

-- Data exporting was unselected.

-- Dumping structure for table gamedemo.guild_goods_store
CREATE TABLE IF NOT EXISTS `guild_goods_store` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `guild_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '公会id',
  `goods_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '物品id',
  `num` int(11) NOT NULL DEFAULT '0' COMMENT '物品数量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公会物品仓库';

-- Data exporting was unselected.

-- Dumping structure for table gamedemo.mall_order
CREATE TABLE IF NOT EXISTS `mall_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '玩家id',
  `cid` bigint(20) NOT NULL DEFAULT '0' COMMENT '商品id',
  `num` int(11) NOT NULL DEFAULT '0' COMMENT '购买数量',
  `cost` int(11) NOT NULL DEFAULT '0' COMMENT '花费',
  `create_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商城购买订单记录';

-- Data exporting was unselected.

-- Dumping structure for table gamedemo.pk_record
CREATE TABLE IF NOT EXISTS `pk_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `challenger` bigint(20) NOT NULL DEFAULT '0' COMMENT '挑战者id',
  `defender` bigint(20) NOT NULL DEFAULT '0' COMMENT '被挑战者',
  `winner` bigint(20) DEFAULT '0' COMMENT '获胜方',
  `loser` bigint(20) DEFAULT '0' COMMENT '战败方',
  `win_honor` int(11) NOT NULL DEFAULT '0' COMMENT '获胜荣誉值奖励',
  `lose_honor` int(11) NOT NULL DEFAULT '0' COMMENT '战败荣誉值奖励',
  `create_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '挑战创建时间',
  `end_time` datetime DEFAULT '0000-00-00 00:00:00' COMMENT '挑战结束时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='玩家pk挑战记录表';

-- Data exporting was unselected.

-- Dumping structure for table gamedemo.role
CREATE TABLE IF NOT EXISTS `role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `site_id` int(11) NOT NULL DEFAULT '0',
  `type` int(11) NOT NULL DEFAULT '0',
  `name` varchar(50) NOT NULL DEFAULT '0',
  `level` int(11) NOT NULL DEFAULT '0',
  `alive` int(11) NOT NULL DEFAULT '0' COMMENT '是否或者 1：存活，0：死亡',
  `hp` int(11) NOT NULL DEFAULT '0' COMMENT '玩家当前血量',
  `max_hp` int(11) DEFAULT '0' COMMENT '玩家的最大血量',
  `mp` int(11) DEFAULT '0' COMMENT '玩家当前蓝量',
  `gold` int(11) unsigned DEFAULT '0' COMMENT '玩家金币值',
  `honor` int(11) unsigned DEFAULT '0' COMMENT '玩家荣誉值',
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `role_idx` (`user_id`,`site_id`,`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10010 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='玩家角色表';

-- Data exporting was unselected.

-- Dumping structure for table gamedemo.role_attr
CREATE TABLE IF NOT EXISTS `role_attr` (
  `role_id` bigint(20) NOT NULL,
  `damage` int(11) DEFAULT NULL COMMENT '攻击力加成',
  `sp` int(11) DEFAULT NULL COMMENT '技能加成',
  `hp` int(11) DEFAULT NULL COMMENT '生命值加成',
  `armor` int(11) DEFAULT NULL COMMENT '护甲加成',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='玩家角色的属性栏（额外加成）';

-- Data exporting was unselected.

-- Dumping structure for table gamedemo.role_email
CREATE TABLE IF NOT EXISTS `role_email` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `from_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '发送方（系统默认为1）',
  `to_role_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '玩家id',
  `theme` varchar(50) NOT NULL DEFAULT '0' COMMENT '主题',
  `content` varchar(200) NOT NULL DEFAULT '0' COMMENT '邮件文本',
  `state` int(11) NOT NULL DEFAULT '0' COMMENT '领取状态，0：未领取，1：已领取',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='玩家邮件信息表';

-- Data exporting was unselected.

-- Dumping structure for table gamedemo.role_equip
CREATE TABLE IF NOT EXISTS `role_equip` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) DEFAULT '0' COMMENT '玩家id',
  `equip_id` bigint(20) DEFAULT '0' COMMENT '装备id',
  `durability` int(11) DEFAULT NULL COMMENT '耐久度',
  `state` int(11) DEFAULT NULL COMMENT '可用状态，1：可用，0：不可用',
  `has_on` int(11) DEFAULT '0' COMMENT '是否穿上',
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_equip_idx` (`role_id`,`equip_id`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='玩家当前穿戴的装备信息';

-- Data exporting was unselected.

-- Dumping structure for table gamedemo.role_equip_has
CREATE TABLE IF NOT EXISTS `role_equip_has` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '玩家id',
  `equip_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '装备id',
  `durability` int(11) NOT NULL DEFAULT '0' COMMENT '耐久度',
  `state` int(11) NOT NULL DEFAULT '0' COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_equip_has_idx` (`role_id`,`equip_id`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='玩家所拥有的装备表';

-- Data exporting was unselected.

-- Dumping structure for table gamedemo.role_guild
CREATE TABLE IF NOT EXISTS `role_guild` (
  `role_id` bigint(20) DEFAULT NULL COMMENT '玩家id',
  `gid` bigint(20) DEFAULT NULL COMMENT '公会id',
  `position` int(11) DEFAULT NULL COMMENT '职位',
  `today_con` int(11) DEFAULT NULL COMMENT '今日贡献值',
  `all_con` int(11) DEFAULT NULL COMMENT '总贡献值',
  UNIQUE KEY `role_guild_idx` (`role_id`,`gid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='玩家公会关联列表';

-- Data exporting was unselected.

-- Dumping structure for table gamedemo.role_objects
CREATE TABLE IF NOT EXISTS `role_objects` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_Id` bigint(20) NOT NULL DEFAULT '0' COMMENT '角色id',
  `objects_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '物品id',
  `num` bigint(20) NOT NULL DEFAULT '0' COMMENT '数量',
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_objects_idx` (`role_Id`,`objects_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1014 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色物品关联表';

-- Data exporting was unselected.

-- Dumping structure for table gamedemo.role_spell
CREATE TABLE IF NOT EXISTS `role_spell` (
  `id` bigint(60) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `role_Id` bigint(60) NOT NULL DEFAULT '0' COMMENT '角色id',
  `spell_id` int(11) NOT NULL DEFAULT '0' COMMENT '技能id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_spell_idx` (`role_Id`,`spell_id`)
) ENGINE=InnoDB AUTO_INCREMENT=108 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色技能表';

-- Data exporting was unselected.

-- Dumping structure for table gamedemo.role_state
CREATE TABLE IF NOT EXISTS `role_state` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) NOT NULL DEFAULT '0',
  `online_time` datetime DEFAULT NULL,
  `offline_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_id` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='玩家的在线下线状态';

-- Data exporting was unselected.

-- Dumping structure for table gamedemo.task
CREATE TABLE IF NOT EXISTS `task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `role_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '玩家id',
  `task_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '任务id',
  `progress` int(11) NOT NULL DEFAULT '1' COMMENT '1：未领取， 2：已领取任务，但还未进行， 3：任务完成，已经领取， 4：放弃任务， 5：任务完成，未领取',
  `create_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '领取任务时间',
  `modify_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '修改任务的时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='玩家任务关系表';

-- Data exporting was unselected.

-- Dumping structure for table gamedemo.trade
CREATE TABLE IF NOT EXISTS `trade` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '交易id',
  `goods_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '物品id',
  `num` int(11) NOT NULL DEFAULT '0' COMMENT '物品数量',
  `seller` bigint(20) NOT NULL DEFAULT '0' COMMENT '卖家id',
  `buyer` bigint(20) NOT NULL DEFAULT '0' COMMENT '买家id',
  `price` int(11) NOT NULL DEFAULT '0' COMMENT '价格',
  `type` int(11) NOT NULL DEFAULT '0' COMMENT '交易类型: 1：一口价， 2：拍卖',
  `process` int(11) NOT NULL DEFAULT '0' COMMENT '交易状态: 1：进行中，2：已经结束',
  `start_time` datetime DEFAULT NULL COMMENT '交易开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '交易结束时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='交易行的交易信息';

-- Data exporting was unselected.

-- Dumping structure for table gamedemo.user_account
CREATE TABLE IF NOT EXISTS `user_account` (
  `user_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_name` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `user_idx` (`user_name`,`password`)
) ENGINE=InnoDB AUTO_INCREMENT=1012 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户账号表';

-- Data exporting was unselected.

-- Dumping structure for table gamedemo.user_site
CREATE TABLE IF NOT EXISTS `user_site` (
  `user_id` bigint(20) DEFAULT NULL,
  `site_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户场景表';

-- Data exporting was unselected.

-- Dumping structure for table gamedemo.user_state
CREATE TABLE IF NOT EXISTS `user_state` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) NOT NULL DEFAULT '0',
  `online_time` datetime DEFAULT NULL,
  `offline_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unx_role_idx` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='记录玩家上线，下线的状态';

-- Data exporting was unselected.

-- Dumping structure for table gamedemo.user_token
CREATE TABLE IF NOT EXISTS `user_token` (
  `user_id` bigint(20) DEFAULT NULL,
  `token` varchar(50) DEFAULT NULL,
  KEY `token_idx` (`user_id`,`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户token表';

-- Data exporting was unselected.

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
