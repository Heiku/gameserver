package com.ljh.gamedemo.module.duplicate.handler;

import com.ljh.gamedemo.module.user.service.UserService;
import com.ljh.gamedemo.proto.protoc.MsgDuplicateProto;
import com.ljh.gamedemo.module.duplicate.service.DuplicateService;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.ljh.gamedemo.server.request.RequestDuplicateType.*;

/**
 * 副本请求处理器
 *
 * @Author: Heiku
 * @Date: 2019/7/30
 */
public class DuplicateHandler extends SimpleChannelInboundHandler<MsgDuplicateProto.RequestDuplicate> {

    /**
     * 用户服务
     */
    private static UserService userService;

    /**
     * 副本服务
     */
    private static DuplicateService duplicateService;

    static {
        userService = SpringUtil.getBean(UserService.class);
        duplicateService = SpringUtil.getBean(DuplicateService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgDuplicateProto.RequestDuplicate request) throws Exception {
        MsgUserInfoProto.ResponseUserInfo userResp = userService.userStateInterceptor(request.getUserId());
        if (userResp != null){
            ctx.channel().writeAndFlush(userResp);
            return;
        }

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
    }
}
