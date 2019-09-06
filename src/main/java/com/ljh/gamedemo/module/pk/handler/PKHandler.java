package com.ljh.gamedemo.module.pk.handler;

import com.ljh.gamedemo.proto.protoc.MsgPKProto;
import com.ljh.gamedemo.module.pk.service.PKService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.ljh.gamedemo.server.request.RequestPKType.*;

/**
 * @Author: Heiku
 * @Date: 2019/8/12
 *
 * PK请求处理器
 */
public class PKHandler extends SimpleChannelInboundHandler<MsgPKProto.RequestPK> {

    private static PKService pkService;

    static {
        pkService = SpringUtil.getBean(PKService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgPKProto.RequestPK req) throws Exception {
        int type = req.getTypeValue();
        switch (type){
            case PK:
                pkService.pk(req, ctx.channel());
                break;
            case AC:
                pkService.acceptChallenge(req, ctx.channel());
                break;
            case SPR:
                pkService.spellRole(req, ctx.channel());
                break;
            case ESCAPE:
                pkService.escape(req, ctx.channel());
                break;
        }
    }
}
