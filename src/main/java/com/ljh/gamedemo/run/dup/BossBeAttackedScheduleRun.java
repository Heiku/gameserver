package com.ljh.gamedemo.run.dup;

import com.ljh.gamedemo.module.duplicate.bean.Boss;
import com.ljh.gamedemo.module.duplicate.bean.Duplicate;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.spell.bean.Spell;
import com.ljh.gamedemo.module.role.cache.RoleAttrCache;
import com.ljh.gamedemo.module.base.cache.ChannelCache;
import com.ljh.gamedemo.run.record.FutureMap;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Boss的持续掉血任务
 *
 * @Author: Heiku
 * @Date: 2019/8/1
 */

@Slf4j
public class BossBeAttackedScheduleRun implements Runnable {

    // 玩家的持续掉血技能
    private Spell spell;

    // 副本信息
    private Duplicate dup;

    // 玩家属性
    private Role role;

    // 加成的技能额外伤害
    private int extra;

    // 技能持续的总伤害值
    private int allDamage;

    // 累计的伤害值
    private int sumDamage = 0;

    // 通信 channel
    private Channel channel;

    // 任务
    private BossBeAttackedRun attackedRun;

    public BossBeAttackedScheduleRun(Role role, Spell spell, Duplicate _dup, int extra){
        this.role = role;
        this.spell = spell;
        this.dup = _dup;
        this.channel = ChannelCache.getUserIdChannelMap().get(role.getUserId());

        this.extra = extra;
        this.allDamage = spell.getDamage();

        attackedRun = new BossBeAttackedRun(role, allDamage, dup.getBosses().get(0));
    }

    @Override
    public void run() {
        // 判断当前副本是否还存在Boss
        List<Boss> bossList = dup.getBosses();
        if (bossList == null || bossList.isEmpty()){
            cancelBossFuture();
            return;
        }
        Boss boss = bossList.get(0);

        // 初始化数据
        extra = RoleAttrCache.getRoleAttrMap().get(role.getRoleId()).getSp();
        // Boss 血量
        int hp = boss.getHp();
        // 技能造成的每秒伤害值
        int damage= spell.getDamage() / spell.getSec() + extra;

        if (hp > 0){
            hp -= damage;
            sumDamage += damage;
            if (hp <= 0){
                // 移除 Boss信息，取消任务
                cancelBossFuture();
                attackedRun.doBossDeath();
                return;
            }

            // 判断总的掉血值，是否超出
            if (sumDamage >= allDamage){
                cancelBossFuture();
                return;
            }
            // 正常扣血
            boss.setHp(hp);
            // 消息返回
            attackedRun.sendBossAttackedMsg(boss);
        }
    }


    /**
     * 取消Boss的伤害任务
     *
     */
    private void cancelBossFuture(){
        // Boss死亡，移除当前的掉血任务
        FutureMap.futureMap.get(this.hashCode()).cancel(true);
        log.info("Boss已经死亡，持续掉血任务取消");
    }
}
