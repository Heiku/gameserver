package com.ljh.gamedemo.run;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ljh.gamedemo.local.LocalSiteMap;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Heiku
 * @Date: 2019/7/19
 */

@Slf4j
public class SiteCreepExecutorManager {

    // 最大的场景线程池数，后期可拓展
    private static final Integer MAX_SITE_CREEP_NUM =  12;

    private static ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("creep-thread-%d")
            .build();

    // 每一个场景对应一个场景野怪集合
    private static CustomExecutor[] executors = new CustomExecutor[12];

    // 用于记录用户与用户线程绑定 <userId, CustomExecutor>
    public static Map<Integer, CustomExecutor> siteCreepExecutorMap = Maps.newConcurrentMap();

    // 用于记录当前的线程池[]的可用状态
    private static int[] idleArr = new int[MAX_SITE_CREEP_NUM];

    // init，为每一个用户线程池进行初始化
    static {
        CustomExecutor executor;
        for (int i = 0; i < MAX_SITE_CREEP_NUM; i++){
            executors[i] = new CustomExecutor(null, threadFactory, true, Integer.MAX_VALUE, RejectedExecutionHandlers.reject());
            executor = executors[i];
            executors[i].submit(new ExecutorInit(executor));
        }
        log.info("Creep Executor init has done !");
    }


    /**
     * 根据野怪的id 绑定用户线程池
     *
     * @param siteId
     */
    public static void bindSiteExecutor(int siteId) {
        if (siteCreepExecutorMap.containsKey(siteId)){
            return;
        }
        for (int i = 0; i < idleArr.length; i++) {
            if (idleArr[i] == 0) {
                idleArr[i] = 1;
                siteCreepExecutorMap.put(siteId, executors[i]);
                break;
            }
        }
    }

    /**
     * 解除绑定用户线程池
     *
     * @param siteId
     */
    public static void unBindSiteExecutor(int siteId){
        CustomExecutor executor = siteCreepExecutorMap.get(siteId);
        if (executor != null){
            for (int i = 0; i < executors.length; i++){
                if (executors[i] == executor){
                    idleArr[i] = 0;
                    siteCreepExecutorMap.remove(siteId);
                }
            }
        }
    }


    /**
     * 往用户线程池中添加任务
     *
     * @param siteId
     * @param task
     * @return
     */
    public static boolean addCreepTask(int siteId, Runnable task){
        if (!siteCreepExecutorMap.containsKey(siteId)){
            return false;
        }
        CustomExecutor executor = siteCreepExecutorMap.get(siteId);
        executor.addTask(task);

        return true;
    }


    /**
     * 用户线程池中移除任务，得保证 task 是同一个 Object
     *
     * @param siteId
     * @param task
     * @return
     */
    public static boolean removeUserTask(int siteId, Runnable task){
        if (!siteCreepExecutorMap.containsKey(siteId)){
            return false;
        }
        CustomExecutor executor = siteCreepExecutorMap.get(siteId);
        executor.removeTask(task);

        return true;
    }

    /**
     * 通过siteId 获取对应的线程池
     *
     * @param siteId
     * @return
     */
    public static CustomExecutor getExecutor(int siteId){
        return siteCreepExecutorMap.get(siteId);
    }
}