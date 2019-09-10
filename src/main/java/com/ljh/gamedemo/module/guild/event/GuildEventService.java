package com.ljh.gamedemo.module.guild.event;

import com.ljh.gamedemo.common.TaskType;
import com.ljh.gamedemo.module.event.BaseEvent;
import com.ljh.gamedemo.module.guild.event.base.GuildEvent;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.task.bean.RoleTask;
import com.ljh.gamedemo.module.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 公会事件订阅者
 *
 * @Author: Heiku
 * @Date: 2019/9/10
 */

@Service
public class GuildEventService {

    /**
     * 任务服务
     */
    @Autowired
    private TaskService taskService;

    /**
     * 监听玩家公会事件，判断完成工会任务
     *
     * @param guildEvent        公会事件
     */
    @EventListener
    public void listenJoinEvent(GuildEvent guildEvent){

        // 获取事件源
        BaseEvent baseEvent = (BaseEvent) guildEvent.getSource();

        // 获取事件参数信息
        Role role = baseEvent.getRole();
        long desId = baseEvent.getDesId();

        // 判断玩家当前是否接下这个类型的任务
        RoleTask task = taskService.getRoleTaskByType(role, TaskType.GUILD_JOIN_TASK);
        if (Objects.isNull(task)){
            return;
        }

        // 加入成功
        if (desId != 0){
            taskService.completeTask(role, task);
        }
    }
}
