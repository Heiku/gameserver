package com.ljh.gamedemo.module.base.service;

import com.ljh.gamedemo.module.duplicate.service.DuplicateService;
import com.ljh.gamedemo.module.group.service.GroupService;
import com.ljh.gamedemo.module.role.dao.UserRoleDao;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.user.local.LocalUserMap;
import com.ljh.gamedemo.module.role.service.RoleService;
import com.ljh.gamedemo.run.record.FutureMap;
import com.ljh.gamedemo.module.user.service.UserService;
import com.ljh.gamedemo.util.SessionUtil;
import io.netty.channel.Channel;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 用于玩家离线时，保存玩家的数据，及数据的清理
 *
 */
@Service
@Slf4j
public class SaveDataService {

    @Autowired
    private UserRoleDao userRoleDao;

    /**
     * 用户服务
     */
    @Autowired
    private UserService userService;

    /**
     * 玩家服务
     */
    @Autowired
    private RoleService roleService;

    /**
     * 副本服务
     */
    @Autowired
    private DuplicateService duplicateService;

    /**
     * 组队服务
     */
    @Autowired
    private GroupService groupService;

    /**
     * 用于用户掉线时，保存用户的数据
     *
     * @param channel
     */
    public void leaveSaveUserData(Channel channel){
        // 获取对应的userId
        long userId = SessionUtil.getUserId(channel);
        if (userId <= 0){
            return;
        }
        Role role = LocalUserMap.userRoleMap.get(userId);
        int n = userRoleDao.updateRoleSiteInfo(role);
        System.out.println(role);
        log.info("update user data before inactive(): " + n);
        log.info(userId + " - "  + role.getName() + " 暂时下线！");


        // 同时，取消玩家的自动恢复任务
        ScheduledFuture future = FutureMap.getRecoverFutureMap().get(role.getRoleId());
        future.cancel(true);

        // 取消玩家的受攻击任务
        roleService.removeRoleFutureList(role);

        // 如果在挑战副本，移出攻击目标队列中
        duplicateService.removeAttackedQueue(role);

        // 退出队伍信息
        groupService.removeGroup(role);

        // 下线信息记录
        userService.updateRoleState(role, false);
    }
}
