package com.ljh.gamedemo.entity;

import lombok.Data;

import java.util.Date;

/**
 * 商品交易实体类
 *
 * @Author: Heiku
 * @Date: 2019/8/29
 */

@Data
public class Trade {

    /**
     * 交易id
     */
    private Long tradeId;

    /**
     * 物品id
     */
    private Long goodsId;

    /**
     * 数量
     */
    private Integer num;

    /**
     * 卖家
     */
    private Long seller;

    /**
     * 买家
     */
    private Long buyer;

    /**
     * 交易金额
     */
    private Integer price;

    /**
     * 交易类型: 1：一口价， 2：拍卖
     */
    private Integer type;

    /**
     * 交易状态: 1：进行中，2：已经结束
     */
    private Integer process;

    /**
     * 创建时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;
}
