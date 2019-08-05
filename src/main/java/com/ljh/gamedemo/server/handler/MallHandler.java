package com.ljh.gamedemo.server.handler;

import com.ljh.gamedemo.proto.protoc.MsgMallProto;
import com.ljh.gamedemo.service.MallService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.ljh.gamedemo.server.request.RequestMallType.*;

/**
 * @Author: Heiku
 * @Date: 2019/8/2
 */
public class MallHandler extends SimpleChannelInboundHandler<MsgMallProto.RequestMall> {

    private static MallService mallService;

    private MsgMallProto.ResponseMall response;

    static {
        mallService = SpringUtil.getBean(MallService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgMallProto.RequestMall request) throws Exception {
        int type = request.getType().getNumber();
        switch (type){
            case MALL:
                response = mallService.getMall(request, ctx.channel());
                break;
            case BUY:
                response = mallService.buyMall(request, ctx.channel());
                break;
        }

        if (response != null){
            ctx.writeAndFlush(response);
        }
    }
}
