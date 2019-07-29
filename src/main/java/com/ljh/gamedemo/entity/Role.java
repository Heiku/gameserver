package com.ljh.gamedemo.entity;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 玩家角色
 */
@Data
public class Role {

    private Long roleId;

    private Long userId;

    private Integer siteId;

    private Integer type;

    private String name;

    private Integer level;

    /**
     * 是否或者 1：存活，0：死亡
     */
    private Integer alive;

    /**
     * hp
     */
    private Integer hp;

    /**
     * mp
     */
    private Integer mp;


    /**
     * 玩家金币值
     */
    private Integer gold;

    private List<Spell> spellList = new ArrayList<>();
}
