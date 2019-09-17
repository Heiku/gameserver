package com.ljh.gamedemo.run.manager;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ljh.gamedemo.run.CustomExecutor;
import com.ljh.gamedemo.run.clean.CleanUpCacheRun;
import io.netty.util.concurrent.RejectedExecutionHandlers;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 清除缓存管理器
 *
 * @Author: Heiku
 * @Date: 2019/8/30
 */
public class CleanUpCacheManager {

    private static ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("cleanCache-thread-%d")
            .build();

    private static CustomExecutor executors;

    static {
        executors = new CustomExecutor(null, threadFactory, true,
                Integer.MAX_VALUE, RejectedExecutionHandlers.reject());

    }

    public static void run(){
        executors.scheduleAtFixedRate(new CleanUpCacheRun(), 0, 2, TimeUnit.SECONDS);
    }
}
