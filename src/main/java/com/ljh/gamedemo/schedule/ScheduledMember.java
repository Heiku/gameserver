package com.ljh.gamedemo.schedule;

import com.ljh.gamedemo.module.guild.dao.GuildDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 每天更新玩家的今日贡献值
 *
 * @Author: Heiku
 * @Date: 2019/8/28
 */

@Component
@Slf4j
public class ScheduledMember {

    /**
     * GuildDao
     */
    @Autowired
    private GuildDao guildDao;

    @Scheduled(cron = "0 0 0 * * ? ")
    private void updateMemberTodayCon(){
        int n = guildDao.updateTodayCon();
        log.info("update guild_member, affected rows: " + n);
    }
}
