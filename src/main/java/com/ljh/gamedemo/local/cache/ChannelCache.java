package com.ljh.gamedemo.local.cache;

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

    /**
     * 所有的玩家channel
     */
    private static ChannelGroup allRoleGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 玩家对应的channel <userId, Channel>
     */
    private static final Map<Long, Channel> userIdChannelMap = Maps.newConcurrentMap();

    /**
     * 保存队伍内的channel信息
     */
    private static Map<Long, ChannelGroup> groupChannelMap = Maps.newConcurrentMap();

    /**
     * 保存公会内的channel信息
     */
    private static Map<Long, ChannelGroup> guildChannelMap = Maps.newConcurrentMap();


    public static Map<Long, Channel> getUserIdChannelMap() {
        return userIdChannelMap;
    }

    public static ChannelGroup getAllRoleGroup() {
        return allRoleGroup;
    }

    public static Map<Long, ChannelGroup> getGroupChannelMap() {
        return groupChannelMap;
    }

    public static Map<Long, ChannelGroup> getGuildChannelMap() {
        return guildChannelMap;
    }
}
