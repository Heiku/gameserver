package com.ljh.gamedemo.local.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ljh.gamedemo.entity.tmp.MallBuyTimes;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Heiku
 * @Date: 2019/8/5
 *
 * 玩家某样物品的最大购买数量
 */
public class MallBuyCache {

    // 设置为一天的间隔，每隔一天就刷新一次购买限制
    private static Cache<Long, List<MallBuyTimes>> buyTimesCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build();

    public static Cache<Long, List<MallBuyTimes>> getBuyTimesCache() {
        return buyTimesCache;
    }
}