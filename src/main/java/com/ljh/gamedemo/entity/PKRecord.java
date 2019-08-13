package com.ljh.gamedemo.entity;

import lombok.Data;

import java.util.Date;

/**
 * PK挑战实体类
 *
 * @Author: Heiku
 * @Date: 2019/8/12
 */

@Data
public class PKRecord {

    /**
     * 标识id
     */
    private Long id;

    /**
     * 挑战者id
     */
    private Long challenger;

    /**
     * 被挑战者id
     */
    private Long defender;

    /**
     * 获胜者id
     */
    private Long winner;

    /**
     * 战败方id
     */
    private Long loser;

    /**
     * 胜者奖励的荣誉值
     */
    private Integer winHonor;

    /**
     * 战败者荣誉值
     */
    private Integer loseHonor;

    /**
     * 挑战开始的时间
     */
    private Date createTime;

    /**
     * 挑战结束时间
     */
    private Date endTime;
}
