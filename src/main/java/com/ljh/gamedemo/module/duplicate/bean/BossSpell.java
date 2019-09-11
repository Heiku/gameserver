package com.ljh.gamedemo.module.duplicate.bean;

import lombok.Data;

/**
 * Boss技能实体类
 *
 * @Author: Heiku
 * @Date: 2019/7/29
 */

@Data
public class BossSpell {

    /**
     * 技能id
     */
    private Long spellId;

    /**
     * 技能名称
     */
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
