package com.ljh.gamedemo.server.handler;

import com.ljh.gamedemo.proto.protoc.MsgAttackCreepProto;
import com.ljh.gamedemo.service.AttackCreepService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import static com.ljh.gamedemo.server.request.RequestAttackCreepType.*;

/**
 * @Author: Heiku
 * @Date: 2019/7/12
 */

@Slf4j
public class AttackCreepHandler extends SimpleChannelInboundHandler<MsgAttackCreepProto.RequestAttackCreep> {

    private static AttackCreepService attackCreepService;

    private MsgAttackCreepProto.ResponseAttackCreep responseAttackCreep;

    static {
        attackCreepService = SpringUtil.getBean(AttackCreepService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgAttackCreepProto.RequestAttackCreep requestAttackCreep) throws Exception {
        int type = requestAttackCreep.getType().getNumber();
        switch (type){
            case ATTACK:
                responseAttackCreep = attackCreepService.attackCreep(requestAttackCreep, ctx.channel());
                break;
            case SPELL:
                responseAttackCreep = attackCreepService.spellAttackCreep(requestAttackCreep, ctx.channel());
                break;

        }

        if (responseAttackCreep != null) {
            ctx.writeAndFlush(responseAttackCreep);
        }
    }
}
