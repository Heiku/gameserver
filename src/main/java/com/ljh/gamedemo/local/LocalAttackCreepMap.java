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

    public static Map<Channel, Creep> getChannelCreepMap() {
        return channelCreepMap;
    }

    public static void setChannelCreepMap(Map<Channel, Creep> channelCreepMap) {
        LocalAttackCreepMap.channelCreepMap = channelCreepMap;
    }
}
