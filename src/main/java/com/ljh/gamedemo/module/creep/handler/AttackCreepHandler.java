package com.ljh.gamedemo.module.creep.handler;

import com.ljh.gamedemo.proto.protoc.MsgAttackCreepProto;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.module.creep.service.AttackCreepService;
import com.ljh.gamedemo.module.user.service.UserService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import static com.ljh.gamedemo.server.request.RequestAttackCreepType.*;

/**
 * 攻击野怪请求处理器
 *
 * @Author: Heiku
 * @Date: 2019/7/12
 */

@Slf4j
public class AttackCreepHandler extends SimpleChannelInboundHandler<MsgAttackCreepProto.RequestAttackCreep> {

    /**
     * 用户服务
     */
    private static UserService userService;

    /**
     * 攻击野怪服务
     */
    private static AttackCreepService attackCreepService;

    static {
        userService = SpringUtil.getBean(UserService.class);
        attackCreepService = SpringUtil.getBean(AttackCreepService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgAttackCreepProto.RequestAttackCreep req) throws Exception {
        int type = req.getType().getNumber();
        Channel channel = ctx.channel();
        MsgUserInfoProto.ResponseUserInfo userResp;

        // 用户状态判断
        if ((userResp = userService.userStateInterceptor(req.getUserId())) != null){
            channel.writeAndFlush(userResp);
            return;
        }

        switch (type){
            case SPELL:
                attackCreepService.spellAttackCreep(req, ctx.channel());
                break;
            case STOP:
                attackCreepService.stopAttack(req, ctx.channel());
                break;
        }

    }
}
