package com.ljh.gamedemo.module.talk.service;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.module.entity.bean.Entity;
import com.ljh.gamedemo.module.event.BaseEvent;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.talk.bean.TalkText;
import com.ljh.gamedemo.module.entity.local.LocalEntityMap;
import com.ljh.gamedemo.module.talk.event.base.TalkEvent;
import com.ljh.gamedemo.module.talk.local.LocalTalkTextMap;
import com.ljh.gamedemo.module.user.local.LocalUserMap;
import com.ljh.gamedemo.proto.protoc.TalkEntityProto;
import com.ljh.gamedemo.module.base.service.ProtoService;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class TalkEntityService {

    /**
     * 协议服务
     */
    @Autowired
    private ProtoService protoService;


    /**
     * 事件发布者
     */
    @Autowired
    private ApplicationEventPublisher publisher;

    /**
     * 交流协议返回
     */
    private TalkEntityProto.ResponseTalkEntity resp;

    /**
     * 获取 npc 的对话
     *
     * @param req   请求
     * @return
     */
    public void talkNpc(TalkEntityProto.RequestTalkEntity req, Channel channel){
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());

        // 读取请求request中的npcName
        String npcName = req.getEntityName().trim();

        // 构造proto，准备返回
        Entity entity = LocalEntityMap.entityMap.get(npcName);

        // 获取npc的对话信息
        List<TalkText> talkTextList = LocalTalkTextMap.getTalkTextMap().get(npcName);
        if (talkTextList == null || talkTextList.isEmpty()){
            sendFailedMsg(channel, ContentType.TALK_EMPTY);
            return;
        }

        // 排序进行level降序，找到一个等级符合的对话
        Collections.sort(talkTextList);

        // 筛选符合条件的对话内容
        Optional<TalkText> result = talkTextList.stream().filter(e -> role.getLevel() >= e.getLevel()).findFirst();
        if (result.isPresent()){
            TalkText talkText = result.get();
            // 返回消息
            resp =  TalkEntityProto.ResponseTalkEntity.newBuilder()
                    .setResult(ResultCode.SUCCESS)
                    .setEntity(protoService.transToEntity(entity))
                    .setFirst(talkText.getFirst())
                    .setContent(talkText.getContent())
                    .setRole(protoService.transToRole(role))
                    .build();
            channel.writeAndFlush(resp);
        }

        // 发送事件通知
        publisher.publishEvent(new TalkEvent(new BaseEvent(role, entity.getId())));
    }


    /**
     * 构造失败协议返回
     *
     * @param msg   消息
     */
    private void sendFailedMsg(Channel channel, String msg){
        resp =  TalkEntityProto.ResponseTalkEntity.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(msg)
                .build();
        channel.writeAndFlush(resp);
    }
}
