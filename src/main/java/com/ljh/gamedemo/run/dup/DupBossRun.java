package com.ljh.gamedemo.run.dup;

import com.ljh.gamedemo.entity.*;
import com.ljh.gamedemo.local.LocalAttackCreepMap;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.run.CustomExecutor;
import com.ljh.gamedemo.run.UserExecutorManager;
import com.ljh.gamedemo.run.record.FutureMap;
import com.ljh.gamedemo.run.user.UserBeAttackedRun;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * Boss 开始攻击任务
 *
 * @Author: Heiku
 * @Date: 2019/8/15
 */
public class DupBossRun implements Runnable{

    /**
     * 副本信息
     */
    private Duplicate dup;

    /**
     * 被攻击的玩家列表
     */
    private Queue<Long> queue;

    /**
     * 抗伤害的玩家
     */
    private Role role;

    public DupBossRun(Duplicate _dup){
        this.dup = _dup;
    }

    @Override
    public void run() {
        initBeAttacked(dup);

        // 获取当前挑战的 Boss
        Boss b = dup.getBosses().get(0);

        // 获取技能列表，并开始执行技能攻击玩家
        List<BossSpell> spells = b.getSpellList();
        spells.forEach(s -> {

            // 根据技能的具体类型进行攻击
            if (s.getSec() == 0){
                if (s.getRange() == 0){
                    // 普通攻击
                    commonAttacked(s);
                }
            }
        });
    }


    /**
     * Boss 进行普攻攻击
     */
    private void commonAttacked(BossSpell s){
        // 构建任务，加入到用户线程中
        UserBeAttackedRun task = new UserBeAttackedRun(role.getUserId(), s.getDamage(), false);
        CustomExecutor userExecutor = UserExecutorManager.getUserExecutor(role.getUserId());
        ScheduledFuture future = userExecutor.scheduleAtFixedRate(task, 0, s.getCd(), TimeUnit.SECONDS);

        // 加入当前副本中玩家的伤害任务
        List<ScheduledFuture> futureList = LocalAttackCreepMap.getDupUserBeAttFutMap().get(role.getRoleId());
        if (futureList == null){
            futureList = new ArrayList<>();
        }
        futureList.add(future);
        FutureMap.getFutureMap().put(task.hashCode(), future);
    }

    /**
     * 初始化 Boss 的攻击目标数据
     *
     * @param dup
     */
    private void initBeAttacked(Duplicate dup){
        queue = LocalAttackCreepMap.getBossAttackedMap().get(dup.getRelatedId());
        if (queue != null && !queue.isEmpty()){
            long aimId = queue.peek();
            role = LocalUserMap.idRoleMap.get(aimId);
        }
    }
}
