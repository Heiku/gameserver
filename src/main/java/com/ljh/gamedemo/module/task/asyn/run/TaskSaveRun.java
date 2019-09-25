package com.ljh.gamedemo.module.task.asyn.run;

import com.ljh.gamedemo.module.task.bean.RoleTask;
import com.ljh.gamedemo.module.task.dao.TaskDao;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import static com.ljh.gamedemo.common.CommonDBType.INSERT;
import static com.ljh.gamedemo.common.CommonDBType.UPDATE;

/**
 * 任务数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/9/25
 */

@Slf4j
public class TaskSaveRun implements Runnable {

    /**
     * 任务信息
     */
    private RoleTask task;

    /**
     * 操作类型
     */
    private int type;

    /**
     * TaskDao
     */
    private TaskDao dao = SpringUtil.getBean(TaskDao.class);

    public TaskSaveRun(RoleTask task, int type) {
        this.task = task;
        this.type = type;
    }

    @Override
    public void run() {
        int n;

        switch (type){
            case INSERT:
                n = dao.insertTask(task);
                log.info("insert into task, affected rows: " + n);
                break;

            case UPDATE:
                n = dao.updateTask(task);
                log.info("update task, affected rows: " + n);
                break;
        }
    }
}
