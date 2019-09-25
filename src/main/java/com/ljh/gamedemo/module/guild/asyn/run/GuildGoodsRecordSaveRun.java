package com.ljh.gamedemo.module.guild.asyn.run;

import com.ljh.gamedemo.module.guild.bean.GuildGoodsRecord;
import com.ljh.gamedemo.module.guild.dao.GuildGoodsDao;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import static com.ljh.gamedemo.common.CommonDBType.INSERT;

/**
 * 公会物品操作记录
 *
 * @Author: Heiku
 * @Date: 2019/9/25
 */

@Slf4j
public class GuildGoodsRecordSaveRun implements Runnable{

    /**
     * 公会物品操作记录
     */
    private GuildGoodsRecord record;

    /**
     * 操作类型
     */
    private int type;

    /**
     * GuildGoodsDao
     */
    private GuildGoodsDao dao = SpringUtil.getBean(GuildGoodsDao.class);


    public GuildGoodsRecordSaveRun(GuildGoodsRecord record, int type){
        this.record = record;
        this.type = type;
    }

    @Override
    public void run() {
        switch (type){
            case INSERT:
                int n = dao.insertGuildGoodsRecord(record);
                log.info("insert guild_goods_record, affected rows: " + n);
                break;
        }
    }
}
