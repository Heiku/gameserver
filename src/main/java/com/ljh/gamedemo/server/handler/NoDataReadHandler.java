package com.ljh.gamedemo.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NoDataReadHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("没有数据接受，触发这里？？");

        //super.userEventTriggered(ctx, evt);
    }
}
