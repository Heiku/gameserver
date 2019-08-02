package com.ljh.gamedemo.server.handler;

import com.ljh.gamedemo.proto.protoc.MsgMallProto;
import com.ljh.gamedemo.service.MallService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author: Heiku
 * @Date: 2019/8/2
 */
public class MallHandler extends SimpleChannelInboundHandler<MsgMallProto.RequestMall> {

    private static MallService mallService;

    private MsgMallProto.ResponseMall response;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgMallProto.RequestMall request) throws Exception {
        int type = request.getType().getNumber();

    }
}
