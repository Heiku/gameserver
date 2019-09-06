package com.ljh.gamedemo.run.creep;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.module.creep.bean.Creep;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.creep.local.LocalAttackCreepMap;
import com.ljh.gamedemo.module.creep.local.LocalCreepMap;
import com.ljh.gamedemo.module.creep.service.AttackCreepService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;

/**
 * 野怪受到直接伤害
 *
 * @Author: Heiku
 * @Date: 2019/7/19
 */

@Slf4j
public class CreepBeAttackedRun implements Runnable {


    /**
     * 玩家信息
     */
    private Role role;

    /**
     * 野怪信息
     */
    private Creep creep;

    /**
     * 造成的伤害值
     */
    private int damage;

    /**
     * 攻击野怪服务
     */
    private AttackCreepService creepService = SpringUtil.getBean(AttackCreepService.class);

    public CreepBeAttackedRun(Role role, Creep creep, int damage){
        this.role = role;
        this.creep = creep;
        this.damage = damage;
    }

    @Override
    public void run() {

        // 指定攻击的野怪
        int startHp = creep.getHp();
        int hp = creep.getHp();

        if (hp > 0){
            hp -= damage;

            // 野怪死亡
            if (hp <= 0){
                // 重置血量
                creep.setHp(startHp);
                creep.setNum(creep.getNum() - 1);
                LocalCreepMap.getIdCreepMap().put(creep.getCreepId(), creep);

                // 野怪死亡，取消玩家自动扣血task
                ScheduledFuture future = LocalAttackCreepMap.getUserBeAttackedMap().get(role.getRoleId());
                future.cancel(true);
                LocalAttackCreepMap.getUserBeAttackedMap().remove(role.getRoleId());

                // 消息返回
                creepService.sendCreepMsg(role, creep, ContentType.ATTACK_DEATH_CREEP);
            }

            // 正常扣血
            creep.setHp(hp);
            LocalCreepMap.getIdCreepMap().put(creep.getCreepId(), creep);

            // 最后攻击成功，返回消息给client
            creepService.sendCreepMsg(role, creep, ContentType.ATTACK_CURRENT);
        }
    }
}
