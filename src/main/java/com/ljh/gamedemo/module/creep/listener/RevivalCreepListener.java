package com.ljh.gamedemo.module.creep.listener;

import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.ljh.gamedemo.module.creep.bean.Creep;
import com.ljh.gamedemo.module.entity.service.EntityService;
import com.ljh.gamedemo.util.SpringUtil;

/**
 * 用户处理具体的野怪复活操作
 *
 * @Author: Heiku
 * @Date: 2019/9/16
 */
public class RevivalCreepListener implements RemovalListener<Long, Creep> {

    /**
     * 实体服务
     */
    private static EntityService entityService;

    static {
        entityService = SpringUtil.getBean(EntityService.class);
    }

    @Override
    public void onRemoval(RemovalNotification<Long, Creep> notify) {
        if (notify.getCause() == RemovalCause.EXPIRED){

            // 获取死亡的野怪实例
            Creep creep = notify.getValue();

            // 重新设置血量
            creep.setHp(creep.getMaxHp());

            // 具体复活操作
            entityService.revivalCreep(creep);
        }
    }
}
