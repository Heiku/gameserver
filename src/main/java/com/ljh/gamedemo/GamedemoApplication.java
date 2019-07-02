package com.ljh.gamedemo;

import com.ljh.gamedemo.local.LocalEntityMap;
import com.ljh.gamedemo.local.LocalSiteMap;
import com.ljh.gamedemo.local.LocalUserMap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GamedemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(GamedemoApplication.class, args);

        // 初始化时载入地图数据
        LocalSiteMap.readCsv();

        // 初始化时载入实体数据
        LocalEntityMap.readCsv();

        // 初始化载入角色信息
        LocalUserMap.readSiteRoles();
    }

}
