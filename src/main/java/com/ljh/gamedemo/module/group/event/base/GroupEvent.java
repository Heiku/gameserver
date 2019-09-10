package com.ljh.gamedemo.module.group.event.base;

import com.ljh.gamedemo.module.event.BaseEvent;
import org.springframework.context.ApplicationEvent;

/**
 * 组队事件
 *
 * @Author: Heiku
 * @Date: 2019/9/10
 */
public class GroupEvent extends ApplicationEvent {

    public GroupEvent(BaseEvent baseEvent){
        super(baseEvent);
    }
}
