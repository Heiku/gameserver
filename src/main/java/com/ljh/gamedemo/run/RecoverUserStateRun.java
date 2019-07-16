package com.ljh.gamedemo.run;

import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.local.LocalUserMap;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @Author: Heiku
 * @Date: 2019/7/16
 *
 * 游戏开始时启动，用于恢复所有角色的属性值（mp支持）
 */

@Slf4j
public class RecoverUserStateRun implements Runnable {

    private static final int MAX_MP = 300;
    private static final int RE_MP_SEC = 5;

    @Override
    public void run() {
        while (true) {
            Map<Long, Role> roleMap = LocalUserMap.userRoleMap;
            roleMap.forEach((k, v) -> {
                int mp = v.getMp();

                if (mp < MAX_MP) {
                    mp += RE_MP_SEC;
                    v.setMp(mp);

                    LocalUserMap.userRoleMap.put(k, v);

                    List<Role> roleList = LocalUserMap.siteRolesMap.get(v.getSiteId());
                    for (Role r : roleList){
                        if (r.getRoleId().longValue() == v.getRoleId().longValue()){
                            r.setMp(v.getMp());
                        }
                    }
                }
            });

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
