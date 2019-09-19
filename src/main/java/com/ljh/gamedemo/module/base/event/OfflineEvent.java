package com.ljh.gamedemo.module.base.event;

import com.ljh.gamedemo.module.event.BaseEvent;
import org.springframework.context.ApplicationEvent;

/**
 * 离线事件
 *
 * @Author: Heiku
 * @Date: 2019/9/19
 */
public class OfflineEvent extends ApplicationEvent {

    public OfflineEvent(BaseEvent baseEvent){
        super(baseEvent);
    }
}
