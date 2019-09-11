package com.ljh.gamedemo.module.spell.handler;

import com.ljh.gamedemo.module.user.service.UserService;
import com.ljh.gamedemo.proto.protoc.MsgSpellProto;
import com.ljh.gamedemo.module.spell.service.SpellService;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static com.ljh.gamedemo.server.request.RequestSpellType.*;

/**
 * 技能请求处理器
 *
 * @Author: Heiku
 * @Date: 2019/7/11
 */

@Slf4j
public class SpellHandler extends SimpleChannelInboundHandler<MsgSpellProto.RequestSpell> {

    /**
     * 技能服务
     */
    private static SpellService spellService;


    /**
     * 用户服务
     */
    private static UserService userService;

    static {
        userService = SpringUtil.getBean(UserService.class);
        spellService = SpringUtil.getBean(SpellService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgSpellProto.RequestSpell req) throws Exception {
        // 用户判断
        MsgUserInfoProto.ResponseUserInfo userResp = userService.userStateInterceptor(req.getUserId());
        if (!Objects.isNull(userResp)){
            ctx.channel().writeAndFlush(userResp);
            return;
        }


        int type = req.getType().getNumber();
        switch (type){
            case SPELL:
                spellService.getSpell(req, ctx.channel());
                break;
            case LEARN:
                spellService.learn(req, ctx.channel());
                break;
        }
    }
}
