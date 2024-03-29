package com.ljh.gamedemo.module.talk.event;

import com.ljh.gamedemo.common.TaskType;
import com.ljh.gamedemo.module.event.BaseEvent;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.talk.event.base.TalkEvent;
import com.ljh.gamedemo.module.task.bean.RoleTask;
import com.ljh.gamedemo.module.task.bean.Task;
import com.ljh.gamedemo.module.task.cache.TaskCache;
import com.ljh.gamedemo.module.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * npc对话事件订阅者
 *
 * @Author: Heiku
 * @Date: 2019/9/9
 */
@Service
public class TalkEventService {

    /**
     * 任务服务
     */
    @Autowired
    private TaskService taskService;


    /**
     * 监听npc对话事件，判断是否完成任务
     *
     * @param talkEvent
     */
    @EventListener
    public void listenTalkEvent(TalkEvent talkEvent){
        // 获取事件源
        BaseEvent baseEvent = (BaseEvent) talkEvent.getSource();

        // 获取事件参数信息
        Role role = baseEvent.getRole();
        long desId = baseEvent.getDesId();

        // 判断玩家当前是否接下这个类型的任务
        RoleTask task = taskService.getRoleTaskByType(role, TaskType.NPC_TALK_TASK);
        if (Objects.isNull(task)){
            return;
        }

        // 判断npc对话的目标是否相同
        Task t = TaskCache.getIdTaskMap().get(task.getTaskId());
        if (t.getDesId() == desId){

            // 任务完成
            taskService.completeTask(role, task);
        }
    }
}
