package com.ljh.gamedemo.server;

import com.ljh.gamedemo.service.SaveDataService;
import com.ljh.gamedemo.service.SiteService;
import com.ljh.gamedemo.util.SessionUtil;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * 定义检测到假死连接之后的逻辑
 */
@Slf4j
public class ServerIdleStateHandler extends IdleStateHandler {

    private static final int READER_IDLE_TIME = 60;

    @Autowired
    private static SaveDataService saveDataService;

    static {
        saveDataService = SpringUtil.getBean(SaveDataService.class);
    }

    public ServerIdleStateHandler(){
        super(READER_IDLE_TIME, 0, 0, TimeUnit.SECONDS);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 每次连接服务器都将保存userId, channel 关联
        Channel channel = ctx.channel();

        long userId = SessionUtil.getUserId(channel);
        SessionUtil.bindSession(userId, ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        log.warn(channel.toString() + "连接断开! ");

        // 用户数据落地
        saveDataService.leaveSaveUserData(channel);

        SessionUtil.unBindSession(channel);
    }
}