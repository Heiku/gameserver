package com.ljh.gamedemo.module.role.event;

import com.ljh.gamedemo.module.base.event.OfflineEvent;
import com.ljh.gamedemo.module.event.BaseEvent;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.role.dao.UserRoleDao;
import com.ljh.gamedemo.module.role.service.RoleService;
import com.ljh.gamedemo.run.record.FutureMap;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 玩家事件服务
 *
 * @Author: Heiku
 * @Date: 2019/9/19
 */

@Service
@Slf4j
public class RoleEventService {


    /**
     * userRoleDao
     */
    @Autowired
    private UserRoleDao userRoleDao;


    /**
     * 玩家服务
     */
    @Autowired
    private RoleService roleService;


    /**
     * 监听玩家离线事件
     *
     * @param offlineEvent      离线事件
     */
    @Async
    @EventListener
    public void listenRoleOfflineEvent(OfflineEvent offlineEvent){
        // 获取事件源
        BaseEvent baseEvent = (BaseEvent) offlineEvent.getSource();

        // 获取事件参数信息
        Role role = baseEvent.getRole();

        int n = userRoleDao.updateRoleSiteInfo(role);
        log.info("update role info, affected rows: " + n);

        // 同时，取消玩家的自动恢复任务
        ScheduledFuture future = FutureMap.getRecoverFutureMap().get(role.getRoleId());
        if (!Objects.isNull(future)) {
            future.cancel(true);
        }

        // 下线信息记录
        roleService.updateRoleState(role, false);

        // 取消玩家的受攻击任务
        roleService.removeRoleFutureList(role);
    }
}
