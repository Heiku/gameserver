package com.ljh.gamedemo.server.handler;

import com.ljh.gamedemo.proto.protoc.MsgItemProto;
import com.ljh.gamedemo.service.ItemService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import static com.ljh.gamedemo.server.request.RequestItemType.*;

/**
 * @Author: Heiku
 * @Date: 2019/7/16
 */

@Slf4j
public class ItemsHandler extends SimpleChannelInboundHandler<MsgItemProto.RequestItem> {

    private static ItemService itemService;

    private MsgItemProto.ResponseItem response;

    static {
        itemService = SpringUtil.getBean(ItemService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgItemProto.RequestItem requestItem) throws Exception {
        int type = requestItem.getType().getNumber();
        switch (type){
            case ALL:
                itemService.getAll(requestItem, ctx.channel());
                break;
            case USE:
                itemService.useItem(requestItem, ctx.channel());
                break;
        }

        if (response != null) {
            ctx.writeAndFlush(response);
        }
    }
}
