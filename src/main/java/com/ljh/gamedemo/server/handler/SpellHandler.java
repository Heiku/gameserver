package com.ljh.gamedemo.server.handler;

import com.ljh.gamedemo.proto.protoc.MsgSpellProto;
import com.ljh.gamedemo.service.SpellService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import static com.ljh.gamedemo.server.request.RequestSpellType.*;

/**
 * @Author: Heiku
 * @Date: 2019/7/11
 */

@Slf4j
public class SpellHandler extends SimpleChannelInboundHandler<MsgSpellProto.RequestSpell> {

    private static SpellService spellService;

    private MsgSpellProto.ResponseSpell responseSpell;

    static {
        spellService = SpringUtil.getBean(SpellService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgSpellProto.RequestSpell requestSpell) throws Exception {
        int type = requestSpell.getType().getNumber();
        switch (type){
            case SPELL:
                spellService.getSpell(requestSpell, ctx.channel());
                break;
            case LEARN:
                spellService.learn(requestSpell, ctx.channel());
                break;
        }
    }
}
