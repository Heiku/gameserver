package com.ljh.gamedemo.module.duplicate.bean;

import com.ljh.gamedemo.module.equip.bean.Equip;
import lombok.Data;

import java.util.List;

/**
 * 副本信息
 *
 * @Author: Heiku
 * @Date: 2019/7/29
 */

@Data
public class Duplicate {

    /**
     * 副本id
     */
    private Long id;


    /**
     * 副本名称
     */
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

    /**
     * 挑战者（队伍 / 独立玩家） 与 副本关联的id
     */
    private Long relatedId;

    /**
     * 装备奖励概率
     */
    private List<Double> probability;
}
