package com.ljh.gamedemo.module.items.bean;

import lombok.Data;

/**
 * 玩家物品关联实体类
 *
 * @Author: Heiku
 * @Date: 2019/7/16
 */

@Data
public class RoleItems {

    /**
     * id
     */
    private Long id;

    /**
     * 玩家id
     */
    private Long roleId;

    /**
     * 物品id
     */
    private Long objectsId;

    /**
     * 物品数量
     */
    private Integer num;
}
