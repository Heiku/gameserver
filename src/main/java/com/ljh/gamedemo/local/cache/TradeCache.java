package com.ljh.gamedemo.local.cache;

import com.google.common.collect.Maps;
import com.ljh.gamedemo.entity.Trade;

import java.util.List;
import java.util.Map;

/**
 * 本地缓存交易信息 （正在进行中的交易）
 *
 * @Author: Heiku
 * @Date: 2019/8/29
 */
public class TradeCache {

    /**
     * 所有的交易行信息（tradeId, trade）
     */
    private static Map<Long, Trade> allTradeMap = Maps.newConcurrentMap();

    /**
     * 交易行一口价的交易信息 (tradeId, trade)
     */
    private static Map<Long, Trade> fixTradeMap = Maps.newConcurrentMap();

    /**
     * 交易行拍卖的交易信息（tradeId, trade）
     */
    private static Map<Long, Trade> auctionTradeMap = Maps.newConcurrentMap();

    /**
     * 玩家正在参与中的交易信息
     */
    private static Map<Long, List<Trade>> roleTradeMap = Maps.newConcurrentMap();

    public static Map<Long, List<Trade>> getRoleTradeMap() {
        return roleTradeMap;
    }

    public static Map<Long, Trade> getAllTradeMap() {
        return allTradeMap;
    }

    public static Map<Long, Trade> getAuctionTradeMap() {
        return auctionTradeMap;
    }

    public static Map<Long, Trade> getFixTradeMap() {
        return fixTradeMap;
    }
}
