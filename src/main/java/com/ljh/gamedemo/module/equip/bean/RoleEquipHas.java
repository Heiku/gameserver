package com.ljh.gamedemo.module.equip.bean;

import lombok.Data;

/**
 * 玩家所拥有的装备实体
 *
 * @Author: Heiku
 * @Date: 2019/7/29
 */

@Data
public class RoleEquipHas {

    /**
     *  装备编号
     */
    private Long id;

    /**
     *  玩家id
     */
    private Long roleId;

    /**
     * 装备id
     */
    private Long equipId;

    /**
     * 耐久度
     */
    private Integer duration;

    /**
     * 装备状态
     */
    private Integer state;
}
