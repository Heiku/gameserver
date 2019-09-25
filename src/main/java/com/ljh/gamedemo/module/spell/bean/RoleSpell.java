package com.ljh.gamedemo.module.spell.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 玩家技能关联
 *
 * @Author: Heiku
 * @Date: 2019/7/11
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
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
