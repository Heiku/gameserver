package com.ljh.gamedemo;

import com.ljh.gamedemo.local.*;
import com.ljh.gamedemo.local.cache.RoleAttrCache;
import com.ljh.gamedemo.local.cache.RoleStateCache;
import com.ljh.gamedemo.run.manager.SaveRoleItemManager;
import com.ljh.gamedemo.run.manager.SendEmailManager;
import com.ljh.gamedemo.server.codec.local.LocalMessageMap;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ljh.gamedemo.dao")
public class GamedemoApplication {

    public static void main(String[] args) throws Exception{
        SpringApplication.run(GamedemoApplication.class, args);

        // 载入协议信息
        LocalMessageMap.readAllMessageType();

        // 初始化时载入地图数据
        LocalSiteMap.readExcel();

        // 初始化时载入实体数据
        LocalEntityMap.readExcel();

        // 初始化载入角色信息
        LocalUserMap.readSiteRoles();

        // 初始化npc对话信息
        LocalTalkTextMap.readExcel();

        // 初始化野怪信息
        LocalCreepMap.readExcel();

        // 获取所有技能信息
        LocalSpellMap.readExcel();

        // 获取角色背包信息
        LocalItemsMap.readExcel();

        // 获取所有的装备信息
        LocalEquipMap.readExcel();

        // 获取所有的玩家属性值
        RoleAttrCache.readBDAttr();

        // 获取玩家的在线信息
        RoleStateCache.postConstructReadDb();

        // 载入boss信息
        LocalBossMap.readExcel();

        // 载入副本信息
        LocalDuplicateMap.readExcel();

        // 载入上商店信息
        LocalCommodityMap.readExcel();

        // 载入玩家pk挑战的奖励信息
        LocalPKRewardMap.readExcel();

        // 启动背包数据存库线程池
        SaveRoleItemManager.run();

        // 启动邮件队列线程池
        SendEmailManager.run();
    }

}
