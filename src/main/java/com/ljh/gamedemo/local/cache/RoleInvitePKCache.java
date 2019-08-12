package com.ljh.gamedemo.local.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.ljh.gamedemo.entity.PKRecord;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 发起挑战记录的缓存存储
 *
 * @Author: Heiku
 * @Date: 2019/8/12
 */
public class RoleInvitePKCache {

    /**
     * 格式为 <cRoleId, dRoleId> ： <挑战者，被挑战者>
     *
     * 挑战的邀请在 1min 后失效，到时需要重新发送 pk 邀请
     */
    private static Cache<Long, Long> pkInviteCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    /**
     * 记录双方的 pk 挑战记录
     */
    private static Map<Long, PKRecord> pkRecordMap = Maps.newConcurrentMap();

    public static Cache<Long, Long> getPkInviteCache() {
        return pkInviteCache;
    }

    public static Map<Long, PKRecord> getPkRecordMap() {
        return pkRecordMap;
    }
}
