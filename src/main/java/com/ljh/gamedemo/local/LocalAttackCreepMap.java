package com.ljh.gamedemo.local;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.entity.Creep;
import io.netty.channel.Channel;

import java.util.Map;

/**
 * @Author: Heiku
 * @Date: 2019/7/12
 */
public class LocalAttackCreepMap {

    public static Map<Channel, Creep> channelCreepMap = Maps.newConcurrentMap();

    public static Map<Channel, Long> channelTimeStampMap = Maps.newConcurrentMap();


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
}
