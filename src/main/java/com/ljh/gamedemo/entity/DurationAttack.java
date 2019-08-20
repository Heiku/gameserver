package com.ljh.gamedemo.entity;

import lombok.Data;

/**
 * 持续技能伤害实体类
 *
 * @Author: Heiku
 * @Date: 2019/8/20
 */

@Data
public class DurationAttack {

    /**
     * 总伤害
     */
    private Integer damage;

    /**
     * 持续时间
     */
    private Integer sec;
}
