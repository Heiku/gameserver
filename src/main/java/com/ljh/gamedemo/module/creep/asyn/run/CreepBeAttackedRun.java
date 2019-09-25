package com.ljh.gamedemo.module.creep.asyn.run;

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
        int hp = creep.getHp();

        if (hp > 0){
            hp -= damage;

            // 野怪死亡
            if (hp <= 0){
                // 野怪死亡的具体操作
                creepService.doCreepDeath(creep, role);
                return;
            }

            // 正常扣血
            creep.setHp(hp);
            LocalCreepMap.getIdCreepMap().put(creep.getId(), creep);

            // 最后攻击成功，返回消息给client
            creepService.sendCreepMsg(role, creep, ContentType.ATTACK_SPELL_SUCCESS);
        }
    }
}
