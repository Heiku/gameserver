package com.ljh.gamedemo.entity;

import lombok.Data;

/**
 * 野怪实体
 *
 */

@Data
public class Creep {

    /**
     * 野怪id
     */
    private Integer creepId;

    /**
     * 野怪类型
     */
    private Integer type;

    /**
     * 野怪名字
     */
    private String name;

    /**
     * 野怪数量
     */
    private Integer num;

    /**
     * 等级
     */
    private Integer level;


    /**
     * 生命值
     */
    private Integer hp;

    /**
     * 伤害值
     */
    private Integer damage;

    /**
     * 场景id
     */
    private Integer siteId;

}