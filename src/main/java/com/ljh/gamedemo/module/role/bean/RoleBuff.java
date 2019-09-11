package com.ljh.gamedemo.module.role.bean;

import lombok.Data;

/**
 * 玩家Buff实体类
 *
 * @Author: Heiku
 * @Date: 2019/7/22
 */

@Data
public class RoleBuff {

    /**
     * 所属的角色id
     */
    private Long roleId;

    /**
     * buff 类别
     */
    private Integer type;

    /**
     * 护盾值
     */
    private Integer shield;

    /**
     * 持续时间
     */
    private Integer sec;

    /**
     * 创建时间
     */
    private Long createTime;
}
