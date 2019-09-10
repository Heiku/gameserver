package com.ljh.gamedemo.common;

/**
 * 任务的状态
 *
 * @Author: Heiku
 * @Date: 2019/9/2
 */
public class TaskState {

    /**
     * 未领取任务
     */
    public static final int UN_RECEIVE_TASK = 1;

    /**
     * 已领取任务，但还未进行
     */
    public static final int RECEIVE_TASK = 2;

    /**
     * 任务完成，已经领取
     */
    public static final int TASK_ALL_FINISH = 3;

    /**
     * 任务完成，未领取
     */
    public static final int TASK_FINISH = 5;

    /**
     * 放弃任务
     */
    public static final int TASK_DISCARD = 4;
}
