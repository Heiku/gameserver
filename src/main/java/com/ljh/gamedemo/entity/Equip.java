package com.ljh.gamedemo.entity;

import lombok.Data;

/**
 * 装备实体类
 *
 * @Author: Heiku
 * @Date: 2019/7/17
 */

@Data
public class Equip {

    /**
     * 装备id
     */
    private Long equipId;

    /**
     * 装备名
     */
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
     * 普通攻击增益
     */
    private Integer aUp;

    /**
     * 技能攻击增益
     */
    private Integer spUp;

    /**
     * 血量增益
     */
    private Integer hpUp;

    /**
     * 护甲值
     */
    private Integer armor;

    /**
     * 耐久
     */
    private Integer durability;

    /**
     * 表示装备的状态：1：可用，0：不可用
     */
    private Integer state;

    /**
     * 最小交易金额
     */
    private Integer minTrans;
}
