package com.ljh.gamedemo.entity;

import lombok.Data;

/**
 * PK 奖励实体类
 *
 * @Author: Heiku
 * @Date: 2019/8/12
 */

@Data
public class PKReward {

    /**
     * 奖励id
     */
    private Long id;

    /**
     * 胜者荣誉值奖励
     */
    private Integer win;

    /**
     * 败者荣誉值奖励
     */
    private Integer lose;

    /**
     * 奖励等级
     */
    private Integer level;
}
