package com.ljh.gamedemo.local.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.ljh.gamedemo.entity.Group;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 组队信息本地缓存
 *
 * @Author: Heiku
 * @Date: 2019/8/14
 */
public class GroupCache {

    // groupCache <groupId, Group>
    private static Cache<Long, Group> groupCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build();

    // 玩家队伍关联信息
    private static Map<Long, Group> roleGroupMap = Maps.newConcurrentMap();


    public static Cache<Long, Group> getGroupCache() {
        return groupCache;
    }

    public static Map<Long, Group> getRoleGroupMap() {
        return roleGroupMap;
    }
}
