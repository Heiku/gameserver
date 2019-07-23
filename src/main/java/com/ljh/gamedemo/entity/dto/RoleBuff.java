package com.ljh.gamedemo.entity.dto;

import lombok.Data;

/**
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
