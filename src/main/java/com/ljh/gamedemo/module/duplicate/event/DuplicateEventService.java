package com.ljh.gamedemo.module.duplicate.event;

import com.ljh.gamedemo.module.base.event.OfflineEvent;
import com.ljh.gamedemo.module.duplicate.service.DuplicateService;
import com.ljh.gamedemo.module.event.BaseEvent;
import com.ljh.gamedemo.module.role.bean.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 副本事件服务
 *
 * @Author: Heiku
 * @Date: 2019/9/19
 */

@Service
public class DuplicateEventService {

    /**
     * 副本服务
     */
    @Autowired
    private DuplicateService duplicateService;


    /**
     * 监听离开副本
     *
     * @param offlineEvent      离线事件
     */
    @Async
    @EventListener
    public void listenLeaveDuplicate(OfflineEvent offlineEvent){
        // 获取事件源
        BaseEvent baseEvent = (BaseEvent) offlineEvent.getSource();

        // 获取事件参数信息
        Role role = baseEvent.getRole();

        // 如果在挑战副本，移出攻击目标队列中
        duplicateService.removeAttackedQueue(role);
    }
}
