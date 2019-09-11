package com.ljh.gamedemo.module.items.handler;

import com.ljh.gamedemo.module.user.service.UserService;
import com.ljh.gamedemo.proto.protoc.MsgItemProto;
import com.ljh.gamedemo.module.items.service.ItemService;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static com.ljh.gamedemo.server.request.RequestItemType.*;

/**
 * 物品请求处理器
 *
 * @Author: Heiku
 * @Date: 2019/7/16
 */

@Slf4j
public class ItemsHandler extends SimpleChannelInboundHandler<MsgItemProto.RequestItem> {

    /**
     * 玩家服务
     */
    private static UserService userService;

    /**
     * 物品服务
     */
    private static ItemService itemService;


    static {
        userService = SpringUtil.getBean(UserService.class);
        itemService = SpringUtil.getBean(ItemService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgItemProto.RequestItem req) throws Exception {
        // 用户判断
        MsgUserInfoProto.ResponseUserInfo userResp = userService.userStateInterceptor(req.getUserId());
        if (!Objects.isNull(userResp)){
            ctx.channel().writeAndFlush(userResp);
            return;
        }

        int type = req.getType().getNumber();
        switch (type){
            case ALL:
                itemService.getAll(req, ctx.channel());
                break;
            case USE:
                itemService.useItem(req, ctx.channel());
                break;
        }
    }
}
