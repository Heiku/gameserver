package com.ljh.gamedemo.listener;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.ljh.gamedemo.entity.Trade;
import com.ljh.gamedemo.service.TradeService;
import com.ljh.gamedemo.util.SpringUtil;

/**
 * 用于处理交易时间到期的交易信息
 *
 * @Author: Heiku
 * @Date: 2019/8/30
 */

public class TradeExpireListener implements RemovalListener<Long, Trade> {

    /**
     * 交易服务
     */
    private static TradeService tradeService;

    static {
        tradeService = SpringUtil.getBean(TradeService.class);
    }


    @Override
    public void onRemoval(RemovalNotification<Long, Trade> notify) {
            // 获取交易时间结束的交易单
            Trade trade = notify.getValue();

            // 判断交易是否有买家
            if (trade.getBuyer() == 0L) {
                // 说明无人竞拍物品，删除交易信息
                tradeService.removeTradeReturn(trade);
                return;
            }

            // 说明拍卖交易完成
            tradeService.completeAuctionTrade(trade);
    }
}
