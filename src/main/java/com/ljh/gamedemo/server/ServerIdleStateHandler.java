package com.ljh.gamedemo.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * 定义检测到假死连接之后的逻辑
 */
public class ServerIdleStateHandler extends IdleStateHandler {

    private static final int READER_IDLE_TIME = 15;

    public ServerIdleStateHandler(){
        super(READER_IDLE_TIME, 0, 0, TimeUnit.SECONDS);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        System.out.println(READER_IDLE_TIME + "秒内未读到数据，关闭连接，不！其实还没关闭");

        //ctx.channel().close();
    }

}