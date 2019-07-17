package com.ljh.gamedemo.entity;

import lombok.Data;

import java.util.List;

/**
 * @Author: Heiku
 * @Date: 2019/7/17
 *
 * 装备实体类
 */

@Data
public class Equip {

    private Long equipId;

    private String name;

    /**
     * 装备的职业类别
     */
    private Integer type;

    /**
     * 身体部位
     */
    private Integer part;

    /**
     * 装备的等级
     */
    private Integer level;

    /**
     * 耐久
     */
    private Integer durability;


    /**
     * 增益效果，len = 2，[0] = 普攻， [1] = 技能伤害
     */
    private List<Integer> up;


    /**
     * 表示装备的状态：1：可用，0：不可用
     */
    private Integer state;
}
