package com.ljh.gamedemo.entity;

import lombok.Data;

/**
 * @Author: Heiku
 * @Date: 2019/7/29
 *
 * boss的技能值
 */

@Data
public class BossSpell {

    private Long spellId;

    private String name;

    /**
     * 技能的类别：0.普通攻击  1.眩晕控制  2.中毒效果
     */
    private Integer school;

    /**
     * boss的技能cd
     */
    private Integer cd;

    /**
     * 是否是范围伤害 0：否  1：是
     */
    private Integer range;

    /**
     * 技能的持续时间：眩晕时间，或是 中毒效果的时间
     */
    private Integer sec;

    /**
     * 伤害值
     */
    private Integer damage;
}
