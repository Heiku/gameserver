package com.ljh.gamedemo.module.pk.event;

import com.ljh.gamedemo.common.TaskType;
import com.ljh.gamedemo.module.event.BaseEvent;
import com.ljh.gamedemo.module.pk.event.base.PKEvent;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.task.bean.RoleTask;
import com.ljh.gamedemo.module.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * pk事件服务
 *
 * @Author: Heiku
 * @Date: 2019/9/10
 */

@Service
public class PKEventService {

    /**
     * 任务服务
     */
    @Autowired
    private TaskService taskService;


    @EventListener
    public void listenPKResult(PKEvent pkEvent){
        // 获取事件源
        BaseEvent baseEvent = (BaseEvent) pkEvent.getSource();

        // 获取事件参数信息
        Role role = baseEvent.getRole();

        // 判断玩家当前是否接下这个类型的任务
        RoleTask task = taskService.getRoleTaskByType(role, TaskType.PK_TASK);
        if (Objects.isNull(task)){
            return;
        }

        // 任务完成
        taskService.completeTask(role, task);
    }
}
