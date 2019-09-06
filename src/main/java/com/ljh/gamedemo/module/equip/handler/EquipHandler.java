package com.ljh.gamedemo.module.equip.handler;

import com.ljh.gamedemo.proto.protoc.MsgEquipProto;
import com.ljh.gamedemo.module.equip.service.EquipService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import static com.ljh.gamedemo.server.request.RequestEquipType.*;

/**
 * @Author: Heiku
 * @Date: 2019/7/24
 */

@Slf4j
public class EquipHandler extends SimpleChannelInboundHandler<MsgEquipProto.RequestEquip> {

    private static EquipService equipService;

    private MsgEquipProto.ResponseEquip response;

    static {
        equipService = SpringUtil.getBean(EquipService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgEquipProto.RequestEquip request) throws Exception {
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
