package com.ljh.gamedemo.module.site.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.site.bean.Site;
import com.ljh.gamedemo.module.site.local.LocalSiteMap;
import com.ljh.gamedemo.module.user.local.LocalUserMap;
import com.ljh.gamedemo.proto.protoc.MsgSiteInfoProto;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 场景移动操作
 */
@Service
public class SiteService {
    
    /**
     * 场景协议返回
     */
    private MsgSiteInfoProto.ResponseSiteInfo siteResp;


    /**
     * 判断能否到达指定的目的地
     *
     * @param role              玩家信息
     * @param nowSiteName       当前场景名
     * @param destination       目的地场景名
     * @return                  能否到达
     */
    public boolean getDestination(Role role, String nowSiteName, String destination){

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
                LocalUserMap.userRoleMap.put(role.getUserId(), role);


                // 这里分为两部分：都是更新 siteRolesMap
                // 一个是更新目的地的所有角色信息，添加move 的role
                // 二是更新出发点的所有角色信息，移除move 的role

                // 这里是第一步,因为目的地中是没有当前角色的，所以直接加进来
                List<Role> desRoleList = Optional.ofNullable(LocalUserMap.siteRolesMap.get(desSiteId))
                        .orElse(Lists.newArrayList());
                desRoleList.add(role);

                // 最后更新 desSiteRoleMap 中的list信息
                LocalUserMap.siteRolesMap.put(desSiteId, desRoleList);


                // 这是第二步，将oldSite的roleList更新，即移除
                int nowSiteId = LocalSiteMap.siteMap.get(nowSiteName).getId();
                List<Role> startRoleList = Optional.ofNullable(LocalUserMap.siteRolesMap.get(nowSiteId))
                        .orElse(Lists.newArrayList());
                startRoleList.removeIf(r -> r.getRoleId().longValue() == role.getRoleId());
                return true;
            }
        }
        return false;
    }


    /**
     * 获取相邻列表
     *
     * @param now       当前的位置名
     * @return          相邻的场景列表
     */
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
     * @param req       请求
     * @param channel   channel
     */
    public void move(MsgSiteInfoProto.RequestSiteInfo req, Channel channel){
        // 获取对应的目的地
        String destination = req.getContent();
        if (Strings.isNullOrEmpty(destination)){
            siteResp = combineSiteResp(ContentType.MOVE_EMPTY);
            channel.writeAndFlush(siteResp);
            return;
        }

        // 获取玩家信息
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());

        // 获取当前场景名
        String siteName = LocalSiteMap.idSiteMap.get(role.getSiteId()).getName();

        String content = "";
        // 判断能否到达目的地，到达后更新当前缓存位置信息
        if (getDestination(role, siteName, destination)){

            // 重新获取当前位置信息
            String site = LocalSiteMap.idSiteMap.get(role.getSiteId()).getName();
            content = "成功到达" + destination + "\n" + "可到达附近地点：" + getNext(site).toString();
            channel.writeAndFlush(combineSiteResp(content));
            return;
        }

        content = "无法到达" + destination + "，原因是：场景间不相邻。\n" +
                "可到达附近地点：" + getNext(siteName).toString();
        channel.writeAndFlush(combineSiteResp(content));
    }


    /**
     * 获得当前角色的位置信息
     *
     * @param req       请求
     * @param channel   channel
     */
    public void getNowSiteCName(MsgSiteInfoProto.RequestSiteInfo req, Channel channel){
        // 玩家角色存在判断
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());

        // 如果存在
        int siteId = role.getSiteId();
        Site site = LocalSiteMap.idSiteMap.get(siteId);

        // 返回消息
        String content = ContentType.SITE_NOW + site.getCName();

        channel.writeAndFlush(combineSiteResp(content));
    }


    /**
     * 判断两个玩家是否在相同的地点
     *
     * @param r1        玩家A
     * @param r2        玩家B
     * @return          是否处于同一场景中
     */
    public boolean inSameSite(Role r1, Role r2){
        return r1.getSiteId().intValue() == r2.getSiteId();
    }


    /**
     * 获取场景的中文名
     *
     * @param name      场景名
     * @return          场景中文名
     */
    private String transformCName(String name){
        if (!LocalSiteMap.siteMap.containsKey(name)){
            return null;
        }
        return LocalSiteMap.siteMap.get(name).getCName();
    }


    /**
     * 构造场景协议返回
     *
     * @param content       消息
     * @return              场景协议
     */
    private MsgSiteInfoProto.ResponseSiteInfo combineSiteResp(String content){
        return MsgSiteInfoProto.ResponseSiteInfo.newBuilder()
                .setContent(content)
                .build();
    }
}
