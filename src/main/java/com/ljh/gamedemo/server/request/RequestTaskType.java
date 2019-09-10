package com.ljh.gamedemo.server.request;

/**
 * @Author: Heiku
 * @Date: 2019/9/6
 */
public class RequestTaskType {

    /**
     * 所有任务
     */
    public static final int TASK_ALL = 0;

    /**
     * 当前已经接的任务
     */
    public static final int TASK_STATE = 1;

    /**
     * 接收任务
     */
    public static final int TASK_RECEIVE = 2;

    /**
     * 放弃任务
     */
    public static final int TASK_GIVE_UP = 3;

    /**
     * 提交任务
     */
    public static final int TASK_SUBMIT = 5;
}
