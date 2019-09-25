package com.ljh.gamedemo.module.guild.asyn.run;

import com.ljh.gamedemo.module.guild.bean.GuildGoodsStore;
import com.ljh.gamedemo.module.guild.dao.GuildGoodsDao;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import static com.ljh.gamedemo.common.CommonDBType.*;

/**
 * 公户物品数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/9/25
 */

@Slf4j
public class GuildStoreSaveRun implements Runnable {

    /**
     * 公会物品变更记录
     */
    private GuildGoodsStore store;

    /**
     * 操作类型
     */
    private int type;

    /**
     * GuildGoodsDao
     */
    private GuildGoodsDao dao = SpringUtil.getBean(GuildGoodsDao.class);


    public GuildStoreSaveRun(GuildGoodsStore store, int type){
        this.store = store;
        this.type = type;
    }

    @Override
    public void run() {
        int n;

        switch (type){
            case INSERT:
                n = dao.insertGuildStore(store);
                log.info("insert guild_goods_store, affected rows: " + n);
                break;

            case UPDATE:
                n = dao.updateGuildStore(store);
                log.info("update guild_goods_store, affected rows: " + n);
                break;

            case DELETE:
                n = dao.deleteGuildStore(store);
                log.info("delete guild_goods_store, affected rows: " + n);
                break;
        }
    }
}
