package com.ljh.gamedemo.module.equip.handler;

import com.ljh.gamedemo.module.user.service.UserService;
import com.ljh.gamedemo.proto.protoc.MsgEquipProto;
import com.ljh.gamedemo.module.equip.service.EquipService;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static com.ljh.gamedemo.server.request.RequestEquipType.*;

/**
 * 装备请求处理器
 *
 * @Author: Heiku
 * @Date: 2019/7/24
 */

@Slf4j
public class EquipHandler extends SimpleChannelInboundHandler<MsgEquipProto.RequestEquip> {

    /**
     * 用户服务
     */
    private static UserService userService;

    /**
     * 装备服务
     */
    private static EquipService equipService;

    static {
        userService = SpringUtil.getBean(UserService.class);
        equipService = SpringUtil.getBean(EquipService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgEquipProto.RequestEquip request) throws Exception {
        // 用户判断
        MsgUserInfoProto.ResponseUserInfo userResp = userService.userStateInterceptor(request.getUserId());
        if (!Objects.isNull(userResp)){
            ctx.channel().writeAndFlush(userResp);
            return;
        }


        int type = request.getType().getNumber();
        switch (type){
            case EQUIP:
                equipService.getEquip(request, ctx.channel());
                break;
            case PUT:
                equipService.putEquip(request, ctx.channel());
                break;
            case TAKEOFF:
                equipService.takeOffEquip(request, ctx.channel());
                break;
            case FIX:
                equipService.fixEquip(request, ctx.channel());
                break;
        }
    }
}
