package com.ljh.gamedemo.run.manager;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ljh.gamedemo.run.db.SendEmailRun;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @Author: Heiku
 * @Date: 2019/8/8
 *
 * 邮件发送线程池
 */

@Slf4j
public class SendEmailManager {

    private static Integer MAX_TASK = 100;

    private static Integer DELAY_SEC = 5;

    private static BlockingQueue<SendEmailRun> queue = new LinkedBlockingQueue<>(MAX_TASK);

    private static ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setDaemon(false)
            .setNameFormat("send-email-thread-%d")
            .build();

    private static ScheduledExecutorService executorService;

    private static Runnable task = null;

    static {
        executorService = Executors.newScheduledThreadPool(1, threadFactory);
    }

    public static void addQueue(SendEmailRun run){
        queue.offer(run);
    }


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
