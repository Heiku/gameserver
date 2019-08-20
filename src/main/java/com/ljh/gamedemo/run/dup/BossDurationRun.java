package com.ljh.gamedemo.run.dup;

import com.ljh.gamedemo.entity.BossSpell;
import com.ljh.gamedemo.entity.Duplicate;
import com.ljh.gamedemo.entity.DurationAttack;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.local.LocalAttackCreepMap;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.run.UserExecutorManager;
import com.ljh.gamedemo.run.user.UserBeAttackedScheduleRun;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Boss 进行中毒攻击
 *
 * @Author: Heiku
 * @Date: 2019/8/20
 */
public class BossDurationRun implements Runnable {

    /**
     * 副本信息
     */
    private Duplicate dup;

    /**
     * 技能信息
     */
    private BossSpell s;

    public BossDurationRun(Duplicate _dup, BossSpell _s){
        this.dup = _dup;
        this.s = _s;
    }

    @Override
    public void run() {
        // 获取挑战者列表
        Deque<Long> deque = LocalAttackCreepMap.getBossAttackQueueMap().get(dup.getRelatedId());
        if (deque == null || deque.isEmpty()){
            return;
        }

        // 开始群体眩晕 （玩家将被眩晕， 伙伴免疫控制）
        for (Long id : deque) {
            // 玩家受到伤害
            Role role = LocalUserMap.getIdRoleMap().get(id);
            if (role != null){
                DurationAttack da = new DurationAttack();
                da.setDamage(s.getDamage());
                da.setSec(s.getSec());
                UserBeAttackedScheduleRun task = new UserBeAttackedScheduleRun(role, da, 0, false);
                ScheduledFuture future = UserExecutorManager.getUserExecutor(role.getUserId())
                        .scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);

                // 添加到futureList中
                List<ScheduledFuture> futureList = LocalAttackCreepMap.getDupAllFutureMap().get(dup.getRelatedId());
                if (futureList == null){
                    futureList = new ArrayList<>();
                }
                futureList.add(future);
                LocalAttackCreepMap.getDupAllFutureMap().put(dup.getRelatedId(), futureList);
                break;
            }
        }
    }
}
