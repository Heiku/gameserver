package com.ljh.gamedemo.run.manager;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ljh.gamedemo.run.db.SaveRoleItemRun;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @Author: Heiku
 * @Date: 2019/7/22
 *
 * 定时从队列中取出更新数据库背包数据的任务，并执行
 */

@Slf4j
public class SaveRoleItemManager {

    private static Integer MAX_TASK = 100;

    private static Integer DELAY_SEC = 1;

    private static BlockingQueue<SaveRoleItemRun> queue = new LinkedBlockingQueue<>(MAX_TASK);

    private static ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setDaemon(false)
            .setNameFormat("save-roleitem-thread-%d")
            .build();

    private static ScheduledExecutorService executorService;

    private static Runnable task = null;

    static {
        executorService = Executors.newScheduledThreadPool(1, threadFactory);
    }

   public static void addQueue(SaveRoleItemRun run){
        queue.offer(run);
   }


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
