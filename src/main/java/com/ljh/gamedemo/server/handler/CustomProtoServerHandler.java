package com.ljh.gamedemo.server.handler;

import com.ljh.gamedemo.proto.MessageBase;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Date;

public class CustomProtoServerHandler extends SimpleChannelInboundHandler<MessageBase.Message> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageBase.Message message) throws Exception {
        System.out.println(new Date());
        System.out.println(message);
    }


}
