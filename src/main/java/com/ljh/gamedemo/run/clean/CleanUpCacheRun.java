package com.ljh.gamedemo.run.clean;

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

        // 清除cache中的过期项，便于监听器的察觉
        TradeCache.getTradeCache().cleanUp();
    }
}
