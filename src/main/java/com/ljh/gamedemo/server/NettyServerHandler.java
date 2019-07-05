package com.ljh.gamedemo.server;

import com.ljh.gamedemo.message.HeartbeatResponsePacket;
import com.ljh.gamedemo.proto.MessageBase;
import com.ljh.gamedemo.service.EntityService;
import com.ljh.gamedemo.service.SiteService;
import com.ljh.gamedemo.util.DateUtil;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;


@Slf4j
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<MessageBase.Message> {

    @Autowired
    private static EntityService entityService;

    @Autowired
    private static SiteService siteService;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageBase.Message message) throws Exception {

    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    // 消息组装
    public static MessageBase.Message transformMessage(String content){
        return MessageBase.Message.newBuilder()
                .setContent(content)
                .build();
    }
}
