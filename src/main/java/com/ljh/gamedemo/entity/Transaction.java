package com.ljh.gamedemo.entity;

import lombok.Data;

import java.util.Date;

/**
 * 面对面交易实体类
 *
 * @Author: Heiku
 * @Date: 2019/8/22
 */

@Data
public class Transaction {

    /**
     * 交易id
     */
    private Long id;

    /**
     * 交易发起人
     */
    private Long promoter;

    /**
     * 交易接收方
     */
    private Long receiver;

    /**
     * 交易物品id
     */
    private Long goodsId;

    /**
     * 交易数量
     */
    private Integer num;

    /**
     * 交易金额
     */
    private Integer amount;

    /**
     * 判断交易是否成功 0: 失败， 1：成功
     */
    private Integer success;

    /**
     * 发起交易的时间
     */
    private Date createTime;

    /**
     * 结束交易的时间
     */
    private Date modifyTime;
}
