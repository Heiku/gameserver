package com.ljh.gamedemo.local.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ljh.gamedemo.entity.dto.RoleBuff;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Heiku
 * @Date: 2019/7/22
 */
public class RoleBuffCache {

    private static Cache<Long, List<RoleBuff>> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .build();

    public static Cache<Long, List<RoleBuff>> getCache() {
        return cache;
    }
}
