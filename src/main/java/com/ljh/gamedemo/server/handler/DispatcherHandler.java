package com.ljh.gamedemo.server.handler;

import com.google.protobuf.Message;
import com.ljh.gamedemo.server.codec.local.LocalMessageMap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;
import java.util.Map;

import static com.ljh.gamedemo.server.codec.MessageType.*;

/**
 * @Author: Heiku
 * @Date: 2019/7/18
 */

@ChannelHandler.Sharable
public class DispatcherHandler extends SimpleChannelInboundHandler<Message> {

    public static final DispatcherHandler INSTANCE = new DispatcherHandler();

    private Map<Integer, SimpleChannelInboundHandler<? extends Message>> handlerMap;

    private DispatcherHandler(){
        handlerMap = new HashMap<>();

        handlerMap.put(MESSAGE_PROTO.protoCode, new CommonHandler());
        handlerMap.put(REQUEST_USERINFO_PROTO.protoCode, new UserInfoHandler());
        handlerMap.put(REQUEST_SITE_PROTO.protoCode, new SiteInfoHandler());
        handlerMap.put(REQUEST_ENTITY_PROTO.protoCode, new EntityInfoHandler());
        handlerMap.put(REQUEST_TALK_ENTITY.protoCode, new TalkEntityHandler());
        handlerMap.put(REQUEST_SPELL.protoCode, new SpellHandler());
        handlerMap.put(REQUEST_ATTACK_SPELL.protoCode, new AttackCreepHandler());
        handlerMap.put(REQUEST_ITEMS.protoCode, new ItemsHandler());
        handlerMap.put(REQUEST_EQUIPS.protoCode, new EquipHandler());
        handlerMap.put(REQUEST_DUPLICATE.protoCode, new DuplicateHandler());
        handlerMap.put(REQUEST_MALL.protoCode, new MallHandler());
        handlerMap.put(REQUEST_CHAT.protoCode, new ChatHandler());
        handlerMap.put(REQUEST_EMAIL.protoCode, new EmailHandler());
        handlerMap.put(REQUEST_PK.protoCode, new PKHandler());
        handlerMap.put(REQUEST_GROUP.protoCode, new GroupHandler());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception{

        Integer protoNum = LocalMessageMap.messageMap.get(msg.getClass());
        handlerMap.get(protoNum).channelRead(ctx, msg);
    }
}
