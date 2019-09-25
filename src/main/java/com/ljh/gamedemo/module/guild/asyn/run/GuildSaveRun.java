package com.ljh.gamedemo.module.guild.asyn.run;

import com.ljh.gamedemo.module.guild.bean.Guild;
import com.ljh.gamedemo.module.guild.dao.GuildDao;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import static com.ljh.gamedemo.common.CommonDBType.*;

/**
 * 公会数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/9/25
 */
@Slf4j
public class GuildSaveRun implements Runnable{

    /**
     * 公会信息
     */
    private Guild guild;

    /**
     * 操作类型
     */
    private int type;


    /**
     * GuildDao
     */
    private GuildDao dao = SpringUtil.getBean(GuildDao.class);


    public GuildSaveRun(Guild guild, int type){
        this.guild = guild;
        this.type = type;
    }

    @Override
    public void run() {
        int n;

        switch (type){
            case INSERT:
                n = dao.insertGuild(guild);
                log.info("insert guild, affected row: " + n);
                break;

            case UPDATE:
                n = dao.updateGuild(guild);
                log.info("update guild, affected rows: " + n);
                break;

            case DELETE:
                n = dao.deleteGuild(guild);
                log.info("delete guild, affected rows: " + n);
                break;
        }
    }
}
