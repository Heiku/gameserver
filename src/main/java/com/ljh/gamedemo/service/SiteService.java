package com.ljh.gamedemo.service;

import com.google.common.base.Strings;
import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.dao.UserRoleDao;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.local.LocalSiteMap;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.proto.MessageBase;
import com.ljh.gamedemo.entity.Site;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SiteService {

    @Autowired
    private UserRoleDao userRoleDao;

    // 判断能否到达指定的目的地
    // now -> destination
    public  boolean getDestination(long userId, Role role, String nowSiteName, String destination){

        // destination = cname, need to change ename
        if(!LocalSiteMap.nameMap.containsKey(destination)){
            return false;
        }
        String destinationEname = LocalSiteMap.nameMap.get(destination);
        List<String> destinationList = LocalSiteMap.siteMap.get(nowSiteName).getNext();

        // 相邻列表判断是否存在目的地
        for (String nextStr : destinationList){
            if (nextStr.equals(destinationEname)){

                // 获取对应目的地场景的id
                Site site = LocalSiteMap.siteMap.get(destinationEname);
                int siteId = site.getId();

                // 更新role
                role.setSiteId(siteId);

                // 更新LocalMap
                LocalUserMap.userRoleMap.put(userId, role);
                List<Role> rolesList = LocalUserMap.siteRolesMap.get(siteId);
                if (rolesList == null){
                    rolesList = new ArrayList<>();
                    rolesList.add(role);
                }else {
                    // 更新rolesList中的role，如果没有则添加进去
                    for (Role r : rolesList){
                        if (r.getRoleId() == role.getRoleId()){
                            r.setSiteId(siteId);

                            return true;
                        }
                    }
                    rolesList.add(role);
                }

                // 最后将siteRolesMap更新
                LocalUserMap.siteRolesMap.put(siteId, rolesList);
                return true;
            }
        }
        return false;
    }

    // 获取相邻列表
    private static List<String> getNext(String now){
        Site destinationSite = LocalSiteMap.siteMap.get(now);
        List<String> eNameList = destinationSite.getNext();
        List<String> cNameList = new ArrayList<>();

        for (String name : eNameList){
            cNameList.add(new SiteService().transformCName(name));
        }
        return cNameList;

    }

    /**
     * move指令
     *
     * 1.先判断当前用户的位置
     * 2.判断位置是否相邻
     * 3.移动后将数据记录
     *
     * @param message
     * @return
     */
    public MessageBase.Message move(MessageBase.Message message){
        // 先判断用户的信息
        long userId = message.getUserId();
        if (userId <= 0){
            return MessageBase.Message.newBuilder()
                    .setContent(ContentType.USER_EMPTY_TOKEN)
                    .build();
        }

        // 获取对应的目的地
        String destination = message.getContent();
        if (Strings.isNullOrEmpty(destination)){
            return MessageBase.Message.newBuilder()
                    .setContent(ContentType.MOVE_EMPTY)
                    .build();
        }

        // 判断是否能够到达
        // 先查缓存，在查数据库
        Role role = null;
        role = LocalUserMap.userRoleMap.get(userId);
        if (role == null){
            role = userRoleDao.selectUserRole(userId).get(0);
        }
        // 数据库还是查不到，直接返回结果
        if (role == null){
            return MessageBase.Message.newBuilder()
                    .setContent(ContentType.ROLE_EMPTY)
                    .build();
        }
        // 获取当前场景名
        String siteName = LocalSiteMap.idSiteMap.get(role.getSiteId()).getName();
        if (getDestination(userId, role,siteName, destination)){
            return MessageBase.Message.newBuilder()
                    .setContent("成功到达" + destination + "\n" +
                            "可到达附近地点：" + getNext(siteName).toString())
                    .build();
        }

        return MessageBase.Message.newBuilder()
                .setContent("无法到达" + destination + "，原因是：场景间不相邻。\n" +
                        "可到达附近地点：" + getNext(siteName).toString())
                .build();
    }


    public String getNowSiteCName(MessageBase.Message msg){
        long userId = msg.getUserId();
        if (userId <= 0){
            return ContentType.USER_EMPTY_DATA;
        }

        // 玩家角色存在判断
        Role role = null;
        role = LocalUserMap.userRoleMap.get(userId);
        if (role == null){
            role = userRoleDao.selectUserRole(userId).get(0);
        }
        if (role == null){
            return ContentType.ROLE_EMPTY;
        }

        // 如果存在
        int siteId = role.getSiteId();
        Site site = LocalSiteMap.idSiteMap.get(siteId);

        return site.getCName();
    }

    // 将name -> cname
    public String transformCName(String name){
        if (!LocalSiteMap.siteMap.containsKey(name)){
            return null;
        }

        return LocalSiteMap.siteMap.get(name).getCName();
    }
}
