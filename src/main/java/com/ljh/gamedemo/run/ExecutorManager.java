package com.ljh.gamedemo.run;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * @Author: Heiku
 * @Date: 2019/7/17
 */
public class ExecutorManager {

   /* private static ThreadFactory threadFactory;
    private static NormalAttackRunExecutor executors;

    static {
        threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("guava-thread-%d")
                .build();
    }*/

    // NormalAttackRunExecutor executors = new NormalAttackRunExecutor(null, threadFactory, true);

    private static final int corePoolSize = 10;
    private static final int maximumPoolSize = 20;
    private static final long keepAliveTime = 3;
    private static BlockingDeque<Runnable> workQueue = new LinkedBlockingDeque<>();
    private static ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("guava-thread-%d")
            .build();
    private static RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.DiscardOldestPolicy();

    private static ThreadPoolExecutor executors;

    static {
        executors = new ThreadPoolExecutor(
                corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS,
                workQueue, threadFactory, rejectedExecutionHandler
        );
    }


    public static ThreadPoolExecutor getExecutors() {
        return executors;
    }


}
