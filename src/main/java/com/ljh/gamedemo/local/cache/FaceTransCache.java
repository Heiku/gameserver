package com.ljh.gamedemo.local.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.ljh.gamedemo.entity.Transaction;
import com.ljh.gamedemo.entity.tmp.FaceTransApply;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 面对面交易信息缓存
 *
 * @Author: Heiku
 * @Date: 2019/8/22
 */
public class FaceTransCache {

    /**
     * 存放临时的申请交易信息 (tmpTransId, ApplyFaceTransaction)
     */
    private static Cache<Long, FaceTransApply> applyFaceTransCache = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();


    /**
     * 存放当前进行的交易关系
     */
    private static Map<Long, FaceTransApply> roleFaceTransMap = Maps.newConcurrentMap();

    /**
     * 本地缓存的面对面交易记录（未确认的交易记录） (roleId, transaction)
     */
    private static Map<Long, Transaction>  unConfirmTransMap = Maps.newConcurrentMap();


    public static Cache<Long, FaceTransApply> getApplyFaceTransCache() {
        return applyFaceTransCache;
    }

    public static Map<Long, FaceTransApply> getRoleFaceTransMap() {
        return roleFaceTransMap;
    }

    public static Map<Long, Transaction> getUnConfirmTransMap() {
        return unConfirmTransMap;
    }
}
