package com.ljh.gamedemo.entity;

import lombok.Data;

/**
 * 召唤出来的伙伴实体
 *
 * @Author: Heiku
 * @Date: 2019/8/20
 */

@Data
public class Partner {

    /**
     * 召唤出来的伙伴关联的id (roleId)
     */
    private Long id;

    /**
     * 所属的玩家
     */
    private Long roleId;

    /**
     * 伙伴名
     */
    private String name;

    /**
     * 伙伴攻击伤害
     */
    private Integer damage;

    /**
     * 伙伴hp
     */
    private Integer hp;

}
