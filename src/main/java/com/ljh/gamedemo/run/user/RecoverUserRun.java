package com.ljh.gamedemo.run.user;

import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.run.record.RecoverBuff;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @Author: Heiku
 * @Date: 2019/7/22
 */

@Slf4j
public class RecoverUserRun implements Runnable {

    // 玩家的最大生命值
    public static final int MAX_HP = 1000;

    // 玩家的最大蓝量值
    public static final int MAX_MP = 300;

    // 玩家自动恢复生命的速度
    private static final int DEFAULT_HP_SEC = 2;

    // 玩家自动恢复蓝量的速度
    private static final int DEFAULT_MP_SEC = 1;


    private long userId;

    private RecoverBuff buff;

    private Channel channel;

    public RecoverUserRun(long userId, RecoverBuff buff, Channel channel){
        this.userId = userId;
        this.buff = buff;
        this.channel = channel;
    }

    @Override
    public void run() {

        // 无buff，采用默认的恢复速度
        if (buff == null){
            buff = new RecoverBuff();
            buff.setHpBuf(DEFAULT_HP_SEC);
            buff.setMpBuf(DEFAULT_MP_SEC);
        }

        Role role = LocalUserMap.userRoleMap.get(userId);
        log.info("用户自动恢复任务更新前：hp=" + role.getHp() + " mp=" + role.getMp());


        // 血量恢复
        int hp = role.getHp();
        if (hp >= MAX_HP){
            log.info("玩家：" + role.getName() + " 当前血量已经满格，无法自动恢复生命值");
        }else {
            int hpSec = buff.getHpBuf();
            hp += hpSec;
            log.info("玩家：" + role.getName() + " 血量恢复：" + hpSec);
            if (hp > MAX_HP) {
                hp = MAX_HP;
            }
            role.setHp(hp);
        }

        // 蓝量恢复
        int mp = role.getMp();
        if (mp >= MAX_MP){
            log.info("玩家：" + role.getName() + " 当前蓝量已经满格，无法自动恢复蓝量值");
            return;
        }else {
            int mpSec = buff.getMpBuf();
            mp += mpSec;
            log.info("玩家：" + role.getName() + " 蓝量恢复：" + mpSec);
            if (mp > MAX_MP){
                mp = MAX_MP;
            }
            role.setMp(mp);
        }

        // 更新用户数据
        LocalUserMap.idRoleMap.put(role.getRoleId(), role);
        log.info("用户自动恢复任务更新后：hp=" + role.getHp() + " mp=" + role.getMp());


        List<Role> siteRoleList = LocalUserMap.siteRolesMap.get(role.getSiteId());
        for (Role role1 : siteRoleList) {
            if (role1.getRoleId().intValue() == role.getRoleId().intValue()){
                role1.setHp(role.getHp());
                role1.setMp(role.getMp());
                break;
            }
        }

        for (Role role1 : siteRoleList) {
            if (role1.getRoleId().intValue() == role.getRoleId().intValue()) {
                log.info("用户自动恢复任务更新后：hp=" + role1.getHp() + " mp=" + role1.getMp());
                break;
            }
        }
    }
}
