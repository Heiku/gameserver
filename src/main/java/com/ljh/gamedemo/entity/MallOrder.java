package com.ljh.gamedemo.entity;

import lombok.Data;

import java.util.Date;

/**
 * @Author: Heiku
 * @Date: 2019/8/8
 *
 * 商品订单表
 */

@Data
public class MallOrder {

    private Long id;

    /**
     * 购买人
     */
    private Long roleId;

    /**
     * 商品id
     */
    private Long cid;

    /**
     * 购买数量
     */
    private Integer num;

    /**
     * 消费金额
     */
    private Integer cost;

    /**
     * 购买时间
     */
    private Date createTime;
}
