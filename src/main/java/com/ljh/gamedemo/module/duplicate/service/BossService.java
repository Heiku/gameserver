package com.ljh.gamedemo.module.duplicate.service;

import com.ljh.gamedemo.module.duplicate.bean.BossSpell;
import com.ljh.gamedemo.module.duplicate.bean.Duplicate;
import com.ljh.gamedemo.module.creep.local.LocalAttackCreepMap;
import com.ljh.gamedemo.module.base.asyn.run.CustomExecutor;
import com.ljh.gamedemo.module.duplicate.asyn.DuplicateManager;
import com.ljh.gamedemo.module.duplicate.asyn.run.BossAoeRun;
import com.ljh.gamedemo.module.duplicate.asyn.run.BossDizzinessRun;
import com.ljh.gamedemo.module.duplicate.asyn.run.BossDoAttackedRun;
import com.ljh.gamedemo.module.duplicate.asyn.run.BossDurationRun;
import io.netty.util.concurrent.ScheduledFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Boss的相关操作
 *
 * @Author: Heiku
 * @Date: 2019/8/20
 */

@Service
public class BossService {


    /**
     * 副本服务
     */
    @Autowired
    private DuplicateService duplicateService;

    /**
     * Boss 进行普通攻击
     *
     * @param dup   副本信息
     * @param s     Boss技能
     */
    public void bossCommonAttack(Duplicate dup, BossSpell s){
        // 普通攻击
        BossDoAttackedRun task = new BossDoAttackedRun(dup, s);
        CustomExecutor executor = DuplicateManager.getExecutor(dup.getRelatedId());
        ScheduledFuture future = executor.scheduleAtFixedRate(task, 0, s.getCd(), TimeUnit.SECONDS);

        // 任务记录
        recordFuture(dup, future);
    }


    /**
     * Boss 进行AOE攻击
     *
     * @param dup   副本信息
     * @param s     boss技能
     */
    public void bossAOEAttack(Duplicate dup, BossSpell s) {
        // AOE 攻击
        BossAoeRun task = new BossAoeRun(dup, s);
        CustomExecutor executor = DuplicateManager.getExecutor(dup.getRelatedId());
        ScheduledFuture future = executor.scheduleAtFixedRate(task, 0, s.getCd(), TimeUnit.SECONDS);

        // 任务记录
        recordFuture(dup, future);
    }


    /**
     * Boss 进行眩晕攻击
     *
     * @param dup   副本信息
     * @param s     Boss技能
     */
    public void bossDizzinessAttack(Duplicate dup, BossSpell s) {
        // 眩晕攻击
        BossDizzinessRun task = new BossDizzinessRun(dup, s);
        CustomExecutor executor = DuplicateManager.getExecutor(dup.getRelatedId());
        ScheduledFuture future = executor.scheduleAtFixedRate(task, 0, s.getCd(), TimeUnit.SECONDS);

        // 任务记录
        recordFuture(dup, future);
    }


    /**
     * Boss 进行中毒攻击
     *
     * @param dup   副本信息
     * @param s     Boss技能
     */
    public void bossDurationAttack(Duplicate dup, BossSpell s) {
        // 中毒 攻击
        BossDurationRun task = new BossDurationRun(dup, s);
        CustomExecutor executor = DuplicateManager.getExecutor(dup.getRelatedId());
        ScheduledFuture future = executor.scheduleAtFixedRate(task, 0, s.getCd(), TimeUnit.SECONDS);

        // 任务记录
        recordFuture(dup, future);
    }


    /**
     * 将持续任务加入到副本总任务列表中
     *
     * @param dup       副本信息
     * @param future    具体任务
     */
    private void recordFuture(Duplicate dup, ScheduledFuture future){
        // 记录任务
        List<ScheduledFuture> dupAllFuture = LocalAttackCreepMap.getDupAllFutureMap().get(dup.getRelatedId());
        if (dupAllFuture == null){
            dupAllFuture = new ArrayList<>();
        }
        dupAllFuture.add(future);
        LocalAttackCreepMap.getDupAllFutureMap().put(dup.getRelatedId(), dupAllFuture);
    }

}
