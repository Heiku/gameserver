package com.ljh.gamedemo.module.task.cache;

import com.google.common.collect.Maps;
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

    public static Map<Integer, List<Task>> getTypeTaskMap() {
        return typeTaskMap;
    }

    public static Map<Long, Task> getIdTaskMap() {
        return idTaskMap;
    }
}
