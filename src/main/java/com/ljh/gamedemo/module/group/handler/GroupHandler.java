package com.ljh.gamedemo.module.group.handler;

import com.ljh.gamedemo.proto.protoc.MsgGroupProto;
import com.ljh.gamedemo.module.group.service.GroupService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.ljh.gamedemo.server.request.RequestGroupType.*;

/**
 * @Author: Heiku
 * @Date: 2019/8/14
 */
public class GroupHandler extends SimpleChannelInboundHandler<MsgGroupProto.RequestGroup> {

    private static GroupService groupService;

    static {
        groupService = SpringUtil.getBean(GroupService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgGroupProto.RequestGroup req) throws Exception {
        Channel channel = ctx.channel();
        int type = req.getTypeValue();

        switch (type){
            case STATE_GROUP:
                groupService.getState(req, channel);
                break;
            case INVITE:
                groupService.invite(req, channel);
                break;
            case JOIN:
                groupService.join(req, channel);
                break;
            case EXIT_GROUP:
                groupService.exit(req, channel);
                break;
        }
    }
}
