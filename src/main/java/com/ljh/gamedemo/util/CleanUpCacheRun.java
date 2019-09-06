package com.ljh.gamedemo.util;

import com.ljh.gamedemo.module.trade.cache.TradeCache;

/**
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
