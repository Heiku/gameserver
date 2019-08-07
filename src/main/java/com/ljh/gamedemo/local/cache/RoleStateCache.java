package com.ljh.gamedemo.local.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ljh.gamedemo.dao.ChatRecordDao;
import com.ljh.gamedemo.entity.RoleState;
import com.ljh.gamedemo.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Heiku
 * @Date: 2019/8/7
 *
 * 玩家在线信息缓存
 */

@Slf4j
public class RoleStateCache {

    private static ChatRecordDao recordDao;

    static {
        recordDao = SpringUtil.getBean(ChatRecordDao.class);
    }

    private static Cache<Long, RoleState> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.DAYS)
            .build();

    public static Cache<Long, RoleState> getCache() {
        return cache;
    }

    public static void postConstructReadDb(){

        List<RoleState> stateList = recordDao.selectAllRoleState();
        stateList.forEach(s -> {
            cache.put(s.getRoleId(), s);
        });

        log.info("加载玩家在线信息成功！");
    }
}
