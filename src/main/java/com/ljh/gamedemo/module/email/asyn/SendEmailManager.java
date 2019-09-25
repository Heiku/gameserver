package com.ljh.gamedemo.module.email.asyn;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ljh.gamedemo.module.email.asyn.run.SendEmailRun;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * 邮件发送线程池
 *
 * @Author: Heiku
 * @Date: 2019/8/8
 */

@Slf4j
public class SendEmailManager {

    /**
     * 最大任务量
     */
    private static Integer MAX_TASK = 100;

    /**
     * 任务执行间隔
     */
    private static Integer DELAY_SEC = 5;

    /**
     * 工作队列
     */
    private static BlockingQueue<SendEmailRun> queue = new LinkedBlockingQueue<>(MAX_TASK);

    /**
     * 线程工厂
     */
    private static ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setDaemon(false)
            .setNameFormat("send-email-thread-%d")
            .build();

    /**
     * 定时线程池
     */
    private static ScheduledExecutorService executorService;

    /**
     * 任务信息
     */
    private static Runnable task = null;

    static {
        executorService = Executors.newScheduledThreadPool(1, threadFactory);
    }

    /**
     * 添加到工作队列中
     *
     * @param run       发送邮件任务
     */
    public static void addQueue(SendEmailRun run){
        queue.offer(run);
    }


    /**
     * 任务执行
     */
    public static void run(){
        new Thread(() -> {
            while (true){
                task = queue.poll();
                if (task != null) {
                    executorService.schedule(task, DELAY_SEC, TimeUnit.SECONDS);
                    log.info("成功发送邮件数据");
                }
            }
        }).start();
    }
}
