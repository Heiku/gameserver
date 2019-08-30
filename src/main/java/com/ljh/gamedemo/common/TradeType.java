package com.ljh.gamedemo.common;

/**
 * 交易的方式
 *
 * @Author: Heiku
 * @Date: 2019/8/29
 */
public class TradeType {

    /**
     * 拍卖交易的最长持续时间
     */
    public static final int AUCTION_DURATION = 1;

    /**
     * 出价的累加价格
     */
    public static final int LOWEST_AUCTION_PRICE = 50;

    /**
     * 交易进行中
     */
    public static final int TRADE_STATE_ON = 1;

    /**
     * 交易已经结束
     */
    public static final int TRADE_STATE_OFF = 2;

    /**
     * 一口价模式
     */
    public static final int FIXED = 1;

    /**
     * 拍卖模式
     */
    public static final int AUCTION = 2;
}
