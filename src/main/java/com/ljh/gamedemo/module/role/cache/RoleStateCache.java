package com.ljh.gamedemo.module.role.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ljh.gamedemo.module.chat.dao.ChatRecordDao;
import com.ljh.gamedemo.module.role.bean.RoleState;
import com.ljh.gamedemo.module.role.dao.RoleStateDao;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 玩家在线信息缓存
 *
 * @Author: Heiku
 * @Date: 2019/8/7
 */

@Slf4j
public class RoleStateCache {

    /**
     * ChatRecordDao
     */
    private static RoleStateDao roleStateDao;

    static {
        roleStateDao = SpringUtil.getBean(RoleStateDao.class);
    }

    /**
     * 玩家上下线本地记录
     */
    private static Cache<Long, RoleState> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.DAYS)
            .build();

    public static Cache<Long, RoleState> getCache() {
        return cache;
    }


    /**
     * 读取数据库中的上下线记录
     */
    public static void postConstructReadDb(){

        List<RoleState> stateList = roleStateDao.selectAllRoleState();
        stateList.forEach(s -> {
            cache.put(s.getRoleId(), s);
        });

        log.info("加载玩家在线信息成功！");
    }
}
