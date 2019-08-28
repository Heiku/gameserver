package com.ljh.gamedemo.server.handler;

import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.proto.protoc.TalkEntityProto;
import com.ljh.gamedemo.service.TalkEntityService;
import com.ljh.gamedemo.service.UserService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.ljh.gamedemo.server.request.RequestTalkEntityType.*;

/**
 * 聊天请求处理器
 */
public class TalkEntityHandler extends SimpleChannelInboundHandler<TalkEntityProto.RequestTalkEntity> {

    /**
     * 用户服务
     */
    private static UserService userService;

    /**
     * 交流服务
     */
    private static TalkEntityService talkEntityService;

    /**
     * 玩家协议返回
     */
    private MsgUserInfoProto.ResponseUserInfo userResp;

    static {
        userService = SpringUtil.getBean(UserService.class);
        talkEntityService = SpringUtil.getBean(TalkEntityService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TalkEntityProto.RequestTalkEntity requestTalkEntity) throws Exception {
        Channel channel = ctx.channel();

        userResp = userService.userStateInterceptor(requestTalkEntity.getUserId());
        if (userResp != null){
            channel.writeAndFlush(userResp);
            return;
        }

        int type = requestTalkEntity.getType().getNumber();
        switch (type) {
            case TALK:
                talkEntityService.talkNpc(requestTalkEntity, ctx.channel());
        }
    }
}
