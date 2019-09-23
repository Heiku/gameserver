package com.ljh.gamedemo.module.equip.bean;

import lombok.Data;

/**
 * 玩家装备实体类
 *
 * @Author: Heiku
 * @Date: 2019/7/17
 */
@Data
public class RoleEquip {

    /**
     * 装备编号
     */
    private Long id;

    /**
     * 玩家id
     */
    private Long roleId;

    /**
     * 装备id
     */
    private Long equipId;

    /**
     * 耐久度
     */
    private Integer durability;

    /**
     * 可用状态：0：不可用，1：可用状态
     */
    private Integer state;

    /**
     * 是否穿上，0：否， 1：穿上
     */
    private Integer on;
}
