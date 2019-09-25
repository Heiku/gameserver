package com.ljh.gamedemo.module.email.asyn.run;

import com.ljh.gamedemo.module.email.bean.EmailGoods;
import com.ljh.gamedemo.module.email.dao.EmailGoodsDao;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import static com.ljh.gamedemo.common.CommonDBType.INSERT;

/**
 * 邮件物品数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/9/25
 */

@Slf4j
public class EmailGoodsSaveRun implements Runnable {

    /**
     * 邮件物品信息
     */
    private EmailGoods emailGoods;

    /**
     * 操作类型
     */
    private int type;


    /**
     * EmailGoodsDao
     */
    private EmailGoodsDao dao = SpringUtil.getBean(EmailGoodsDao.class);

    public EmailGoodsSaveRun(EmailGoods ed, int type){
        this.emailGoods = ed;
        this.type = type;
    }

    @Override
    public void run() {
        switch (type){
            case INSERT:
                int n = dao.insertEmailGoods(emailGoods);
                log.info("insert into emailGoods, affected row:" + n);
                break;
        }
    }
}
