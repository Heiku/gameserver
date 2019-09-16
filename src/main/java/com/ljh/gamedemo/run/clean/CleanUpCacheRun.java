package com.ljh.gamedemo.run.clean;

import com.ljh.gamedemo.module.creep.cache.RevivalCreepCache;
import com.ljh.gamedemo.module.trade.cache.TradeCache;

/**
 * 定期清除交易缓存记录，便于触发过期定时器
 *
 * @Author: Heiku
 * @Date: 2019/8/30
 */
public class CleanUpCacheRun implements Runnable {

    @Override
    public void run() {

        // 拍卖时间定时
        TradeCache.getTradeCache().cleanUp();

        // 野怪复活定时清除触发
        RevivalCreepCache.getRevivalCache().cleanUp();
    }
}
