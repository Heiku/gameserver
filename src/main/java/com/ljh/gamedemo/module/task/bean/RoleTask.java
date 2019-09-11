package com.ljh.gamedemo.module.task.bean;

import lombok.Data;

import java.util.Date;

/**
 * 玩家的任务记录
 *
 * @Author: Heiku
 * @Date: 2019/9/4
 */
@Data
public class RoleTask {

    /**
     * id
     */
    private Long id;

    /**
     * 玩家id
     */
    private Long roleId;

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 进度 (1：未领取， 2：已领取任务，但还未进行， 3：任务完成，已经领取， 4：放弃任务， 5：任务完成，未领取)
     */
    private Integer progress;

    /**
     * 领取任务时间
     */
    private Date createTime;

    /**
     * 修改任务的时间
     */
    private Date modifyTime;
}
