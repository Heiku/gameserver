package com.ljh.gamedemo.service;

import com.google.common.collect.Lists;
import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.dao.GuildDao;
import com.ljh.gamedemo.entity.Guild;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.local.cache.GuildCache;
import com.ljh.gamedemo.proto.protoc.MsgGuildProto;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公会具体操作
 *
 * @Author: Heiku
 * @Date: 2019/8/26
 */

@Service
public class GuildService {

    /**
     * 建立公会所需的最低荣誉值
     */
    public static final int ESTABLISH_HONOR = 200;

    /**
     * guildDao
     */
    @Autowired
    private GuildDao guildDao;

    /**
     * 用户服务
     */
    @Autowired
    private UserService userService;

    /**
     * 协议服务
     */
    @Autowired
    private ProtoService protoService;

    /**
     * 用户返回
     */
    private MsgUserInfoProto.ResponseUserInfo userResp;

    /**
     * 公会返回
     */
    private MsgGuildProto.ResponseGuild guildResp;



    /**
     * 获取当前玩家所在的公会信息
     *
     * @param req       请求
     * @param channel   channel
     */
    public void guild(MsgGuildProto.RequestGuild req, Channel channel) {
        // 用户认证
        userResp = userService.userStateInterceptor(req.getUserId());
        if (userResp != null) {
            channel.writeAndFlush(userResp);
            return;
        }
        // 获取当前的公会信息
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());
        Guild guild = getCurGuild(role.getRoleId());
        if (guild == null) {
            sendFailedMsg(channel, ContentType.GUILD_NOT_IN);
            return;
        }

        // 组成消息，准备返回
        guildResp = combineResp(Lists.newArrayList(guild), MsgGuildProto.RequestType.GUILD);
        channel.writeAndFlush(guildResp);
    }


    /**
     * 获取所有的公会信息
     *
     * @param req       请求
     * @param channel   channel
     */
    public void guildAll(MsgGuildProto.RequestGuild req, Channel channel) {
        // 用户认证
        userResp = userService.userStateInterceptor(req.getUserId());
        if (userResp != null) {
            channel.writeAndFlush(userResp);
            return;
        }

        // 获取所有的公会信息
        List<Guild> guilds = getAllGuild();

        // 消息返回
        guildResp = combineResp(guilds, MsgGuildProto.RequestType.GUILD_ALL);
        channel.writeAndFlush(guildResp);
    }


    /**
     * 建立公会 （建立公会的荣誉值不得低于 200）
     *
     *
     * @param req       请求
     * @param channel   channel
     */
    public void establish(MsgGuildProto.RequestGuild req, Channel channel) {
        // 用户认证
        userResp = userService.userStateInterceptor(req.getUserId());
        if (userResp != null) {
            channel.writeAndFlush(userResp);
            return;
        }

        // 判断当前是否已经有公会信息了
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());
        Guild guild = getCurGuild(role.getRoleId());
        if (guild != null){
            sendFailedMsg(channel, ContentType.GUILD_ESTABLISH_HAS_IN);
            return;
        }

        // 荣誉值判断
        if (role.getHonor() < ESTABLISH_HONOR){
            sendFailedMsg(channel, String.format(ContentType.GUILD_ESTABLISH_HONOR_NOT_ENOUGH, ESTABLISH_HONOR));
            return;
        }

        // 创建公会信息
        doEstablishGuild(role, req.getGuildName(), req.getBulletin());
    }


    /**
     * 创建公会信息
     *
     * @param role          玩家信息
     * @param guildName     公会名
     * @param bulletin      公会公告
     */
    private void doEstablishGuild(Role role, String guildName, String bulletin) {
        Guild guild = Guild.builder()
                .name(guildName)
                .bulletin(bulletin)
                .level(1)
                .num(1)
                .maxNum(20)
                .president(role.getRoleId())
                .build();

        // 插入数据库中
        // int n =

    }


    /**
     * 获取玩家所在的公会信息
     *
     * @param roleId    玩家信息
     * @return          公会信息
     */
    private Guild getCurGuild(long roleId){
        return GuildCache.getRoleIdGuildMap().get(roleId);
    }


    /**
     * 获取所有的工会信息
     *
     * @return  公会信息列表
     */
    private List<Guild> getAllGuild() {
        return Lists.newArrayList(GuildCache.getIdGuildMap().values());
    }


    private MsgGuildProto.ResponseGuild combineResp(List<Guild> guilds, MsgGuildProto.RequestType type) {
        guildResp = MsgGuildProto.ResponseGuild.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .addAllGuild(protoService.transToGuildList(guilds))
                .setType(type)
                .build();
        return guildResp;
    }


    /**
     * 发送失败消息
     *
     * @param channel       channel
     * @param msg           消息
     */
    private void sendFailedMsg(Channel channel, String msg) {
        guildResp = MsgGuildProto.ResponseGuild.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(msg)
                .build();
        channel.writeAndFlush(guildResp);
    }
}
