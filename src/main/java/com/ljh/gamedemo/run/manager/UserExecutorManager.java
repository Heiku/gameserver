package com.ljh.gamedemo.run.manager;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ljh.gamedemo.run.CustomExecutor;
import com.ljh.gamedemo.run.ExecutorInit;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadFactory;

/**
 * 用户线程池管理器
 *
 * @Author: Heiku
 * @Date: 2019/7/18
 */

@Slf4j
public class UserExecutorManager {

    /**
     * 最大的用户线程池数，后期可拓展
     */
    private static final Integer MAX_USER_THREAD =  10;

    /**
     * 线程工厂
     */
    private static ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("user-thread-%d")
            .build();


    /**
     * 每一个用户对应一个UserSingleExecutor
     */
    private static CustomExecutor[] executors = new CustomExecutor[10];


    /**
     * 用于记录用户与用户线程绑定 <userId, CustomExecutor>
     */
    private static Map<Long, CustomExecutor> userExecutorMap = Maps.newConcurrentMap();


    /**
     * 用于记录当前的线程池数组的可用状态
     */
    private static int[] idleArr = new int[20];


    static {
        CustomExecutor executor;
        for (int i = 0; i < MAX_USER_THREAD; i++){
            executors[i] = new CustomExecutor(null, threadFactory, true, Integer.MAX_VALUE, RejectedExecutionHandlers.reject());
            executor = executors[i];
            executors[i].submit(new ExecutorInit(executor));
        }
        log.info("User Executor init has done !");
    }


    /**
     * 根据用户的id 绑定用户线程池
     *
     * @param userId        用户id
     */
    public synchronized static void bindUserExecutor(long userId) {
        if (userExecutorMap.containsKey(userId)){
            return;
        }
        for (int i = 0; i < idleArr.length; i++) {
            if (idleArr[i] == 0) {
                idleArr[i] = 1;
                userExecutorMap.put(userId, executors[i]);

                break;
            }
        }
    }

    /**
     * 解除绑定用户线程池
     *
     * @param userId        用户id
     */
    public synchronized static void unBindUserExecutor(long userId){
        CustomExecutor executor = userExecutorMap.get(userId);
        if (executor != null){
            for (int i = 0; i < executors.length; i++){
                if (executors[i] == executor){
                    idleArr[i] = 0;
                    userExecutorMap.remove(userId);
                }
            }
        }
    }


    /**
     * 往用户线程池中添加任务
     *
     * @param userId        用户id
     * @param task          任务信息
     * @return              是否添加成功
     */
    public static boolean addUserTask(long userId, Runnable task){
        if (!userExecutorMap.containsKey(userId)){
            return false;
        }
        CustomExecutor executor = userExecutorMap.get(userId);
        executor.addTask(task);

        return true;
    }

    /**
     * 添加返回的任务
     *
     * @param userId        用户信息
     * @param task          任务信息
     * @return              任务 future
     */
    public static <T> Future<T> addUserCallableTask(long userId, Callable<T> task){
        if (!userExecutorMap.containsKey(userId)){
            return null;
        }
        CustomExecutor executor = userExecutorMap.get(userId);
        return executor.submit(task);
    }


    /**
     * 用户线程池中移除任务，得保证 task 是同一个 Object
     *
     * @param userId        用户id
     * @param task          任务信息
     * @return              是否移除成功
     */
    public static boolean removeUserTask(long userId, Runnable task){
        if (!userExecutorMap.containsKey(userId)){
            return false;
        }
        CustomExecutor executor = userExecutorMap.get(userId);
        executor.removeTask(task);

        return true;
    }

    public static CustomExecutor getUserExecutor(long userId){
        return userExecutorMap.get(userId);
    }

}
