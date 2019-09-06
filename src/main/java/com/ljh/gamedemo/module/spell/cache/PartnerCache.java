package com.ljh.gamedemo.module.spell.cache;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.module.spell.bean.Partner;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.Map;

/**
 * 召唤伙伴缓存
 *
 * @Author: Heiku
 * @Date: 2019/8/20
 */
public class PartnerCache {

    /**
     * 存放当前系统中的所有伙伴 (partnerId, Partner)
     */
    private static Map<Long, Partner> idPartnerMap = Maps.newConcurrentMap();

    /**
     * 用户的伙伴数据 (roleId, Partner)
     */
    private static Map<Long, Partner> rolePartnerMap = Maps.newConcurrentMap();

    /**
     * 伙伴攻击的future (partnerId, future)
     */
    private static Map<Long, ScheduledFuture> partnerFutureMap = Maps.newConcurrentMap();


    public static Map<Long, Partner> getIdPartnerMap() {
        return idPartnerMap;
    }

    public static Map<Long, Partner> getRolePartnerMap() {
        return rolePartnerMap;
    }

    public static Map<Long, ScheduledFuture> getPartnerFutureMap() {
        return partnerFutureMap;
    }
}
