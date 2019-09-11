package com.ljh.gamedemo.module.base.handler;

import com.ljh.gamedemo.proto.protoc.MessageBase;
import com.ljh.gamedemo.module.base.service.BaseService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import static com.ljh.gamedemo.server.request.RequestCommonType.DATE;
import static com.ljh.gamedemo.server.request.RequestCommonType.HEARTBEAT_REQUEST;


/**
 * 心跳请求处理器
 */
@Slf4j
public class CommonHandler extends SimpleChannelInboundHandler<MessageBase.Message> {


    /**
     * 基础服务
     */
    @Autowired
    private static BaseService baseService;

    static {
        baseService = SpringUtil.getBean(BaseService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageBase.Message message) throws Exception {
        int requestType = message.getType().getNumber();
        Channel channel = ctx.channel();

        switch (requestType){
            // 心跳包
            case HEARTBEAT_REQUEST:
                baseService.sendHeartBeatResponse();
                break;
            case DATE:
                baseService.getDate(channel);
                break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("client 连接出现异常");
    }

}
