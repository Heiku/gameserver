package com.ljh.gamedemo.server.handler;

import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.service.UserService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;

import static com.ljh.gamedemo.server.request.RequestUserInfoType.*;


@ChannelHandler.Sharable
public class UserInfoHandler extends SimpleChannelInboundHandler<MsgUserInfoProto.RequestUserInfo> {

    @Autowired
    private static UserService userService;

    private MsgUserInfoProto.ResponseUserInfo responseUserInfo;

    static {
        userService = SpringUtil.getBean(UserService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgUserInfoProto.RequestUserInfo requestUserInfo) throws Exception {

        Channel channel = ctx.channel();

        int requestType = requestUserInfo.getType().getNumber();
        switch (requestType){
            case LOGIN:
                responseUserInfo = userService.login(channel, requestUserInfo);
                break;
            case REGISTER:
                responseUserInfo = userService.register(channel, requestUserInfo);
                break;
            case STATE:
                responseUserInfo = userService.getState(requestUserInfo);
                break;
            case EXIT:
                responseUserInfo = userService.exit(channel, requestUserInfo);
                break;
        }

        // 返回消息
        ctx.writeAndFlush(responseUserInfo);
    }
}
