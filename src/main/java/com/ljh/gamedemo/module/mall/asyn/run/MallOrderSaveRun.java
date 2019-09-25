package com.ljh.gamedemo.module.mall.asyn.run;

import com.ljh.gamedemo.module.mall.bean.MallOrder;
import com.ljh.gamedemo.module.mall.dao.MallOrderDao;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import static com.ljh.gamedemo.common.CommonDBType.INSERT;

/**
 * 商城订单数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/9/25
 */
@Slf4j
public class MallOrderSaveRun implements Runnable {

    /**
     * 商城订单
     */
    private MallOrder mallOrder;

    /**
     * 操作类型
     */
    private int type;

    /**
     * MallOrderDao
     */
    private MallOrderDao dao = SpringUtil.getBean(MallOrderDao.class);

    public MallOrderSaveRun(MallOrder order, int type){
        this.mallOrder = order;
        this.type = type;
    }

    @Override
    public void run() {
        int n;

        switch (type){
            case INSERT:
                n = dao.insertMallOrder(mallOrder);
                log.info("insert into mall_order, affected rows: " + n);
        }
    }
}
