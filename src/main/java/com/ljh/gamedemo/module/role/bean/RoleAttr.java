package com.ljh.gamedemo.module.role.bean;

import lombok.Data;

/**
 * 用户的增益属性栏
 *
 * @Author: Heiku
 * @Date: 2019/7/23
 */

@Data
public class RoleAttr {

    /**
     * 所属的玩家角色
     */
    private Long roleId;

    /**
     * 攻击力加成
     */
    private Integer damage;

    /**
     * 法强，技能加成
     */
    private Integer sp;

    /**
     * 血量加成
     */
    private Integer hp;

    /**
     * 护甲加成
     */
    private Integer armor;
}
