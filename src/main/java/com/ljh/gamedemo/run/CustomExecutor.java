package com.ljh.gamedemo.run;

import io.netty.util.concurrent.*;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 自定义的单线程线程池，用于线程隔离下的任务执行
 *
 * @Author: Heiku
 * @Date: 2019/7/17
 */
public class CustomExecutor extends SingleThreadEventExecutor {

    public CustomExecutor(EventExecutorGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp, int maxPending, RejectedExecutionHandler rejectedHandler) {
        super(parent, threadFactory, addTaskWakesUp, maxPending, rejectedHandler);
    }

    @Override
    protected void run() {
        while (true){
            Runnable task = takeTask();
            if (task != null){
                task.run();
            }
        }
    }

    @Override
    public void addTask(Runnable task) {
        super.addTask(task);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(task);
    }

    @Override
    protected Runnable takeTask() {
        return super.takeTask();
    }

    @Override
    public boolean removeTask(Runnable task) {
        return super.removeTask(task);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return super.scheduleAtFixedRate(command, initialDelay, period, unit);
    }
}
