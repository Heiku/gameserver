package com.ljh.gamedemo.common;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * @Author: Heiku
 * @Date: 2019/9/25
 */
public class CommonExecutorConfig {

    /**
     * 队列的最大任务量
     */
    public static final Integer MAX_TASK = 1000;

    /**
     * 任务处理的时间间隔
     */
    public static final Integer DELAY_SEC = 1;


    /**
     * 自定义拒绝策略
     */
    public static final RejectedExecutionHandler defaultHandler =
            new ThreadPoolExecutor.AbortPolicy();
}
