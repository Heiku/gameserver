package com.ljh.gamedemo.module.spell.bean;

import lombok.Data;

/**
 * 技能实体类
 *
 * @Author: Heiku
 * @Date: 2019/7/11
 */
@Data
public class Spell {

    /**
     * 技能id
     */
    private Integer spellId;

    /**
     * 名字
     */
    private String name;

    /**
     * 技能等级
     */
    private Integer level;

    /**
     * 技能伤害
     */
    private Integer damage;

    /**
     * 蓝耗
     */
    private Integer cost;

    /**
     * cd
     */
    private Integer coolDown;

    /**
     * school：物理伤害：1，法术伤害：2，暗影伤害：3， 魔法护盾：4
     */
    private Integer school;

    /**
     * type: 职业类型，对应entity的type
     */
    private Integer type;

    /**
     * range：判断是否是范围伤害 1:单独伤害，2：范围伤害
     */
    private Integer range;

    /**
     * sec: 技能的持续时间
     */
    private Integer sec;

    /**
     * 增益效果
     */
    private Integer up;
}
