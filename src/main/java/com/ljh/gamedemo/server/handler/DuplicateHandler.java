package com.ljh.gamedemo.server.handler;

import com.ljh.gamedemo.proto.protoc.MsgDuplicateProto;
import com.ljh.gamedemo.service.DuplicateService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.ljh.gamedemo.server.request.RequestDuplicateType.*;

/**
 * @Author: Heiku
 * @Date: 2019/7/30
 */
public class DuplicateHandler extends SimpleChannelInboundHandler<MsgDuplicateProto.RequestDuplicate> {

    private static DuplicateService duplicateService;

    private MsgDuplicateProto.ResponseDuplicate response;

    static {
        duplicateService = SpringUtil.getBean(DuplicateService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgDuplicateProto.RequestDuplicate request) throws Exception {
        int type = request.getType().getNumber();
        switch (type){
            case DUPLICATE:
                duplicateService.getDuplicate(request, ctx.channel());
                break;
            case ENTER:
                duplicateService.enterDuplicate(request, ctx.channel());
                break;
            case SPELL:
                duplicateService.spellBoss(request, ctx.channel());
                break;
            case STOP:
                duplicateService.stopAttack(request, ctx.channel());
                break;
            case LEAVE:
                duplicateService.leaveDuplicate(request, ctx.channel());
                break;
        }
        if (response != null) {
            ctx.writeAndFlush(response);
        }
    }
}
