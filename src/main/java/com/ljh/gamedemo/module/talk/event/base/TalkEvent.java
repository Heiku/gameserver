package com.ljh.gamedemo.module.talk.event.base;

import com.ljh.gamedemo.module.event.BaseEvent;
import org.springframework.context.ApplicationEvent;

/**
 * npc对话事件
 *
 * @Author: Heiku
 * @Date: 2019/9/9
 */
public class TalkEvent extends ApplicationEvent {

    public TalkEvent(BaseEvent baseEvent){
        super(baseEvent);
    }
}
