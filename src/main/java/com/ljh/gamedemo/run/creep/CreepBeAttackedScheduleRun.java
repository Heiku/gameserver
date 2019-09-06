package com.ljh.gamedemo.run.creep;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.module.creep.bean.Creep;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.spell.bean.Spell;
import com.ljh.gamedemo.module.creep.local.LocalCreepMap;
import com.ljh.gamedemo.module.base.cache.ChannelCache;
import com.ljh.gamedemo.run.record.FutureMap;
import com.ljh.gamedemo.module.creep.service.AttackCreepService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;


/**
 * 技能对野怪造成持续伤害
 *
 * @Author: Heiku
 * @Date: 2019/7/22
 */

@Slf4j
public class CreepBeAttackedScheduleRun implements Runnable {

    /**
     * 玩家信息
     */
    private Role role;

    /**
     * 技能信息
     */
    private Spell spell;

    /**
     * 野怪信息
     */
    private Creep creep;

    /**
     * 额外伤害加成
     */
    private int extra;

    /**
     * channel
     */
    private Channel channel;

    /**
     * 技能总伤害
     */
    private Integer allDamage;

    /**
     * 技能累计伤害
     */
    private Integer sumDamage = 0;

    /**
     * 攻击野怪的任务
     */
    private AttackCreepService creepService = SpringUtil.getBean(AttackCreepService.class);



    public CreepBeAttackedScheduleRun(Role role, Spell spell, Creep creep, int extra){
        this.role = role;
        this.spell = spell;
        this.creep = creep;
        this.extra = extra;

        this.channel = ChannelCache.getUserIdChannelMap().get(role.getUserId());

        // 持续伤害的总伤害值
        this.allDamage = spell.getDamage();
    }

    @Override
    public void run() {
        // 获取初始血量西悉尼
        int hp = creep.getHp();
        int startUp = creep.getMaxHp();

        // 每秒造成的伤害值
        int damage = spell.getDamage() / spell.getSec() + extra;

        log.info("野怪持续掉血任务，野怪掉血前：" + creep.getHp());
        if (hp > 0){
            hp -= damage;
            sumDamage += damage;

            if (hp <= 0){
                creep.setHp(startUp);
                creep.setNum(creep.getNum() - 1);
                // 更新cache
                LocalCreepMap.getIdCreepMap().put(creep.getCreepId(), creep);

                // 同时，野怪死亡，取消任务
                FutureMap.futureMap.get(this.hashCode()).cancel(true);
                log.info("野怪持续掉血任务：野怪死亡，任务取消");

                creepService.sendCreepMsg(role, creep, ContentType.ATTACK_DEATH_CREEP);
                return;
            }

            // 判断掉血值
            if (sumDamage >= allDamage){
                FutureMap.futureMap.get(this.hashCode()).cancel(true);
                log.info("野怪持续掉血任务：达到最大掉血值，任务取消");
                return;
            }

            // 更新野怪的血量
            creep.setHp(hp);
            LocalCreepMap.getIdCreepMap().put(creep.getCreepId(), creep);

            // 最后攻击成功，返回消息给client
            creepService.sendCreepMsg(role, creep, ContentType.ATTACK_CURRENT);
        }
    }
}
