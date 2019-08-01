package com.ljh.gamedemo.run;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ThreadFactory;

/**
 * @Author: Heiku
 * @Date: 2019/7/30
 *
 * 用户每创建一个副本，都会构建一个线程去处理对应的任务
 */

@Slf4j
public class DuplicateManager {

    private static final Integer MAX_DUP_NUM = 50;

    // 用于记录 挑战者（个人、队伍）与 副本线程进行绑定 <roleId/teamId, CustomExecutor>
    private static Map<Long, CustomExecutor> dupExecutorMap = Maps.newConcurrentMap();

    private static ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("duplicate-thread-%d")
            .build();

    private static int[] idleArr = new int[MAX_DUP_NUM];

    private static CustomExecutor[] executors = new CustomExecutor[MAX_DUP_NUM];


    /**
     * 副本线程添加新的任务
     *
     * @param id
     * @param task
     * @return
     */
    public static boolean addDupTask(long id, Runnable task){
        if (!dupExecutorMap.containsKey(id)){
            return false;
        }

        CustomExecutor executor = dupExecutorMap.get(id);
        executor.addTask(task);

        return true;
    }

    /**
     * 为临时副本绑定线程池
     *
     * @param id
     */
    public synchronized static void bindDupExecutor(long id) {
        if (dupExecutorMap.containsKey(id)){
            return;
        }
        for (int i = 0; i < idleArr.length; i++) {
            if (idleArr[i] == 0) {
                idleArr[i] = 1;

                // 当前的先吃还没初始化
                if (executors[i] == null){
                    executors[i] = startExecutor(executors[i]);
                }

                // map存储对应的关系
                dupExecutorMap.put(id, executors[i]);
                break;
            }
        }
    }


    /**
     * 解除绑定线程池
     *
     * @param id
     */
    public synchronized static void unBindDupExecutor(long id){
        CustomExecutor executor = dupExecutorMap.get(id);
        if (executor != null){
            for (int i = 0; i < executors.length; i++){
                if (executors[i] == executor){

                    // 停止任务
                    executor.shutdownGracefully();
                    executor = null;

                    idleArr[i] = 0;
                    dupExecutorMap.remove(id);
                }
            }
        }
    }


    /**
     * 获取对应副本的线程池
     *
     * @param id
     * @return
     */
    public static CustomExecutor getExecutor(long id){
        return dupExecutorMap.get(id);
    }


    private static CustomExecutor startExecutor(CustomExecutor executor){
        CustomExecutor e = new CustomExecutor(null, threadFactory, true, Integer.MAX_VALUE, RejectedExecutionHandlers.reject());
        e.submit(new ExecutorInit(e));

        return e;
    }
}
