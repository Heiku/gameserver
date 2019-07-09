package com.ljh.gamedemo.server.handler;

import com.ljh.gamedemo.proto.protoc.MsgSiteInfoProto;
import com.ljh.gamedemo.service.SaveDataService;
import com.ljh.gamedemo.service.SiteService;
import com.ljh.gamedemo.util.SpringUtil;
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
        switch (type){
            case SITE:
                responseSiteInfo = siteService.getNowSiteCName(requestSiteInfo);
                break;
            case MOVE:
                responseSiteInfo = siteService.move(requestSiteInfo);
                break;
        }

        ctx.writeAndFlush(responseSiteInfo);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }

}
