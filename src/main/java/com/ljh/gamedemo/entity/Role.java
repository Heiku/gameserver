package com.ljh.gamedemo.entity;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 玩家角色
 */
@Data
public class Role {

    /**
     * 玩家id
     */
    private Long userId;

    /**
     * 账号id
     */
    private Long roleId;

    /**
     * 位置id
     */
    private Integer siteId;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色类型
     */
    private Integer type;

    /**
     * 是否或者 1：存活，0：死亡
     */
    private Integer alive;

    /**
     * 蓝量
     */
    private Integer hp;

    /**
     * 血量上限
     */
    private Integer maxHp;

    /**
     * 当前生命值
     */
    private Integer mp;

    /**
     * 玩家金币值
     */
    private Integer gold;

    /**
     * 角色等级
     */
    private Integer level;


    /**
     * 荣誉值
     */
    private Integer honor;

    /**
     * 玩家技能列表
     */
    private List<Spell> spellList = new ArrayList<>();
}
