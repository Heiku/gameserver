package com.ljh.gamedemo.module.creep.local;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ljh.gamedemo.module.task.bean.Task;

import java.util.concurrent.TimeUnit;

/**
 * 攻击野怪事件的处理器
 *
 * @Author: Heiku
 * @Date: 2019/9/10
 */
public class AttackCreepEventCache {

    private static Cache<Long, Task> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build();

    public static Cache<Long, Task> getCache() {
        return cache;
    }
}
