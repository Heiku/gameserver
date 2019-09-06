package com.ljh.gamedemo.module.site.handler;

import com.ljh.gamedemo.proto.protoc.MsgSiteInfoProto;
import com.ljh.gamedemo.module.base.service.SaveDataService;
import com.ljh.gamedemo.module.site.service.SiteService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;

import static com.ljh.gamedemo.server.request.RequestSiteType.MOVE;
import static com.ljh.gamedemo.server.request.RequestSiteType.SITE;


public class SiteInfoHandler extends SimpleChannelInboundHandler<MsgSiteInfoProto.RequestSiteInfo> {

    @Autowired
    private static SiteService siteService;

    @Autowired
    private static SaveDataService saveDataService;

    private MsgSiteInfoProto.ResponseSiteInfo responseSiteInfo;

    static {
        siteService = SpringUtil.getBean(SiteService.class);
        saveDataService = SpringUtil.getBean(SaveDataService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgSiteInfoProto.RequestSiteInfo requestSiteInfo) throws Exception {
        int type = requestSiteInfo.getType().getNumber();
        Channel channel = ctx.channel();
        switch (type){
            case SITE:
                siteService.getNowSiteCName(requestSiteInfo, channel);
                break;
            case MOVE:
                siteService.move(requestSiteInfo, channel);
                break;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }

}
