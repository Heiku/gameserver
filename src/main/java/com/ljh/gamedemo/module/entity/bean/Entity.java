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
    private Long id;

    /**
     * 实体类型 EntityType
     */
    private Integer type;

    /**
     * 名字
     */
    private String name;

    /**
     * 等级
     */
    private Integer level;

    /**
     * 是否或者 1：存活，0：死亡
     */
    private Integer alive;

    /**
     * 位置信息
     */
    private Site site;
}
