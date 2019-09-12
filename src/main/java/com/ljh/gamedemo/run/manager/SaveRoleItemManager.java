package com.ljh.gamedemo.run.manager;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ljh.gamedemo.run.db.SaveRoleItemRun;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * 定时从队列中取出更新数据库背包数据的任务，并执行
 *
 * @Author: Heiku
 * @Date: 2019/7/22
 */

@Slf4j
public class SaveRoleItemManager {

    /**
     * 最大任务数量
     */
    private static Integer MAX_TASK = 100;

    /**
     * 任务处理间隔
     */
    private static Integer DELAY_SEC = 1;

    /**
     * 任务队列
     */
    private static BlockingQueue<SaveRoleItemRun> queue = new LinkedBlockingQueue<>(MAX_TASK);

    /**
     * 线程工厂
     */
    private static ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setDaemon(false)
            .setNameFormat("save-roleitem-thread-%d")
            .build();

    /**
     * 线程池
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
     * 将任务添加到队列中
     *
     * @param run       任务信息
     */
   public static void addQueue(SaveRoleItemRun run){
        queue.offer(run);
   }


    /**
     * 任务运行
     */
    public static void run() {
        new Thread(() -> {
            while (true){
                task = queue.poll();
                if (task != null) {
                    executorService.schedule(task, DELAY_SEC, TimeUnit.SECONDS);
                    log.info("完成更新role_objects表数据");
                }
            }
        }).start();
    }
}
