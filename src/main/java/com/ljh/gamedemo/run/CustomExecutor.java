package com.ljh.gamedemo.run;

import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.concurrent.SingleThreadEventExecutor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Heiku
 * @Date: 2019/7/17
 */
public class CustomExecutor extends SingleThreadEventExecutor {

    public CustomExecutor(EventExecutorGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp) {
        super(parent, threadFactory, addTaskWakesUp);
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
    protected void addTask(Runnable task) {
        super.addTask(task);
    }

    @Override
    protected Runnable takeTask() {
        return super.takeTask();
    }

    @Override
    protected boolean removeTask(Runnable task) {
        return super.removeTask(task);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return super.scheduleAtFixedRate(command, initialDelay, period, unit);
    }
}
