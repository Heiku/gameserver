package com.ljh.gamedemo.module.entity.handler;

import com.ljh.gamedemo.module.entity.service.EntityService;
import com.ljh.gamedemo.module.user.service.UserService;
import com.ljh.gamedemo.proto.protoc.MsgEntityInfoProto;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Objects;

import static com.ljh.gamedemo.server.request.RequestEntityInfoType.AOI;


/**
 * 实体请求处理器
 */
public class EntityInfoHandler extends SimpleChannelInboundHandler<MsgEntityInfoProto.RequestEntityInfo> {

    /**
     * 玩家服务
     */
    private static UserService userService;

    /**
     * 实体服务
     */
    private static EntityService entityService;

    static {
        userService = SpringUtil.getBean(UserService.class);
        entityService = SpringUtil.getBean(EntityService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgEntityInfoProto.RequestEntityInfo req) throws Exception {
        // 用户认证判断
        MsgUserInfoProto.ResponseUserInfo userResp = userService.userStateInterceptor(req.getUserId());
        if (!Objects.isNull(userResp)){
            ctx.channel().writeAndFlush(userResp);
            return;
        }

        // 获取请求类型
        int requestType = req.getType().getNumber();
        switch (requestType){
            case AOI:
                entityService.getAoi(req, ctx.channel());
                break;
        }
    }
}
