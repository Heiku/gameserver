package com.ljh.gamedemo.module.trade.asyn.run;

import com.ljh.gamedemo.module.trade.bean.Trade;
import com.ljh.gamedemo.module.trade.dao.TradeDao;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import static com.ljh.gamedemo.common.CommonDBType.INSERT;
import static com.ljh.gamedemo.common.CommonDBType.UPDATE;

/**
 * 交易数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/9/25
 */

@Slf4j
public class TradeSaveRun implements Runnable {

    /**
     * 交易信息
     */
    private Trade trade;

    /**
     * 操作类型
     */
    private int type;

    /**
     * TradeDao
     */
    private TradeDao dao = SpringUtil.getBean(TradeDao.class);

    public TradeSaveRun(Trade trade, int type) {
        this.trade = trade;
        this.type = type;
    }

    @Override
    public void run() {
        int n;

        switch (type){
            case INSERT:
                n = dao.insertTrade(trade);
                log.info("insert into trade, affected rows: " + n);
                break;

            case UPDATE:
                n = dao.updateTrade(trade);
                log.info("update trade, affected rows: " + n);
                break;
        }
    }
}
