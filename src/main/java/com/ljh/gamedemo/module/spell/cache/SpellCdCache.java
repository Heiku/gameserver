package com.ljh.gamedemo.module.spell.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ljh.gamedemo.module.spell.tmp.SpellTimeStamp;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 存储用户玩家的cd时间间隔
 *
 * @Author: Heiku
 * @Date: 2019/7/19
 */
public class SpellCdCache {


    /**
     * 玩家技能时间缓存
     */
    private static Cache<Long, List<SpellTimeStamp>> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .build();


    public static Cache<Long, List<SpellTimeStamp>> getCache() {
        return cache;
    }

}
