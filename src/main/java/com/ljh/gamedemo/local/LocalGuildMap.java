package com.ljh.gamedemo.local;

import com.ljh.gamedemo.dao.GuildDao;
import com.ljh.gamedemo.dao.GuildGoodsDao;
import com.ljh.gamedemo.entity.Guild;
import com.ljh.gamedemo.local.cache.GuildCache;
import com.ljh.gamedemo.util.SpringUtil;

import java.util.List;

/**
 * 加载数据库中的公会信息
 *
 * @Author: Heiku
 * @Date: 2019/8/27
 */
public class LocalGuildMap {

    /**
     * GuildDao
     */
    private static GuildDao guildDao;

    /**
     * GuildGoodsDao
     */
    private static GuildGoodsDao guildGoodsDao;

    static {
        guildDao = SpringUtil.getBean(GuildDao.class);
        guildGoodsDao = SpringUtil.getBean(GuildGoodsDao.class);
    }

    public void readDB(){
        List<Guild> guilds = guildDao.queryGuildList();
        if (guilds != null){
            guilds.forEach(g ->
                GuildCache.getIdGuildMap().put(g.getId(), g));
        }


    }
}
