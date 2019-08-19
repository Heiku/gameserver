package com.ljh.gamedemo.entity;

import lombok.Data;

/**
 * 玩家创建职业的基本职业类型属性
 *
 * @Author: Heiku
 * @Date: 2019/8/19
 */

@Data
public class RoleInit {

    /**
     * 职业类别
     */
    private Integer type;

    /**
     * 职业名称
     */
    private String name;

    /**
     * 初始的血量
     */
    private Integer hp;

    /**
     * 初始的蓝量
     */
    private Integer mp;

    /**
     * 职业描述
     */
    private String desc;
}
