package com.ljh.gamedemo.server.handler;

import com.ljh.gamedemo.proto.protoc.MsgEmailProto;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.service.EmailService;
import com.ljh.gamedemo.service.UserService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.ljh.gamedemo.server.request.RequestEmailType.EMAIL;
import static com.ljh.gamedemo.server.request.RequestEmailType.RECEIVE;

/**
 * 邮件请求处理器
 *
 * @Author: Heiku
 * @Date: 2019/8/8
 */
public class EmailHandler extends SimpleChannelInboundHandler<MsgEmailProto.RequestEmail> {

    /**
     * 邮件服务
     */
    private static EmailService emailService;

    /**
     * 用户服务
     */
    private static UserService userService;

    /**
     * 用户协议返回
     */
    private MsgUserInfoProto.ResponseUserInfo userResp;

    static {
        userService = SpringUtil.getBean(UserService.class);
        emailService = SpringUtil.getBean(EmailService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgEmailProto.RequestEmail request) throws Exception {
        userResp = userService.userStateInterceptor(request.getUserId());
        if (userResp != null){
            ctx.channel().writeAndFlush(userResp);
            return;
        }

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
