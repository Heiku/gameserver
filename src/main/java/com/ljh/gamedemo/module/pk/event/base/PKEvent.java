package com.ljh.gamedemo.module.pk.event.base;

import com.ljh.gamedemo.module.event.BaseEvent;
import org.springframework.context.ApplicationEvent;

/**
 * PK 事件
 *
 * @Author: Heiku
 * @Date: 2019/9/10
 */
public class PKEvent extends ApplicationEvent {

    public PKEvent(BaseEvent baseEvent){
        super(baseEvent);
    }
}
