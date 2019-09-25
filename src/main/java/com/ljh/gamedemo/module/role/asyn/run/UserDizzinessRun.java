package com.ljh.gamedemo.module.role.asyn.run;

/**
 * 玩家受到眩晕技能，停止一切动作
 *
 * @Author: Heiku
 * @Date: 2019/8/20
 */
public class UserDizzinessRun implements Runnable {

    /**
     * 眩晕时间
     */
    private int sec;

    public UserDizzinessRun(int _sec){
        this.sec = _sec;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(sec * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
