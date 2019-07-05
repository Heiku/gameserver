package com.ljh.gamedemo.service;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.dao.UserRoleDao;
import com.ljh.gamedemo.entity.Entity;
import com.ljh.gamedemo.common.EntityType;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.local.LocalEntityMap;
import com.ljh.gamedemo.local.LocalSiteMap;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.proto.EntityProto;
import com.ljh.gamedemo.proto.MessageBase;
import com.ljh.gamedemo.proto.MsgEntityInfoProto;
import com.ljh.gamedemo.proto.RoleProto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.ljh.gamedemo.common.ContentType.USER_EMPTY_DATA;

/**
 * 角色 & npc 服务类
 *
 */
@Component
public class EntityService {

    @Autowired
    private UserRoleDao userRoleDao;

    public MessageBase.Message getAoi(MessageBase.Message message){

        // 根据用户的userId，获取roleId, 再从服务端获取site信息  (当前一个玩家只有一个角色role)
        // 先从缓存中读取，读不到在去查找数据库
        long userId = message.getUserId();
        if (userId <= 0){
            return MessageBase.Message.newBuilder()
                    .setContent(USER_EMPTY_DATA)
                    .build();
        }

        // 缓存读，再数据库
        Role role = null;
        if (LocalUserMap.userRoleMap.containsKey(userId)){
            role = LocalUserMap.userRoleMap.get(userId);
        }else {
            // TODO:这里是直接数据库中读取一个角色，当一个用户对应多个角色时，这里需要修改以西
            role = userRoleDao.selectUserRole(userId).get(0);
        }

        if (role == null){
            return MessageBase.Message.newBuilder()
                    .setContent(ContentType.ROLE_EMPTY)
                    .build();
        }

        // 获取当前角色的场景
        int siteId = role.getSiteId();
        String siteName = LocalSiteMap.idSiteMap.get(siteId).getName();

        // 返回消息builder
        StringBuilder sb = new StringBuilder("当前场景的实体对象有：\n");
        if (!LocalEntityMap.entitySiteMap.containsKey(siteName)){
            sb.append("当前场景不存在");
        }

        List<Entity> entityList = LocalEntityMap.entitySiteMap.get(siteName);
        if (entityList.isEmpty()){
            sb.append("暂无实体npc");
        }

        // 添加真实的玩家角色信息：
        List<Role> roleList = LocalUserMap.siteRolesMap.get(siteId);
        if (!roleList.isEmpty()){
            for (Role r : roleList){
                String playerType = EntityType.getContentFromCode(r.getType()).getContent();
                String playerLevel = String.valueOf(r.getLevel());
                String playerAlive = r.getAlive() == 1 ? "存活" : "死亡";
                sb.append("玩家：" + r.getName() + " 职业：" + playerType + " 等级：" + playerLevel + " 状态：" + playerAlive + " \n");
            }
        }

        // 添加场景中的所有npc信息：
        for (Entity entity : entityList){
            String name = entity.getName();
            String type = EntityType.getContentFromCode(entity.getType()).getContent();
            String level = String.valueOf(entity.getLevel());
            String alive = entity.getAlive() == 1 ? "存活" : "死亡";

            sb.append("名称：" + name + " 职业：" + type + " 等级：" + level + " 状态：" + alive + " \n");
        }

        return MessageBase.Message.newBuilder()
                .setContent(sb.toString())
                .build();
    }


    /**
     * 获取当前用户下，当前位置的所有实体信息
     *
     * @param requestEntityInfo
     * @return
     */
    public MsgEntityInfoProto.ResponseEntityInfo getAoi(MsgEntityInfoProto.RequestEntityInfo requestEntityInfo){

        // 定义返回格式
        MsgEntityInfoProto.ResponseEntityInfo responseEntityInfo = MsgEntityInfoProto.ResponseEntityInfo.getDefaultInstance();

        // 根据用户的userId，获取roleId, 再从服务端获取site信息  (当前一个玩家只有一个角色role)
        // 先从缓存中读取，读不到在去查找数据库
        long userId = requestEntityInfo.getUserId();
        if (userId <= 0){
            return MsgEntityInfoProto.ResponseEntityInfo.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(USER_EMPTY_DATA)
                    .build();
        }

        // 缓存读，再数据库
        Role role = null;
        if (LocalUserMap.userRoleMap.containsKey(userId)){
            role = LocalUserMap.userRoleMap.get(userId);
        }else {
            // TODO:这里是直接数据库中读取一个角色，当一个用户对应多个角色时，这里需要修改以西
            role = userRoleDao.selectUserRole(userId).get(0);
        }

        if (role == null){
            return MsgEntityInfoProto.ResponseEntityInfo.newBuilder()
                    .setContent(ContentType.ROLE_EMPTY)
                    .build();
        }

        // 获取当前角色的场景
        int siteId = role.getSiteId();
        String siteName = LocalSiteMap.idSiteMap.get(siteId).getName();


        // 获取当前场景下的所有npc
        List<Entity> entityList = LocalEntityMap.entitySiteMap.get(siteName);

        // 添加真实的玩家角色信息：
        // 组装 role
        List<Role> roleList = LocalUserMap.siteRolesMap.get(siteId);

        List<RoleProto.Role> roles = new ArrayList<>();
        if (!roleList.isEmpty()){
            for (Role r : roleList){
                RoleProto.Role roleProto = RoleProto.Role.newBuilder()
                        .setRoleId(r.getRoleId())
                        .setName(r.getName())
                        .setType(r.getType())
                        .setLevel(r.getLevel())
                        .setAlive(r.getAlive())
                        .build();

                roles.add(roleProto);
            }
        }

        // 添加场景中的所有npc信息：
        // 组装 entity
        List<EntityProto.Entity> entities = new ArrayList<>();
        for (Entity entity : entityList){
            EntityProto.Entity entityProto = EntityProto.Entity.newBuilder()
                    .setId(entity.getId())
                    .setName(entity.getName())
                    .setType(entity.getType())
                    .setLevel(entity.getLevel())
                    .setAlive(entity.getAlive())
                    .build();

            entities.add(entityProto);
        }

        return responseEntityInfo.toBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.ENTITY_FIND_ALL)
                .addAllRole(roles)
                .addAllEntity(entities)
                .build();
    }
}
