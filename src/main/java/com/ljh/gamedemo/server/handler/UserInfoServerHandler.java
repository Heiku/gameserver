package com.ljh.gamedemo.server.handler;

import com.ljh.gamedemo.proto.UserInfoProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Date;

public class UserInfoServerHandler extends SimpleChannelInboundHandler<UserInfoProto.RequestUserInfo> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, UserInfoProto.RequestUserInfo requestUserInfo) throws Exception {
        System.out.println(new Date());

        System.out.println("这是来自 UserInfoServerHandler的爱");
        System.out.println(requestUserInfo);
    }
}
