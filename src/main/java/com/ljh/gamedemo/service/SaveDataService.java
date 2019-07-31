package com.ljh.gamedemo.service;

import com.ljh.gamedemo.dao.UserDao;
import com.ljh.gamedemo.dao.UserRoleDao;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.util.SessionUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SaveDataService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserRoleDao userRoleDao;


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
    }
}
