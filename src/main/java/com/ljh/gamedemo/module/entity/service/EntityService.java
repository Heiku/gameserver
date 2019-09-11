package com.ljh.gamedemo.module.entity.service;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.module.base.service.ProtoService;
import com.ljh.gamedemo.module.creep.bean.Creep;
import com.ljh.gamedemo.module.creep.local.LocalCreepMap;
import com.ljh.gamedemo.module.entity.bean.Entity;
import com.ljh.gamedemo.module.entity.local.LocalEntityMap;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.site.local.LocalSiteMap;
import com.ljh.gamedemo.module.user.local.LocalUserMap;
import com.ljh.gamedemo.proto.protoc.MsgEntityInfoProto;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 实体服务类
 */

@Slf4j
@Component
public class EntityService {

    /**
     * 协议服务
     */
    @Autowired
    private ProtoService protoService;

    /**
     * 实体协议返回
     */
    private MsgEntityInfoProto.ResponseEntityInfo entityResp;


    /**
     * 获取当前用户下，当前位置的所有实体信息
     *
     * @param req       请求
     */
    public void getAoi(MsgEntityInfoProto.RequestEntityInfo req, Channel channel){
        // 获取玩家信息
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());

        // 获取当前角色的场景
        int siteId = role.getSiteId();
        String siteName = LocalSiteMap.idSiteMap.get(siteId).getName();

        // 获取当前场景下的所有npc
        List<Entity> entityList = LocalEntityMap.siteEntityMap.get(siteName);

        // 获取当前场景下的所有野怪信息
        List<Creep> creepList = LocalCreepMap.getSiteCreepMap().get(siteId);

        // 添加真实的玩家角色信息：
        List<Role> roleList = LocalUserMap.siteRolesMap.get(siteId);

        // 消息返回
        sendEntityMsg(roleList, entityList, creepList, channel);
    }


    /**
     * 发送
     *
     * @param roles         玩家列表
     * @param entities      实体列表
     * @param creeps        野怪列表
     */
    private void sendEntityMsg(List<Role> roles, List<Entity> entities, List<Creep> creeps, Channel channel){
        entityResp  = MsgEntityInfoProto.ResponseEntityInfo.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.ENTITY_FIND_ALL)
                .addAllRole(protoService.transToRoleList(roles))
                .addAllEntity(protoService.transToEntityList(entities))
                .addAllCreep(protoService.transToCreepList(creeps))
                .build();
        channel.writeAndFlush(entityResp);
    }
}
