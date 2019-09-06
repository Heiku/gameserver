package com.ljh.gamedemo.module.entity.bean;

import com.ljh.gamedemo.module.site.bean.Site;
import lombok.Data;

/**
 * npc实体
 */
@Data
public class Entity {
    /**
     * 唯一标识符
     */
    private long id;

    /**
     * 实体类型 EntityType
     */
    private int type;

    /**
     * 名字
     */
    private String name;

    /**
     * 等级
     */
    private int level;

    /**
     * 是否或者 1：存活，0：死亡
     */
    private int alive;


    private Site site;
}
