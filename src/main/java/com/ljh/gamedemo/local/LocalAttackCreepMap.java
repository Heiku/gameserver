package com.ljh.gamedemo.local;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.entity.Duplicate;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * 存储所有关于涉及攻击的数据
 *
 * @Author: Heiku
 * @Date: 2019/7/12
 */
public class LocalAttackCreepMap {

    /**
     * 玩家受到的伤害future，现只用于Creep
     */
    private static Map<Long, ScheduledFuture> userBeAttackedMap = Maps.newConcurrentMap();

    /**
     * 玩家当前攻击的野怪信息
     */
    private static Map<Long, Long> currentCreepMap = Maps.newConcurrentMap();

    /**
     * 野怪与玩家攻击相关联  <creepId, List<RoleId>>
     */
    private static Map<Long, Deque<Long>> creepAttackedMap = Maps.newConcurrentMap();


    /**
     * Duplicate相关
     * Boss的攻击目标队列
     */
    private static Map<Long, Deque<Long>> bossAttackQueueMap = Maps.newConcurrentMap();

    /**
     * 玩家攻击副本boss关联
     *     单人打boss：<roleId, duplicate>
     *     组队打boss：<groupId, duplicate>
     */
    private static Map<Long, Duplicate> curDupMap = Maps.newConcurrentMap();

    /**
     * 挑战boss的时间戳，用于判断挑战副本成功
     */
    private static Map<Long, Long> dupTimeStampMap = Maps.newConcurrentMap();

    /**
     * 玩家施放的持续技能任务 (玩家施放)
     */
    private static Map<Long, List<ScheduledFuture>> spellToBossFutMap = Maps.newConcurrentMap();

    /**
     * 当前场景中的自动施放任务 （Boss施放）
     */
    private static Map<Long, List<ScheduledFuture>> dupAllFutureMap = Maps.newConcurrentMap();

    /**
     * 多场景下的玩家受到的持续伤害
     */
    private static Map<Long, List<ScheduledFuture>> roleSchFutMap = Maps.newConcurrentMap();


    public static Map<Long, Duplicate> getCurDupMap() {
        return curDupMap;
    }

    public static Map<Long, ScheduledFuture> getUserBeAttackedMap() {
        return userBeAttackedMap;
    }

    public static Map<Long, Deque<Long>> getCreepAttackedMap() {
        return creepAttackedMap;
    }

    public static Map<Long, Long> getCurrentCreepMap() {
        return currentCreepMap;
    }

    public static Map<Long, Long> getDupTimeStampMap() {
        return dupTimeStampMap;
    }


    public static Map<Long, Deque<Long>> getBossAttackQueueMap() {
        return bossAttackQueueMap;
    }

    public static Map<Long, List<ScheduledFuture>> getSpellToBossFutMap() {
        return spellToBossFutMap;
    }

    public static Map<Long, List<ScheduledFuture>> getDupAllFutureMap() {
        return dupAllFutureMap;
    }

    public static Map<Long, List<ScheduledFuture>> getRoleSchFutMap() {
        return roleSchFutMap;
    }
}
