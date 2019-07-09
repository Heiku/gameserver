package com.ljh.gamedemo.server.handler;

import com.ljh.gamedemo.proto.protoc.TalkEntityProto;
import com.ljh.gamedemo.service.TalkEntityService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.ljh.gamedemo.server.request.RequestTalkEntityType.*;

public class TalkEntityHandler extends SimpleChannelInboundHandler<TalkEntityProto.RequestTalkEntity> {

    private static TalkEntityService talkEntityService;

    private TalkEntityProto.ResponseTalkEntity responseTalkEntity;

    static {
        talkEntityService = SpringUtil.getBean(TalkEntityService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TalkEntityProto.RequestTalkEntity requestTalkEntity) throws Exception {
        int type = requestTalkEntity.getType().getNumber();
        switch (type){
            case TALK:
                responseTalkEntity = talkEntityService.talkNpc(requestTalkEntity);

        }

        ctx.writeAndFlush(responseTalkEntity);
    }
}
