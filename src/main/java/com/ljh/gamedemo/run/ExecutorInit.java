package com.ljh.gamedemo.run;

import lombok.extern.slf4j.Slf4j;


/**
 * 线程池启动输出
 */
@Slf4j
public class ExecutorInit implements Runnable{

    /**
     * 执行线程池
     */
    private CustomExecutor executor;

    public ExecutorInit(CustomExecutor executor){
        this.executor = executor;
    }

    @Override
    public void run() {
        log.info(executor.toString());
    }
}