package com.ljh.gamedemo.service;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.entity.Entity;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.entity.TalkText;
import com.ljh.gamedemo.local.LocalEntityMap;
import com.ljh.gamedemo.local.LocalTalkTextMap;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.proto.protoc.EntityProto;
import com.ljh.gamedemo.proto.protoc.RoleProto;
import com.ljh.gamedemo.proto.protoc.TalkEntityProto;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class TalkEntityService {

    /**
     * 获取 npc 的对话
     *
     * @param requestTalkEntity
     * @return
     */
    public TalkEntityProto.ResponseTalkEntity talkNpc(TalkEntityProto.RequestTalkEntity requestTalkEntity){

        // 用户id标识判断
        long userId = requestTalkEntity.getUserId();
        if (userId <= 0){
            return TalkEntityProto.ResponseTalkEntity.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.USER_TOKEN_DATA_EMPTY)
                    .build();
        }

        // 找不到对应的角色信息
        Role role = LocalUserMap.userRoleMap.get(userId);
        if (role == null){
            return TalkEntityProto.ResponseTalkEntity.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.ROLE_EMPTY)
                    .build();
        }

        // 读取请求request中的npcName
        String npcName = requestTalkEntity.getEntityName().trim();

        // 构造proto，准备返回
        Entity entity = LocalEntityMap.entityMap.get(npcName);
        EntityProto.Entity entityProto = EntityProto.Entity.newBuilder()
                .setName(entity.getName())
                .setId(entity.getId())
                .setType(entity.getType())
                .setAlive(entity.getAlive())
                .setLevel(entity.getLevel())
                .build();

        RoleProto.Role roleProto = RoleProto.Role.newBuilder()
                .setRoleId(role.getRoleId())
                .setName(role.getName())
                .setType(role.getType())
                .setLevel(role.getLevel())
                .setAlive(role.getAlive())
                .build();

        // 获取npc的对话信息
        List<TalkText> talkTextList = LocalTalkTextMap.getTalkTextMap().get(npcName);
        if (talkTextList == null || talkTextList.isEmpty()){
            return TalkEntityProto.ResponseTalkEntity.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.TALK_EMPTY)
                    .build();
        }


        // 这里根据role的level获取不同阶段的npc对话
        TalkText talkText = new TalkText();

        // 排序进行level降序，找到一个等级符合的对话
        Collections.sort(talkTextList);
        talkTextList.forEach(e -> {
            if (role.getLevel() >= e.getLevel()){
                try {
                    BeanUtils.copyProperties(talkText, e);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        // 返回消息
        return TalkEntityProto.ResponseTalkEntity.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setEntity(entityProto)
                .setFirst(talkText.getFirst())
                .setContent(talkText.getContent())
                .setRole(roleProto)
                .build();
    }
}
