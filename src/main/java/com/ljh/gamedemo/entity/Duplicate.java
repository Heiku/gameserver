package com.ljh.gamedemo.entity;

import lombok.Data;

import java.util.List;

/**
 * @Author: Heiku
 * @Date: 2019/7/29
 *
 * 副本信息
 */

@Data
public class Duplicate {

    private Long id;

    private String name;

    /**
     * Boss野怪
     */
    private List<Boss> bosses;

    /**
     * 装备奖励
     */
    private List<Equip> equipReward;

    /**
     * 金币奖励
     */
    private Integer goldReward;

    /**
     * 副本总进度 e.g. progress=2 : 代表一共有两个Boss，需要完全击杀才能获得副本奖励
     */
    private Integer progress;

    /**
     * 限定时间
     */
    private Integer limitTime;
}
