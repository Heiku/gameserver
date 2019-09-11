package com.ljh.gamedemo.module.spell.bean;

import lombok.Data;

/**
 * 玩家技能关联
 *
 * @Author: Heiku
 * @Date: 2019/7/11
 */

@Data
public class RoleSpell {

    /**
     * 玩家id
     */
    private long roleId;

    /**
     * 技能id
     */
    private int spellId;
}
