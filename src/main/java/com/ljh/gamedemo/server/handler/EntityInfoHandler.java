package com.ljh.gamedemo.server.handler;

import com.ljh.gamedemo.proto.protoc.MsgEntityInfoProto;
import com.ljh.gamedemo.service.EntityService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;

import static com.ljh.gamedemo.server.request.RequestEntityInfoType.*;

public class EntityInfoHandler extends SimpleChannelInboundHandler<MsgEntityInfoProto.RequestEntityInfo> {

    @Autowired
    private static EntityService entityService;

    private MsgEntityInfoProto.ResponseEntityInfo responseEntityInfo;

    static {
        entityService = SpringUtil.getBean(EntityService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgEntityInfoProto.RequestEntityInfo requestEntityInfo) throws Exception {
        // 获取请求类型
        int requestType = requestEntityInfo.getType().getNumber();
        switch (requestType){
            case AOI:
                responseEntityInfo = entityService.getAoi(requestEntityInfo);
                break;
        }

        ctx.writeAndFlush(responseEntityInfo);
    }
}
