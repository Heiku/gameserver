package com.ljh.gamedemo.module.role.asyn;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

import static com.ljh.gamedemo.common.CommonExecutorConfig.MAX_TASK;
import static com.ljh.gamedemo.common.CommonExecutorConfig.defaultHandler;

/**
 * 玩家线程池
 *
 * @Author: Heiku
 * @Date: 2019/9/25
 */
public class RoleSaveManager {
    /**
     * 线程池
     */
    private static ExecutorService executorService;

    /**
     * 线程工厂
     */
    private static ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setDaemon(false)
            .setNameFormat("save-role-thread-%d")
            .build();

    /**
     * 任务队列
     */
    private static BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(MAX_TASK);


    static {
        executorService = new ThreadPoolExecutor(
                2, 5 , 1, TimeUnit.SECONDS, queue, threadFactory, defaultHandler
        );
    }


    /**
     * 获取对应的线程池
     *
     * @return  线程池
     */
    public static ExecutorService getExecutorService() {
        return executorService;
    }
}
