package com.ljh.gamedemo.local;

import com.google.common.collect.Lists;
import com.ljh.gamedemo.common.TradeType;
import com.ljh.gamedemo.dao.TradeDao;
import com.ljh.gamedemo.entity.Trade;
import com.ljh.gamedemo.local.cache.TradeCache;
import com.ljh.gamedemo.util.SpringUtil;

import java.util.List;
import java.util.Optional;

/**
 * 载入所有正在进行中的商品信息
 *
 * @Author: Heiku
 * @Date: 2019/8/30
 */
public class LocalTradeMap {

    /**
     * TradeDao
     */
    private static TradeDao tradeDao;


    static {
        tradeDao = SpringUtil.getBean(TradeDao.class);
    }

    public static void readDB(){
        List<Trade> unCompleteTrade = tradeDao.selectAllOnTrade();
        if (unCompleteTrade == null || unCompleteTrade.isEmpty()){
            return;
        }

        // 根据正在交易的类型进行分类
        unCompleteTrade.forEach(t -> {
            if (t.getType() == TradeType.FIXED){
                TradeCache.getFixTradeMap().put(t.getId(), t);
            }

            if (t.getType() == TradeType.AUCTION){
                TradeCache.getAuctionTradeMap().put(t.getId(), t);
            }


            // 存储用户关联的交易信息
            List<Trade> roleTrades = Optional.ofNullable(TradeCache.getRoleTradeMap().get(t.getSeller()))
                    .orElse(Lists.newArrayList());
            roleTrades.add(t);
            TradeCache.getRoleTradeMap().put(t.getSeller(), roleTrades);


            if (t.getBuyer() != 0){
                roleTrades = Optional.ofNullable(TradeCache.getRoleTradeMap().get(t.getBuyer()))
                        .orElse(Lists.newArrayList());
                roleTrades.add(t);
                TradeCache.getRoleTradeMap().put(t.getBuyer(), roleTrades);
            }
        });
    }
}

