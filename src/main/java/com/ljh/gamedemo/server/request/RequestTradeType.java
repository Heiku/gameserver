package com.ljh.gamedemo.server.request;

/**
 * 交易的请求类型
 *
 * @Author: Heiku
 * @Date: 2019/8/29
 */
public class RequestTradeType {

    /**
     * 交易状态
     */
    public static final int TRADE_STATE = 0;

    /**
     * 获取所有一口价商品
     */
    public static final int FIXED_PRICE_ALL = 1;

    /**
     * 获取所有拍卖商品
     */
    public static final int AUCTION_ALL = 2;

    /**
     * 上架商品
     */
    public static final int PUT_GOODS = 3;

    /**
     * 购买一口价物品
     */
    public static final int BUY_FIXED = 4;

    /**
     * 购买拍卖物品
     */
    public static final int BUY_AUCTION = 5;

    /**
     * 商品下架
     */
    public static final int OUT_OF_TRADE = 6;
}
