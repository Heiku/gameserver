package com.ljh.gamedemo;

import com.ljh.gamedemo.local.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GamedemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(GamedemoApplication.class, args);

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
    }

}
