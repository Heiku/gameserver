package com.ljh.gamedemo.entity;

import lombok.Data;

import java.util.List;

/**
 * @Author: Heiku
 * @Date: 2019/7/29
 *
 * boss信息
 */

@Data
public class Boss {

    private Long id;

    private String name;

    private Integer hp;

    /**
     * boss的技能
     */
    private List<BossSpell> spellList;
}
