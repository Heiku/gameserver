package com.ljh.gamedemo.module.duplicate.bean;

import lombok.Data;

import java.util.List;

/**
 * Boss实体类
 *
 * @Author: Heiku
 * @Date: 2019/7/29
 */

@Data
public class Boss {

    /**
     * id
     */
    private Long id;

    /**
     * Boss名称
     */
    private String name;

    /**
     * Boss 血量
     */
    private Integer hp;

    /**
     * Boss最大生命值
     */
    private Integer maxHp;

    /**
     * boss的技能
     */
    private List<BossSpell> spellList;
}
