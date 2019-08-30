package com.ljh.gamedemo.local.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.ljh.gamedemo.common.TradeType;
import com.ljh.gamedemo.entity.Trade;
import com.ljh.gamedemo.listener.TradeExpireListener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    private static Cache<Long, Trade> tradeCache = CacheBuilder.newBuilder()
            .expireAfterWrite(TradeType.AUCTION_DURATION, TimeUnit.MINUTES)
            .removalListener(new TradeExpireListener())
            .build();


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


    public static Cache<Long, Trade> getTradeCache() {
        return tradeCache;
    }

    public static Map<Long, List<Trade>> getRoleTradeMap() {
        return roleTradeMap;
    }

    public static Map<Long, Trade> getAuctionTradeMap() {
        return auctionTradeMap;
    }

    public static Map<Long, Trade> getFixTradeMap() {
        return fixTradeMap;
    }
}
