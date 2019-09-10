package com.ljh.gamedemo.module.task.cache;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.module.task.bean.RoleTask;
import com.ljh.gamedemo.module.task.bean.Task;

import java.util.List;
import java.util.Map;

/**
 * 本地缓存任务信息
 *
 * @Author: Heiku
 * @Date: 2019/9/6
 */
public class TaskCache {

    /**
     * 所有的任务map (taskId, task)
     */
    private static Map<Long, Task> idTaskMap = Maps.newHashMap();

    /**
     * 任务类型map (typeId, List<task>)
     */
    private static Map<Integer, List<Task>> typeTaskMap = Maps.newHashMap();


    /**
     * 玩家正在进行中的任务
     */
    private static Map<Long, List<RoleTask>> roleProcessTaskMap = Maps.newConcurrentMap();

    /**
     * 玩家已经完成的任务
     */
    private static Map<Long, List<RoleTask>> roleDoneTaskMap = Maps.newConcurrentMap();


    public static Map<Integer, List<Task>> getTypeTaskMap() {
        return typeTaskMap;
    }

    public static Map<Long, Task> getIdTaskMap() {
        return idTaskMap;
    }

    public static Map<Long, List<RoleTask>> getRoleDoneTaskMap() {
        return roleDoneTaskMap;
    }

    public static Map<Long, List<RoleTask>> getRoleProcessTaskMap() {
        return roleProcessTaskMap;
    }
}
