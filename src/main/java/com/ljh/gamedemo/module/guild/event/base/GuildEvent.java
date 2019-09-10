package com.ljh.gamedemo.module.guild.event.base;

import com.ljh.gamedemo.module.event.BaseEvent;
import org.springframework.context.ApplicationEvent;

/**
 * 公会基础事件
 *
 * @Author: Heiku
 * @Date: 2019/9/10
 */
public class GuildEvent extends ApplicationEvent {

    public GuildEvent(BaseEvent baseEvent){
        super(baseEvent);
    }
}
