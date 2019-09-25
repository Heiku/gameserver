package com.ljh.gamedemo.module.guild.asyn.run;

import com.ljh.gamedemo.module.guild.bean.Member;
import com.ljh.gamedemo.module.guild.dao.GuildDao;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import static com.ljh.gamedemo.common.CommonDBType.*;

/**
 * 公会成员数据库操作
 *
 * @Author: Heiku
 * @Date: 2019/9/25
 */

@Slf4j
public class GuildMemberSaveRun implements Runnable {

    /**
     * 公会成员信息
     */
    private Member member;

    /**
     * 操作类型
     */
    private int type;

    /**
     * GuildDao
     */
    private GuildDao dao = SpringUtil.getBean(GuildDao.class);


    public GuildMemberSaveRun(Member member, int type){
        this.member = member;
        this.type = type;
    }


    @Override
    public void run() {
        int n;

        switch (type){
            case INSERT:
                n = dao.insertGuildMember(member);
                log.info("insert into guild_member, affected rows: " + n);
                break;

            case UPDATE:
                n = dao.updateMemberByRoleId(member);
                log.info("update guild_member, affected rows: " + n);
                break;

            case DELETE:
                n = dao.deleteMemberInfo(member);
                log.info("delete guild_member, affected rows: " + n);
                break;
        }
    }
}
