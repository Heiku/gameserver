package com.ljh.gamedemo.run.dup;

import com.ljh.gamedemo.entity.BossSpell;
import com.ljh.gamedemo.entity.Duplicate;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.run.UserExecutorManager;
import com.ljh.gamedemo.run.user.UserBeAttackedRun;
import com.ljh.gamedemo.service.DuplicateService;
import com.ljh.gamedemo.util.SpringUtil;

/**
 * Boss选取目标并进行普通攻击
 *
 * @Author: Heiku
 * @Date: 2019/8/16
 */
public class BossDoAttackedRun implements Runnable {

    /**
     * 副本信息
     */
    private Duplicate dup;

    /**
     * 技能信息
     */
    private BossSpell s;

    /**
     * duplicateService
     */
    private DuplicateService dupService = SpringUtil.getBean(DuplicateService.class);

    public BossDoAttackedRun(Duplicate _dup, BossSpell _s){
        this.dup = _dup;
        this.s = _s;
    }

    @Override
    public void run() {
        Role role = dupService.getFirstAimFromQueue(dup);
        if (role != null) {
            UserBeAttackedRun task = new UserBeAttackedRun(role.getUserId(), s.getDamage(), false);
            UserExecutorManager.addUserTask(role.getUserId(), task);
        }
    }
}
