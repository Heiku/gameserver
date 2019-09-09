package com.ljh.gamedemo.module.talk.event;

import com.ljh.gamedemo.module.event.BaseEvent;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.talk.event.base.TalkEvent;
import org.springframework.context.event.EventListener;

/**
 * npc对话事件订阅者
 *
 * @Author: Heiku
 * @Date: 2019/9/9
 */
public class TalkEventService {


    /**
     * 监听npc对话事件，判断是否完成任务
     *
     * @param talkEvent
     */
    @EventListener
    public void listenTalkEvent(TalkEvent talkEvent){
        BaseEvent baseEvent = (BaseEvent) talkEvent.getSource();
        Role role = baseEvent.getRole();
    }
}
