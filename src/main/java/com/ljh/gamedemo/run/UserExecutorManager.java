package com.ljh.gamedemo.run;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ThreadFactory;

/**
 * @Author: Heiku
 * @Date: 2019/7/18
 */

@Slf4j
public class UserExecutorManager {

    // 最大的用户线程池数，后期可拓展
    private static final Integer MAX_USER_THREAD =  20;

    private static ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("user-thread-%d")
            .build();

    // 每一个用户对应一个UserSingleExecutor
    private static CustomExecutor[] executors = new CustomExecutor[20];

    // 用于记录用户与用户线程绑定 <userId, CustomExecutor>
    private static Map<Long, CustomExecutor> userExecutorMap = Maps.newConcurrentMap();

    // 用于记录当前的线程池[]的可用状态
    private static int[] idleArr = new int[20];

    // init，为每一个用户线程池进行初始化
    static {
        CustomExecutor executor;
        for (int i = 0; i < MAX_USER_THREAD; i++){
            executors[i] = new CustomExecutor(null, threadFactory, true);
            executor = executors[i];
            executors[i].submit(new ExecutorInit(executor));
        }
        log.info("User Executor init has done !");
    }


    /**
     * 根据用户的id 绑定用户线程池
     *
     * @param userId
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
     * @param userId
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
     * @param userId
     * @param task
     * @return
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
     * 用户线程池中移除任务，得保证 task 是同一个 Object
     *
     * @param userId
     * @param task
     * @return
     */
    public static boolean removeUserTask(long userId, Runnable task){
        if (!userExecutorMap.containsKey(userId)){
            return false;
        }
        CustomExecutor executor = userExecutorMap.get(userId);
        executor.removeTask(task);

        return true;
    }

}


@Slf4j
class ExecutorInit implements Runnable{

    private CustomExecutor executor;

    ExecutorInit(CustomExecutor executor){
        this.executor = executor;
    }

    @Override
    public void run() {
        log.info(executor.toString());
    }
}