package com.ljh.gamedemo.server.handler;

import com.ljh.gamedemo.proto.protoc.MsgGuildProto;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.service.GuildService;
import com.ljh.gamedemo.service.UserService;
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

    /**
     * 公会服务
     */
    private static GuildService guildService;

    /**
     * 用户服务
     */
    private static UserService userService;

    static {
        userService = SpringUtil.getBean(UserService.class);
        guildService = SpringUtil.getBean(GuildService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgGuildProto.RequestGuild req) throws Exception {
        int type = req.getTypeValue();
        Channel channel = ctx.channel();

        // 用户认证
        MsgUserInfoProto.ResponseUserInfo userResp;
        if ((userResp = userService.userStateInterceptor(req.getUserId())) != null){
            channel.writeAndFlush(userResp);
            return;
        }

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
            case TAKE_OUT:
                guildService.takeOut(req, channel);
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
