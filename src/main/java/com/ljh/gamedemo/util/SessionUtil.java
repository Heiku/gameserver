package com.ljh.gamedemo.util;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.attribute.Attributes;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

import java.util.Map;

public class SessionUtil {

    private static final Map<Long, Channel> userIdChannelMap = Maps.newConcurrentMap();

    // 用于以后的广播通知
    private static final Map<Long, ChannelGroup> groupIdChannelGroupMap = Maps.newConcurrentMap();


    /**
     * userId 绑定channel
     *
     * @param userId
     * @param channel
     */
    public static void bindSession(Long userId, Channel channel){
        userIdChannelMap.put(userId, channel);

        channel.attr(Attributes.USER_ID).set(userId);
    }

    /**
     * 接触绑定
     *
     * @param channel
     */
    public static void unBindSession(Channel channel){
        long userId = getUserId(channel);

        userIdChannelMap.remove(userId);

        channel.attr(Attributes.USER_ID).set(0L);
    }


    public static long getUserId(Channel channel){
        return channel.attr(Attributes.USER_ID).get();
    }

    /**
     * 获取userId 绑定的 channel
     *
     * @param userId
     * @return
     */
    public static Channel getChannel(long userId){
        return userIdChannelMap.get(userId);
    }
}
