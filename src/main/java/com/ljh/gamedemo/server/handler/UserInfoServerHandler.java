package com.ljh.gamedemo.server.handler;

import com.ljh.gamedemo.proto.UserInfoProto;
import com.ljh.gamedemo.service.UserService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.ljh.gamedemo.common.RequestUserInfoType.*;

@Component
public class UserInfoServerHandler extends SimpleChannelInboundHandler<UserInfoProto.RequestUserInfo> {

    @Autowired
    private UserService userService;

    private UserInfoProto.ResponseUserInfo responseUserInfo;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, UserInfoProto.RequestUserInfo requestUserInfo) throws Exception {

        // TODO: 这里先暂时这样写，如果后期再再再细分协议的时候，就需要修改
        int requestType = requestUserInfo.getType().getNumber();
        switch (requestType){
            case LOGIN:
                responseUserInfo = userService.login(requestUserInfo);
                break;
            case REGISTER:
                responseUserInfo = userService.register(requestUserInfo);
                break;
            case STATE:
                responseUserInfo = userService.getState(requestUserInfo);
                break;
            case EXIT:
                responseUserInfo = userService.exit(requestUserInfo);
                break;
        }

        // 返回消息
        channelHandlerContext.channel().writeAndFlush(responseUserInfo);
    }
}
