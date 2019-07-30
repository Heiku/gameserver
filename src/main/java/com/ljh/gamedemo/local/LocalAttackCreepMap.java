package com.ljh.gamedemo.local;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.entity.Creep;
import com.ljh.gamedemo.entity.Duplicate;
import io.netty.channel.Channel;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.Map;

/**
 * @Author: Heiku
 * @Date: 2019/7/12
 */
public class LocalAttackCreepMap {

    public static Map<Channel, Creep> channelCreepMap = Maps.newConcurrentMap();

    public static Map<Channel, Long> channelTimeStampMap = Maps.newConcurrentMap();


    private static Map<Long, ScheduledFuture> userBeAttackedMap = Maps.newConcurrentMap();

    // 玩家攻击副本boss关联
    private static Map<Long, Duplicate> curDupMap = Maps.newConcurrentMap();

    public static Map<Channel, Creep> getChannelCreepMap() {
        return channelCreepMap;
    }

    public static Map<Channel, Long> getChannelTimeStampMap() {
        return channelTimeStampMap;
    }

    public static void setChannelCreepMap(Map<Channel, Creep> channelCreepMap) {
        LocalAttackCreepMap.channelCreepMap = channelCreepMap;
    }

    public static void setChannelTimeStampMap(Map<Channel, Long> channelTimeStampMap) {
        LocalAttackCreepMap.channelTimeStampMap = channelTimeStampMap;
    }

    public static Map<Long, Duplicate> getCurDupMap() {
        return curDupMap;
    }

    public static Map<Long, ScheduledFuture> getUserBeAttackedMap() {
        return userBeAttackedMap;
    }
}
