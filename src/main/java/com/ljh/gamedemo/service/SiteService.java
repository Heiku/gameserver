package com.ljh.gamedemo.service;

import com.google.common.base.Strings;
import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.dao.UserRoleDao;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.local.LocalSiteMap;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.entity.Site;
import com.ljh.gamedemo.proto.MsgSiteInfoProto;
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

        // 获取当前地点的临近场景列表，用于下方进行位置判断能否到达
        List<String> destinationList = LocalSiteMap.siteMap.get(nowSiteName).getNext();

        // 相邻列表判断是否存在目的地
        for (String nextStr : destinationList){
            if (nextStr.equals(destinationEname)){

                // 获取对应目的地场景的id
                int desSiteId = LocalSiteMap.siteMap.get(destinationEname).getId();

                // 更新role的位置信息
                role.setSiteId(desSiteId);

                // 更新LocalUserRoleMap
                LocalUserMap.userRoleMap.put(userId, role);


                // 这里分为两部分：都是更新 siteRolesMap
                // 一个是更新目的地的所有角色信息，添加move 的role
                // 二是更新出发点的所有角色信息，移除move 的role

                // 这里是第一步,因为目的地中是没有当前角色的，所以直接加进来
                List<Role> desRoleList = LocalUserMap.siteRolesMap.get(desSiteId);
                if (desRoleList == null || desRoleList.isEmpty()){
                    desRoleList = new ArrayList<>();
                }
                desRoleList.add(role);

                // 最后更新 desSiteRoleMap 中的list信息
                LocalUserMap.siteRolesMap.put(desSiteId, desRoleList);


                // 这是第二步，将oldSite的roleList更新，即移除
                int nowSiteId = LocalSiteMap.siteMap.get(nowSiteName).getId();
                List<Role> startRoleList = LocalUserMap.siteRolesMap.get(nowSiteId);
                if (!startRoleList.isEmpty()) {
                    synchronized (this) {
                        for (Role r : startRoleList) {
                            if (r.getRoleId().longValue() == role.getRoleId()) {
                                startRoleList.remove(r);
                                break;
                            }
                        }
                    }
                }
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
     * @param requestSiteInfo
     * @return
     */
    public MsgSiteInfoProto.ResponseSiteInfo move(MsgSiteInfoProto.RequestSiteInfo requestSiteInfo){
        // 先判断用户的信息
        long userId = requestSiteInfo.getUserId();
        if (userId <= 0){
            return MsgSiteInfoProto.ResponseSiteInfo.newBuilder()
                    .setContent(ContentType.USER_EMPTY_TOKEN)
                    .build();
        }

        // 获取对应的目的地
        String destination = requestSiteInfo.getContent();
        if (Strings.isNullOrEmpty(destination)){
            return MsgSiteInfoProto.ResponseSiteInfo.newBuilder()
                    .setContent(ContentType.MOVE_EMPTY)
                    .build();
        }

        // 判断是否能够到达
        // 先查缓存，在查数据库
        Role role;
        role = LocalUserMap.userRoleMap.get(userId);
        if (role == null){
            role = userRoleDao.selectUserRole(userId).get(0);
        }
        // 数据库还是查不到，直接返回结果
        if (role == null){
            return MsgSiteInfoProto.ResponseSiteInfo.newBuilder()
                    .setContent(ContentType.ROLE_EMPTY)
                    .build();
        }
        // 获取当前场景名
        String siteName = LocalSiteMap.idSiteMap.get(role.getSiteId()).getName();

        // 判断能否到达目的地，到达后更新当前缓存位置信息
        if (getDestination(userId, role, siteName, destination)){

            // 重新获取当前位置信息
            Role r = LocalUserMap.userRoleMap.get(userId);
            String site = LocalSiteMap.idSiteMap.get(r.getSiteId()).getName();
            return MsgSiteInfoProto.ResponseSiteInfo.newBuilder()
                    .setContent("成功到达" + destination + "\n" +
                            "可到达附近地点：" + getNext(site).toString())
                    .build();
        }

        return MsgSiteInfoProto.ResponseSiteInfo.newBuilder()
                .setContent("无法到达" + destination + "，原因是：场景间不相邻。\n" +
                        "可到达附近地点：" + getNext(siteName).toString())
                .build();
    }


    /**
     * 获得当前角色的位置信息
     *
     * @param requestSiteInfo
     * @return
     */
    public MsgSiteInfoProto.ResponseSiteInfo getNowSiteCName(MsgSiteInfoProto.RequestSiteInfo requestSiteInfo){
        // 用户认证信息判断
        long userId = requestSiteInfo.getUserId();
        if (userId <= 0){
            return MsgSiteInfoProto.ResponseSiteInfo.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.USER_TOKEN_DATA_EMPTY)
                    .build();
        }

        // 玩家角色存在判断
        Role role = null;
        role = LocalUserMap.userRoleMap.get(userId);
        if (role == null){
            role = userRoleDao.selectUserRole(userId).get(0);
        }
        if (role == null){
            return MsgSiteInfoProto.ResponseSiteInfo.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.ROLE_EMPTY)
                    .build();
        }

        // 如果存在
        int siteId = role.getSiteId();
        Site site = LocalSiteMap.idSiteMap.get(siteId);

        // 返回消息
        String content = ContentType.SITE_NOW + site.getCName();

        return MsgSiteInfoProto.ResponseSiteInfo.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(content)
                .build();
    }

    // 将name -> cname
    public String transformCName(String name){
        if (!LocalSiteMap.siteMap.containsKey(name)){
            return null;
        }

        return LocalSiteMap.siteMap.get(name).getCName();
    }
}
