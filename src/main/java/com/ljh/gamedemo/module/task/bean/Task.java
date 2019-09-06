package com.ljh.gamedemo.module.task.bean;

import com.ljh.gamedemo.module.email.bean.EmailGoods;
import com.ljh.gamedemo.module.goods.bean.Goods;
import lombok.Data;

import java.util.List;

/**
 * 任务实体类
 *
 * @Author: Heiku
 * @Date: 2019/9/4
 */

@Data
public class Task {

    /**
     * id
     */
    private Long id;

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 任务名
     */
    private String name;

    /**
     * 任务类型
     */
    private Integer type;

    /**
     * 目标id
     */
    private Long desId;

    /**
     * 目标数
     */
    private Integer goal;

    /**
     * 次数
     */
    private Integer times;

    /**
     * 奖励金币值
     */
    private Integer gold;

    /**
     * 物品奖励
     */
    private List<EmailGoods> goods;

    /**
     * 子任务
     */
    private List<Task> tasks;

    /**
     * 任务详情
     */
    private String desc;
}
