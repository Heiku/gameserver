package com.ljh.gamedemo.server.handler;

import com.ljh.gamedemo.proto.protoc.MessageBase;
import com.ljh.gamedemo.service.BaseService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import static com.ljh.gamedemo.server.request.RequestCommonType.DATE;
import static com.ljh.gamedemo.server.request.RequestCommonType.HEARTBEAT_REQUEST;

@Slf4j
public class CommonHandler extends SimpleChannelInboundHandler<MessageBase.Message> {

    // 心跳丢失计数器
    //private int counter;

    @Autowired
    private static BaseService baseService;

    private MessageBase.Message msg;

    static {
        baseService = SpringUtil.getBean(BaseService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageBase.Message message) throws Exception {
        int requestType = message.getType().getNumber();
        switch (requestType){
            // 心跳包
            case HEARTBEAT_REQUEST:
                msg = baseService.sendHeartBeatResponse();
                break;
            case DATE:
                msg = baseService.getDate();
                break;
        }

        ctx.writeAndFlush(message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("client 连接出现异常");
    }

}
