package com.ljh.gamedemo.local;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.entity.Duplicate;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.Map;
import java.util.Queue;

/**
 * @Author: Heiku
 * @Date: 2019/7/12
 */
public class LocalAttackCreepMap {

    private static Map<Long, ScheduledFuture> userBeAttackedMap = Maps.newConcurrentMap();

    // 玩家当前攻击的野怪信息
    private static Map<Long, Long> currentCreepMap = Maps.newConcurrentMap();

    // 野怪与玩家攻击相关联  <creepId, List<RoleId>>
    private static Map<Long, Queue<Long>> creepAttackedMap = Maps.newConcurrentMap();

    // Boss 与玩家攻击相关联 <Duplicate, List<RoleId>>
    private static Map<Duplicate, Queue<Long>> bossAttackedMap = Maps.newConcurrentMap();


    // 玩家攻击副本boss关联
    // 单人打boss：<roleId, duplicate>
    // 组队打boss：<roleId + roleId + ... , duplicate>
    private static Map<Long, Duplicate> curDupMap = Maps.newConcurrentMap();

    // 挑战boss的时间戳，用于判断挑战副本成功
    private static Map<Long, Long> dupTimeStampMap = Maps.newConcurrentMap();

    public static Map<Long, Duplicate> getCurDupMap() {
        return curDupMap;
    }

    public static Map<Long, ScheduledFuture> getUserBeAttackedMap() {
        return userBeAttackedMap;
    }

    public static Map<Long, Queue<Long>> getCreepAttackedMap() {
        return creepAttackedMap;
    }

    public static Map<Long, Long> getCurrentCreepMap() {
        return currentCreepMap;
    }

    public static Map<Long, Long> getDupTimeStampMap() {
        return dupTimeStampMap;
    }

    public static Map<Duplicate, Queue<Long>> getBossAttackedMap() {
        return bossAttackedMap;
    }
}
