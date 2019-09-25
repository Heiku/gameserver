package com.ljh.gamedemo.module.guild.asyn.run;

import com.ljh.gamedemo.module.guild.bean.GuildApply;
import com.ljh.gamedemo.module.guild.dao.GuildApplyDao;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import static com.ljh.gamedemo.common.CommonDBType.INSERT;
import static com.ljh.gamedemo.common.CommonDBType.UPDATE;

/**
 * 公会申请数据库记录
 *
 * @Author: Heiku
 * @Date: 2019/9/25
 */

@Slf4j
public class GuildApplySaveRun implements Runnable {

    /**
     * 公会申请信息
     */
    private GuildApply apply;

    /**
     * 操作类型
     */
    private int type;

    /**
     * GuildApplyDao
     */
    private GuildApplyDao dao = SpringUtil.getBean(GuildApplyDao.class);


    public GuildApplySaveRun(GuildApply apply, int type){
        this.apply = apply;
        this.type = type;
    }


    @Override
    public void run() {
        int n;

        switch (type){
            case INSERT:
                n = dao.insertGuildApply(apply);
                log.info("insert guild_apply, affected rows: " + n);
                break;

            case UPDATE:
                n = dao.updateGuildApply(apply);
                log.info("update guild_apply, affected rows: " + n);
                break;
        }
    }
}
