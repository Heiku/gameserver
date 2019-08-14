package com.ljh.gamedemo.run.boss;

import com.ljh.gamedemo.entity.Boss;
import com.ljh.gamedemo.entity.Duplicate;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.entity.Spell;
import com.ljh.gamedemo.local.LocalAttackCreepMap;
import com.ljh.gamedemo.local.cache.RoleAttrCache;
import com.ljh.gamedemo.local.channel.ChannelCache;
import com.ljh.gamedemo.run.record.FutureMap;
import com.ljh.gamedemo.service.ProtoService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @Author: Heiku
 * @Date: 2019/8/1
 */

@Slf4j
public class BossBeAttackedScheduleRun implements Runnable {

    // 玩家的持续掉血技能
    private Spell spell;

    // 目标的boss
    private Boss boss;

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

    private BossBeAttackedRun attackedRun = BossBeAttackedRun.getInstance();

    public BossBeAttackedScheduleRun(Role role, Spell spell, Boss boss, int extra){
        this.role = role;
        this.spell = spell;
        this.boss = boss;
        this.channel = ChannelCache.getUserIdChannelMap().get(role.getUserId());

        this.extra = extra;
        this.allDamage = spell.getDamage();
    }

    @Override
    public void run() {
        // 初始化数据
        extra = RoleAttrCache.getRoleAttrMap().get(role.getRoleId()).getSp();
        // Boss 血量
        int hp = boss.getHp();

        // 技能造成的每秒伤害值
        int damage= spell.getDamage() / spell.getSec() + extra;

        log.info("Boss：" + boss.getName() + " 持续掉血任务，掉血前的血量为：hp=" + hp);
        if (hp > 0){
            hp -= damage;
            sumDamage += damage;

            if (hp <= 0){
                Duplicate dup = LocalAttackCreepMap.getCurDupMap().get(role.getRoleId());

                // 移除 Boss信息，取消任务
                removeBossInfo(dup, boss, role);

                // 重新获取副本中的 Boss 信息
                List<Boss> nowBosses = dup.getBosses();
                boolean empty;
                empty = nowBosses.isEmpty();

                // 发送通知玩家该 Boss 已经死亡
                attackedRun.sendBossKilledMsg(boss, channel, empty);
                boss = null;

                // 攻击的时间判断
                attackedRun.attackTime(empty, role, dup, nowBosses);
            }

            // 判断总的掉血值，是否超出
            if (sumDamage >= allDamage){
                FutureMap.futureMap.get(this.hashCode()).cancel(true);
                log.info("Boss: " + boss.getName() + " 达到总掉血值，持续掉血任务取消");
                return;
            }

            // 正常扣血
            boss.setHp(hp);
            log.info("Boss: " + boss.getName() + " 持续掉血任务，掉血后的血量为：" + boss.getHp());

            // 消息返回
            attackedRun.sendBossAttackedMsg(boss, channel);
        }
    }


    private void removeBossInfo(Duplicate dup, Boss boss, Role role){
        // 移除 Boss 信息
        dup.getBosses().remove(boss);

        // 取消 Boss的攻击任务
        LocalAttackCreepMap.getUserBeAttackedMap().get(role.getRoleId()).cancel(true);
        LocalAttackCreepMap.getUserBeAttackedMap().remove(role.getRoleId());

        // Boss死亡，移除当前的掉血任务
        FutureMap.futureMap.get(this.hashCode()).cancel(true);
        log.info("Boss: " + boss.getName() + " 已经死亡，持续掉血任务取消");
    }
}
