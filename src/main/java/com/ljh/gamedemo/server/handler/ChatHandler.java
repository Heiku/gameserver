package com.ljh.gamedemo.server.handler;

import com.ljh.gamedemo.proto.protoc.MsgChatProto;
import com.ljh.gamedemo.service.ChatService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.ljh.gamedemo.server.request.RequestChatType.*;

/**
 * @Author: Heiku
 * @Date: 2019/8/6
 */
public class ChatHandler extends SimpleChannelInboundHandler<MsgChatProto.RequestChat> {

    private static ChatService chatService;

    static {
        chatService = SpringUtil.getBean(ChatService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgChatProto.RequestChat request) throws Exception {
        int type = request.getType().getNumber();
        Channel channel = ctx.channel();
        switch (type){
            case CHAT:
                chatService.chatRole(request, channel);
                break;
            case CHAT_GROUP:
                chatService.chatAllGroup(request, channel);
                break;
        }
    }
}
