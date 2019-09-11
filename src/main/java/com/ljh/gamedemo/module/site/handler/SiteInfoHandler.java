package com.ljh.gamedemo.module.site.handler;

import com.ljh.gamedemo.module.user.service.UserService;
import com.ljh.gamedemo.proto.protoc.MsgSiteInfoProto;
import com.ljh.gamedemo.module.base.service.SaveDataService;
import com.ljh.gamedemo.module.site.service.SiteService;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

import static com.ljh.gamedemo.server.request.RequestSiteType.MOVE;
import static com.ljh.gamedemo.server.request.RequestSiteType.SITE;


/**
 * 场景请求处理器
 */
public class SiteInfoHandler extends SimpleChannelInboundHandler<MsgSiteInfoProto.RequestSiteInfo> {

    /**
     * 玩家服务
     */
    private static UserService userService;

    /**
     * 场景服务
     */
    private static SiteService siteService;

    static {
        userService = SpringUtil.getBean(UserService.class);
        siteService = SpringUtil.getBean(SiteService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgSiteInfoProto.RequestSiteInfo req) throws Exception {
        // 用户判断
        MsgUserInfoProto.ResponseUserInfo userResp = userService.userStateInterceptor(req.getUserId());
        if (!Objects.isNull(userResp)){
            ctx.channel().writeAndFlush(userResp);
            return;
        }

        int type = req.getType().getNumber();
        Channel channel = ctx.channel();
        switch (type){
            case SITE:
                siteService.getNowSiteCName(req, channel);
                break;
            case MOVE:
                siteService.move(req, channel);
                break;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }

}
