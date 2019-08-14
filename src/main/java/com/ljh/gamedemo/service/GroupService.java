package com.ljh.gamedemo.service;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.entity.Group;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.local.cache.GroupCache;
import com.ljh.gamedemo.local.channel.ChannelCache;
import com.ljh.gamedemo.proto.protoc.MsgGroupProto;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.util.CommonUtil;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 组队的具体操作
 *
 * @Author: Heiku
 * @Date: 2019/8/14
 */

@Service
public class GroupService {

    @Autowired
    private UserService userService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private ProtoService protoService;


    private MsgUserInfoProto.ResponseUserInfo userResp;

    private MsgGroupProto.ResponseGroup groupResp;


    /**
     * 获取当前玩家的组队信息
     *
     * @param req       请求
     * @param channel   channel
     */
    public void getState(MsgGroupProto.RequestGroup req, Channel channel) {
        // 玩家认证
        userResp = userService.userStateInterceptor(req.getUserId());
        if (userResp != null){
            channel.writeAndFlush(userResp);
            return;
        }

        // 判断是否有队伍
        groupResp = groupHasInterceptor(req.getUserId());
        if (groupResp != null){
            channel.writeAndFlush(groupResp);
            return;
        }
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());
        Group group = GroupCache.getRoleGroupMap().get(role.getRoleId());

        // 返回组队消息
        groupResp = combineResp(group, ContentType.GROUP_STATE_SUCCESS);
        channel.writeAndFlush(groupResp);
    }



    /**
     * 邀请玩家进行组队
     *
     * 1.如果发起人当前没有队伍，则新建队伍，自己为队长
     * 2.发送邀请人邀请信息
     * 3.响应操作回复
     *
     * @param req       请求
     * @param channel   channel
     */
    public void invite(MsgGroupProto.RequestGroup req, Channel channel) {
        // 用户认证
        userResp = userService.userStateInterceptor(req.getUserId());
        if (userResp != null){
            channel.writeAndFlush(userResp);
            return;
        }
        // 获取用户信息
        Role own = LocalUserMap.getUserRoleMap().get(req.getUserId());
        Role invite = LocalUserMap.getIdRoleMap().get(req.getRoleId());

        // 只有相同地点的玩家才可以组队
        if (!siteService.inSameSite(own, invite)){
            responseFailed(channel, ContentType.GROUP_NOT_IN_SAME_SITE);
            return;
        }

        Group group = GroupCache.getRoleGroupMap().get(own.getRoleId());
        if (group == null){
            group = newGroup(own);
        }

        // 发送组队邀请
        sendGroupInvite(own, invite, group);

        responseSuccess(channel, ContentType.GROUP_INVITE_SEND_SUCCESS);
    }


    /**
     * 加入队伍指定 groupId 的队伍
     *
     * 1.判断当前玩家是否在其他队伍中
     * 2.关联队伍信息
     * 3.消息返回
     *
     * @param req       请求
     * @param channel   channel
     */
    public void join(MsgGroupProto.RequestGroup req, Channel channel) {
        // 用户认证
        userResp = userService.userStateInterceptor(req.getUserId());
        if (userResp != null){
            channel.writeAndFlush(userResp);
            return;
        }

        Role own = LocalUserMap.getUserRoleMap().get(req.getUserId());

        // 判断当前玩家是否已经在当前队伍中
        groupResp = groupInInterceptor(own.getRoleId(), req.getGroupId());
        if (groupResp != null){
            channel.writeAndFlush(groupResp);
            return;
        }

        // 判断当前玩家是否在其他队伍中
        if (GroupCache.getRoleGroupMap().get(own.getRoleId()) != null){
            responseFailed(channel, ContentType.GROUP_ROLE_HAS_IN_OTHER_GROUP);
            return;
        }

        // 正式加入队伍中
        long groupId = req.getGroupId();
        Group group = GroupCache.getGroupCache().getIfPresent(groupId);
        if (group == null){
            responseFailed(channel, ContentType.GROUP_WRONG_GROUP_ID);
            return;
        }
        addGroup(own, group);

        // 消息返回
        responseSuccess(channel, ContentType.GROUP_JOIN_SUCCESS);
    }


    /**
     * 退出队伍
     *
     * @param req       请求
     * @param channel   channel
     */
    public void exit(MsgGroupProto.RequestGroup req, Channel channel) {
        userResp = userService.userStateInterceptor(req.getUserId());
        if (userResp != null){
            channel.writeAndFlush(userResp);
            return;
        }

        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());
        Group group = GroupCache.getRoleGroupMap().get(role.getRoleId());
        if (group == null){
            responseFailed(channel, ContentType.GROUP_NOT_IN_ANY_GROUP);
            return;
        }

        // 具体退出队伍操作
        removeGroup(role);

        responseSuccess(channel, ContentType.GROUP_EXIT_SUCCESS);
    }



    /**
     * 构建新的队伍信息
     *
     * @param role  玩家
     */
    private Group newGroup(Role role) {
        Group group = new Group();
        group.setId(CommonUtil.generateLong());
        group.setLeader(role.getRoleId());
        group.getMembers().add(role.getRoleId());

        // 队伍存放
        GroupCache.getGroupCache().put(group.getId(), group);

        // 玩家队伍存放
        GroupCache.getRoleGroupMap().put(role.getRoleId(), group);

        return group;
    }


    /**
     * 将玩家加入队伍
     *
     * @param own    玩家
     * @param group  队伍
     */
    private void addGroup(Role own, Group group) {
        group.getMembers().add(own.getRoleId());

        // 存档
        GroupCache.getGroupCache().put(group.getId(), group);

        GroupCache.getRoleGroupMap().put(own.getRoleId(), group);
    }


    /**
     * 玩家退出队伍
     *
     * @param role  玩家
     */
    private void removeGroup(Role role) {
        Group group = GroupCache.getRoleGroupMap().get(role.getRoleId());

        if (group != null) {
            group.getMembers().remove(role.getRoleId());

            // 当前队伍已经没有成员
            if (group.getMembers().isEmpty()){
                GroupCache.getGroupCache().invalidate(group.getId());
                GroupCache.getRoleGroupMap().remove(role.getRoleId());
                group = null;
                return;
            }
            else if (group.getLeader().longValue() == role.getRoleId()){
                group.setLeader(group.getMembers().get(0));
            }
            // 更新队伍记录
            GroupCache.getRoleGroupMap().remove(role.getRoleId());

            GroupCache.getGroupCache().put(group.getId(), group);
        }    }

    /**
     * 发送组队邀请
     *
     * @param own       发送方
     * @param invite    接受方
     * @param group     队伍信息
     */
    private void sendGroupInvite(Role own, Role invite, Group group) {
        // 获取接受方的channel
        Channel inviteChannel = ChannelCache.getUserIdChannelMap().get(invite.getUserId());

        // 组队文本
        String content = String.format(ContentType.GROUP_INVITE_MSG, own.getName(), group.getId());

        responseSuccess(inviteChannel, content);
    }


    /**
     * 判断玩家是否有队伍信息
     *
     * @param userId
     * @return
     */
    private MsgGroupProto.ResponseGroup groupHasInterceptor(long userId){
        Role role = LocalUserMap.getUserRoleMap().get(userId);

        Group group = GroupCache.getRoleGroupMap().get(role.getRoleId());
        if (group == null){
            groupResp = MsgGroupProto.ResponseGroup.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.GROUP_NOT_IN_GROUP)
                    .build();
            return groupResp;
        }
        return null;
    }


    /**
     * 判断玩家是否已经在要加入的队伍中
     *
     * @param roleId    玩家id
     * @param groupId   队伍id
     */
    private MsgGroupProto.ResponseGroup groupInInterceptor(Long roleId, long groupId) {
        Group group = GroupCache.getGroupCache().getIfPresent(groupId);
        if (group != null){
            if (group.getMembers().contains(roleId)){
                groupResp = MsgGroupProto.ResponseGroup.newBuilder()
                        .setResult(ResultCode.FAILED)
                        .setContent(ContentType.GROUP_ROLE_HAS_IN_GROUP)
                        .build();
                return groupResp;
            }
        }
        return null;
    }

    /**
     * 组合返回消息
     *
     * @param group     队伍信息
     * @param content   消息
     * @return
     */
    private MsgGroupProto.ResponseGroup combineResp(Group group, String content) {
        groupResp = MsgGroupProto.ResponseGroup.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgGroupProto.RequestType.STATE_GROUP)
                .setContent(content)
                .setGroup(protoService.transToGroup(group))
                .build();
        return groupResp;
    }


    /**
     * 返回失败消息
     *
     * @param channel   channel
     * @param content   消息
     */
    private void responseFailed(Channel channel, String content){
        groupResp = MsgGroupProto.ResponseGroup.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(content)
                .build();
        channel.writeAndFlush(groupResp);
    }

    /**
     * 返回成功消息
     *
     * @param channel   channel
     * @param content   消息
     */
    private void responseSuccess(Channel channel, String content) {
        groupResp = MsgGroupProto.ResponseGroup.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(content)
                .setType(MsgGroupProto.RequestType.INVITE_GROUP)
                .build();
        channel.writeAndFlush(groupResp);
    }

}
