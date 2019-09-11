package com.ljh.gamedemo.module.chat.handler;

import com.ljh.gamedemo.module.user.service.UserService;
import com.ljh.gamedemo.proto.protoc.MsgChatProto;
import com.ljh.gamedemo.module.chat.service.ChatService;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.ljh.gamedemo.server.request.RequestChatType.*;

/**
 * 聊天请求处理器
 *
 * @Author: Heiku
 * @Date: 2019/8/6
 */
public class ChatHandler extends SimpleChannelInboundHandler<MsgChatProto.RequestChat> {

    /**
     * 用户服务
     */
    private static UserService userService;

    /**
     * 聊天服务
     */
    private static ChatService chatService;

    static {
        userService = SpringUtil.getBean(UserService.class);
        chatService = SpringUtil.getBean(ChatService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgChatProto.RequestChat request) throws Exception {
        MsgUserInfoProto.ResponseUserInfo userRep = userService.userStateInterceptor(request.getUserId());
        if (userRep != null){
            ctx.channel().writeAndFlush(userRep);
            return;
        }

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
