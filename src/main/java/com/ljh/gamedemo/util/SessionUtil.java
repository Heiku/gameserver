package com.ljh.gamedemo.util;

import com.ljh.gamedemo.attribute.Attributes;
import com.ljh.gamedemo.module.base.cache.ChannelCache;
import io.netty.channel.Channel;


/**
 * Session工具类
 */
public class SessionUtil {

    /**
     * userId 绑定channel
     *
     * @param userId        userId
     * @param channel       channel
     */
    public static void bindSession(Long userId, Channel channel){
        if (ChannelCache.getUserIdChannelMap().containsKey(userId)){
            return;
        }
        // 绑定用户channel
        ChannelCache.getUserIdChannelMap().put(userId, channel);
        channel.attr(Attributes.USER_ID).set(userId);

        // 添加到玩家 group 中
        ChannelCache.getAllRoleGroup().add(channel);
    }

    /**
     * 接触绑定
     *
     * @param channel   channel
     */
    public static void unBindSession(Channel channel){
        // 移除玩家映射channel
        long userId = getUserId(channel);
        ChannelCache.getUserIdChannelMap().remove(userId);

        // 移除channelGroup
        ChannelCache.getAllRoleGroup().remove(channel);

        // 清空玩家attr
        channel.attr(Attributes.USER_ID).set(0L);
    }


    /**
     * 获取玩家id
     *
     * @param channel       channel
     * @return              userId
     */
    public static long getUserId(Channel channel){
        if (channel.attr(Attributes.USER_ID).get() == null){
            return 0;
        }
        return channel.attr(Attributes.USER_ID).get();
    }
}
