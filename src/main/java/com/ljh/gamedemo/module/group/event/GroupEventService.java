package com.ljh.gamedemo.module.group.event;

import com.ljh.gamedemo.common.TaskType;
import com.ljh.gamedemo.module.base.event.OfflineEvent;
import com.ljh.gamedemo.module.event.BaseEvent;
import com.ljh.gamedemo.module.group.event.base.GroupEvent;
import com.ljh.gamedemo.module.group.service.GroupService;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.task.bean.RoleTask;
import com.ljh.gamedemo.module.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 组队事件订阅者
 *
 * @Author: Heiku
 * @Date: 2019/9/10
 */

@Service
public class GroupEventService {

    /**
     * 任务服务
     */
    @Autowired
    private TaskService taskService;

    /**
     * 组队服务
     */
    @Autowired
    private GroupService groupService;


    /**
     * 监听加入组队的任务
     *
     * @param groupEvent        组队事件
     */
    @EventListener
    public void listenJoinGroupEvent(GroupEvent groupEvent){
        // 获取事件源
        BaseEvent baseEvent = (BaseEvent) groupEvent.getSource();

        // 获取事件参数信息
        Role role = baseEvent.getRole();

        // 判断玩家当前是否接下这个类型的任务
        RoleTask task = taskService.getRoleTaskByType(role, TaskType.GROUP_JOIN_TASK);
        if (Objects.isNull(task)){
            return;
        }

        long desId = baseEvent.getDesId();
        // 加入成功
        if (desId != 0){
            taskService.completeTask(role, task);
        }
    }



    /**
     * 监听玩家离线退出队伍事件
     *
     * @param offlineEvent      离线事件
     */
    @Async
    @EventListener
    public void listenLeaveGroupEvent(OfflineEvent offlineEvent){
        // 获取事件源
        BaseEvent baseEvent = (BaseEvent) offlineEvent.getSource();

        // 获取事件参数信息
        Role role = baseEvent.getRole();

        // 退出队伍
        groupService.removeGroup(role);
    }
}
