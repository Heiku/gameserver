package com.ljh.gamedemo.server.handler;

import com.ljh.gamedemo.proto.protoc.MsgGuildProto;
import com.ljh.gamedemo.service.GuildService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.ljh.gamedemo.server.request.RequestGuildType.*;

/**
 * 公会请求处理器
 *
 * @Author: Heiku
 * @Date: 2019/8/26
 */
public class GuildHandler extends SimpleChannelInboundHandler<MsgGuildProto.RequestGuild> {

    private static GuildService guildService;

    static {
        guildService = SpringUtil.getBean(GuildService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgGuildProto.RequestGuild req) throws Exception {
        int type = req.getTypeValue();
        Channel channel = ctx.channel();
        switch (type){
            case GUILD:
                guildService.guild(req, channel);
                break;
            case GUILD_ALL:
                guildService.guildAll(req, channel);
                break;
            case ESTABLISH:
                guildService.establish(req, channel);
                break;
            case APPLY_GUILD:
                guildService.applyGuild(req, channel);
                break;
            case APPLY_ALL:
                guildService.applyAll(req, channel);
                break;
            case APPROVAL:
                guildService.approval(req, channel);
                break;
            case MODIFY_ANN:
                guildService.modifyAnn(req, channel);
                break;
            case GIVE:
                guildService.give(req, channel);
                break;
            case DONATE:
                guildService.donate(req, channel);
                break;
            case KICK_OUT:
                guildService.kickOut(req, channel);
                break;
            case EXIT_GUILD:
                guildService.exitGuild(req, channel);
                break;
        }
    }
}
