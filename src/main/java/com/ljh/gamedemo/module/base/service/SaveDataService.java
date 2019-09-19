package com.ljh.gamedemo.module.base.service;

import com.ljh.gamedemo.module.base.event.OfflineEvent;
import com.ljh.gamedemo.module.event.BaseEvent;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.user.local.LocalUserMap;
import com.ljh.gamedemo.util.SessionUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;


/**
 * 用于玩家离线时，保存玩家的数据，及数据的清理
 *
 */
@Service
@Slf4j
public class SaveDataService {


    /**
     * 事件发送者
     */
    @Autowired
    private ApplicationEventPublisher publisher;


    /**
     * 用于用户掉线时，保存用户的数据
     *
     * @param channel       channel
     */
    public void leaveSaveUserData(Channel channel){
        // 获取channel 绑定的 userId
        long userId = SessionUtil.getUserId(channel);
        if (userId <= 0){
            return;
        }

        // 获取玩家角色信息
        Role role = LocalUserMap.getUserRoleMap().get(userId);

        // 发送离线事件消息
        publisher.publishEvent(new OfflineEvent(new BaseEvent(role, 0L)));
    }
}
