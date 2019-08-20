package com.ljh.gamedemo.local.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.ljh.gamedemo.entity.BossSpell;
import com.ljh.gamedemo.entity.dto.RoleBuff;
import com.ljh.gamedemo.entity.tmp.RoleDeBuff;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 存储玩家身上的buff
 *
 * @Author: Heiku
 * @Date: 2019/7/22
 */
public class RoleBuffCache {

    /**
     * 玩家身上的buff信息
     */
    private static Cache<Long, List<RoleBuff>> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(60, TimeUnit.SECONDS)
            .build();

    /**
     * 用户被施放的控制技能的时间点 (用户判断控制是否生效)
     */
    private static Map<Long, RoleDeBuff> roleDeBuffMap = Maps.newConcurrentMap();

    public static Cache<Long, List<RoleBuff>> getCache() {
        return cache;
    }

    public static Map<Long, RoleDeBuff> getRoleDeBuffMap() {
        return roleDeBuffMap;
    }
}
