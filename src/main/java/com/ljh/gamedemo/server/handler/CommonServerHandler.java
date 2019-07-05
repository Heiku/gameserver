package com.ljh.gamedemo.server.handler;

import com.ljh.gamedemo.proto.MessageBase;
import com.ljh.gamedemo.service.BaseService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;

import static com.ljh.gamedemo.server.request.RequestCommonType.*;

public class CommonServerHandler extends SimpleChannelInboundHandler<MessageBase.Message> {

    @Autowired
    private static BaseService baseService;

    private MessageBase.Message message;

    static {
        baseService = SpringUtil.getBean(BaseService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageBase.Message message) throws Exception {
        int requestType = message.getType().getNumber();
        switch (requestType){
            case DATE:
                message = baseService.getDate();
                break;
        }

        ctx.writeAndFlush(message);
    }


}
