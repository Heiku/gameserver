package com.ljh.gamedemo.module.pk.handler;

import com.ljh.gamedemo.module.user.service.UserService;
import com.ljh.gamedemo.proto.protoc.MsgPKProto;
import com.ljh.gamedemo.module.pk.service.PKService;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Objects;

import static com.ljh.gamedemo.server.request.RequestPKType.*;

/**
 *  PK请求处理器
 *
 * @Author: Heiku
 * @Date: 2019/8/12
 */
public class PKHandler extends SimpleChannelInboundHandler<MsgPKProto.RequestPK> {

    /**
     * 用户服务
     */
    private static UserService userService;

    /**
     * pk服务
     */
    private static PKService pkService;

    static {
        userService = SpringUtil.getBean(UserService.class);
        pkService = SpringUtil.getBean(PKService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgPKProto.RequestPK req) throws Exception {
        // 用户判断
        MsgUserInfoProto.ResponseUserInfo userResp = userService.userStateInterceptor(req.getUserId());
        if (!Objects.isNull(userResp)){
            ctx.channel().writeAndFlush(userResp);
            return;
        }

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
