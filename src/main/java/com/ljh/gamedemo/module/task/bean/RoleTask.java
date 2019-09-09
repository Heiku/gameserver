package com.ljh.gamedemo.module.task.bean;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * 玩家的任务记录
 *
 * @Author: Heiku
 * @Date: 2019/9/4
 */

@Builder
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
     * 进度 (1：未领取， 2：正在进行中， 3：完成任务， 4：放弃任务)
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
