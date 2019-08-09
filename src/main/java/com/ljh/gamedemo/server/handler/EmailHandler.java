package com.ljh.gamedemo.server.handler;

import com.ljh.gamedemo.proto.protoc.MsgEmailProto;
import com.ljh.gamedemo.service.EmailService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.ljh.gamedemo.server.request.RequestEmailType.EMAIL;
import static com.ljh.gamedemo.server.request.RequestEmailType.RECEIVE;

/**
 * @Author: Heiku
 * @Date: 2019/8/8
 *
 * 邮件相关 Handler
 */
public class EmailHandler extends SimpleChannelInboundHandler<MsgEmailProto.RequestEmail> {

    private static EmailService emailService;

    static {
        emailService = SpringUtil.getBean(EmailService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgEmailProto.RequestEmail request) throws Exception {
        int type = request.getType().getNumber();

        switch (type){
            case RECEIVE:
                emailService.receiveEmail(request, ctx.channel());
                break;
            case EMAIL:
                emailService.getAllEmail(request, ctx.channel());
                break;
        }
    }
}
