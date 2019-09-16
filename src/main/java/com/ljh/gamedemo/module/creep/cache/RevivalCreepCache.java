package com.ljh.gamedemo.module.creep.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListeners;
import com.ljh.gamedemo.module.creep.bean.Creep;
import com.ljh.gamedemo.module.creep.listener.RevivalCreepListener;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 存储需要复活的野怪
 *
 * @Author: Heiku
 * @Date: 2019/9/16
 */
public class RevivalCreepCache {

    private static Cache<Long, Creep> revivalCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .removalListener(RemovalListeners.asynchronous(new RevivalCreepListener(), Executors.newSingleThreadExecutor()))
            .build();

    public static Cache<Long, Creep> getRevivalCache() {
        return revivalCache;
    }
}
