package com.ljh.gamedemo.module.creep.event.base;

import com.ljh.gamedemo.module.event.BaseEvent;
import org.springframework.context.ApplicationEvent;

/**
 * 攻击野怪事件
 *
 * @Author: Heiku
 * @Date: 2019/9/10
 */
public class AttackCreepEvent extends ApplicationEvent {

    public AttackCreepEvent(BaseEvent baseEvent){
        super(baseEvent);
    }
}
