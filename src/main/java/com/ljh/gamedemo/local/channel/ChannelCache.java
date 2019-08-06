package com.ljh.gamedemo.local.channel;

import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Map;

/**
 * @Author: Heiku
 * @Date: 2019/8/6
 */
public class ChannelCache {

    // 所有的玩家channel
    private static ChannelGroup allRoleGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    // 玩家对应的channel <userId, Channel>
    private static final Map<Long, Channel> userIdChannelMap = Maps.newConcurrentMap();

    public static Map<Long, Channel> getUserIdChannelMap() {
        return userIdChannelMap;
    }

    public static ChannelGroup getAllRoleGroup() {
        return allRoleGroup;
    }
}
