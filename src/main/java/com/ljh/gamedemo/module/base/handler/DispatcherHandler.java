package com.ljh.gamedemo.module.base.handler;

import com.google.protobuf.Message;
import com.ljh.gamedemo.module.chat.handler.ChatHandler;
import com.ljh.gamedemo.module.creep.handler.AttackCreepHandler;
import com.ljh.gamedemo.module.duplicate.handler.DuplicateHandler;
import com.ljh.gamedemo.module.email.handler.EmailHandler;
import com.ljh.gamedemo.module.entity.handler.EntityInfoHandler;
import com.ljh.gamedemo.module.equip.handler.EquipHandler;
import com.ljh.gamedemo.module.face.handler.FaceTransactionHandler;
import com.ljh.gamedemo.module.group.handler.GroupHandler;
import com.ljh.gamedemo.module.guild.handler.GuildHandler;
import com.ljh.gamedemo.module.items.handler.ItemsHandler;
import com.ljh.gamedemo.module.mall.handler.MallHandler;
import com.ljh.gamedemo.module.pk.handler.PKHandler;
import com.ljh.gamedemo.module.role.handler.RoleHandler;
import com.ljh.gamedemo.module.site.handler.SiteInfoHandler;
import com.ljh.gamedemo.module.spell.handler.SpellHandler;
import com.ljh.gamedemo.module.talk.handler.TalkEntityHandler;
import com.ljh.gamedemo.module.task.handler.TaskHandler;
import com.ljh.gamedemo.module.trade.handler.TradeHandler;
import com.ljh.gamedemo.server.codec.local.LocalMessageMap;
import com.ljh.gamedemo.module.user.handler.UserInfoHandler;
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
        handlerMap.put(REQUEST_ROLE.protoCode, new RoleHandler());
        handlerMap.put(REQUEST_FACE_TRANS.protoCode, new FaceTransactionHandler());
        handlerMap.put(REQUEST_GUILD.protoCode, new GuildHandler());
        handlerMap.put(REQUEST_TRADE.protoCode, new TradeHandler());
        handlerMap.put(REQUEST_TASK.protoCode, new TaskHandler());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception{

        Integer protoNum = LocalMessageMap.messageMap.get(msg.getClass());
        handlerMap.get(protoNum).channelRead(ctx, msg);
    }
}
