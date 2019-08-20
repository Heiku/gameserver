package com.ljh.gamedemo.entity.tmp;

import lombok.Data;

/**
 * 玩家身上的 deBuff信息
 *
 * @Author: Heiku
 * @Date: 2019/8/20
 */

@Data
public class RoleDeBuff {

    /**
     * 持续时间
     */
    private Integer sec;

    /**
     * 当前施放技能的时间点
     */
    private Long ts;
}
