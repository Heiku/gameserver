package com.ljh.gamedemo.module.role.service;

import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.spell.bean.Spell;
import com.ljh.gamedemo.module.role.bean.RoleAttr;
import com.ljh.gamedemo.module.role.cache.RoleAttrCache;
import org.springframework.stereotype.Service;

/**
 * 玩家Buff服务
 *
 * @Author: Heiku
 * @Date: 2019/9/3
 */

@Service
public class RoleAttrService {


    /**
     * 获取技能的额外伤害加成
     *
     * @param role      玩家信息
     * @param spell     技能信息
     * @return          伤害加成
     */
    public synchronized int getExtraDamage(Role role, Spell spell){
        int extra = 0;
        // 获取 Buff 加成的额外伤害
        RoleAttr attr = RoleAttrCache.getRoleAttrMap().get(role.getRoleId());
        if (attr != null){
            extra = spell.getCost() == 0 ? attr.getDamage() : attr.getSp();
        }

        return extra;
    }
}
