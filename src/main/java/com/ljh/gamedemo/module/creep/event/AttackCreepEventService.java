package com.ljh.gamedemo.module.creep.event;

import com.ljh.gamedemo.common.TaskType;
import com.ljh.gamedemo.module.creep.event.base.AttackCreepEvent;
import com.ljh.gamedemo.module.creep.local.AttackCreepEventCache;
import com.ljh.gamedemo.module.event.BaseEvent;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.task.bean.RoleTask;
import com.ljh.gamedemo.module.task.bean.Task;
import com.ljh.gamedemo.module.task.cache.TaskCache;
import com.ljh.gamedemo.module.task.service.TaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 攻击野怪事件订阅者
 *
 * @Author: Heiku
 * @Date: 2019/9/10
 */

@Service
public class AttackCreepEventService {


    /**
     * 任务服务
     */
    @Autowired
    private TaskService taskService;


    /**
     * 监听玩家攻击野怪事件
     *
     * @param creepEvent        攻击野怪事件
     */
    @EventListener
    public void listenAttackCreepEvent(AttackCreepEvent creepEvent){
        // 获取事件源
        BaseEvent baseEvent = (BaseEvent) creepEvent.getSource();

        // 获取事件参数信息
        Role role = baseEvent.getRole();
        long desId = baseEvent.getDesId();

        // 判断玩家当前是否接下这个类型的任务
        RoleTask task = taskService.getRoleTaskByType(role, TaskType.NPC_TALK_TASK);
        if (Objects.isNull(task) || TaskCache.getIdTaskMap().get(task.getTaskId()).getDesId() != desId){
            return;
        }

        // 存在对应的任务
        long id = role.getRoleId() + desId;

        // 获取正在进行的任务信息
        Task doing = AttackCreepEventCache.getCache().getIfPresent(id);
        // 第一次进行任务
        if (Objects.isNull(doing)){
            // 任务模板
            Task model = TaskCache.getIdTaskMap().get(task.getTaskId());
            doing = new Task();
            BeanUtils.copyProperties(model, doing);
            doing.setId(task.getId());
        }

        // 更新野怪的攻击数量
        doing.setGoal(doing.getGoal() - 1);
        if (doing.getGoal() == 0){
            taskService.completeTask(role, task);
            AttackCreepEventCache.getCache().invalidate(id);
            return;
        }

        // 否则更新对应cache
        AttackCreepEventCache.getCache().put(id, doing);
    }
}
