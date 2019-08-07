package com.ljh.gamedemo.service;

import com.google.common.base.Strings;
import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.dao.ChatRecordDao;
import com.ljh.gamedemo.entity.ChatRecord;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.entity.RoleState;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.local.cache.RoleStateCache;
import com.ljh.gamedemo.local.channel.ChannelCache;
import com.ljh.gamedemo.proto.protoc.MsgChatProto;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author: Heiku
 * @Date: 2019/8/6
 */
@Service
@Slf4j
public class ChatService  {

    @Autowired
    private ChatRecordDao recordDao;

    private ProtoService protoService = ProtoService.getInstance();

    private MsgChatProto.ResponseChat response;

    /**
     * 玩家聊天的具体操作
     *
     * @param request
     * @param channel
     */
    public void chatRole(MsgChatProto.RequestChat request, Channel channel) {

        // 参数校验
        response = chanInterceptor(request, true);
        if (response != null){
            channel.writeAndFlush(response);
            return;
        }
        doChatToRole(request, channel);
    }


    /**
     * 群聊的具体操作
     *
     * @param request
     * @param channel
     */
    public void chatAllGroup(MsgChatProto.RequestChat request, Channel channel) {
        // 参数校验
        response = chanInterceptor(request, false);
        if (response != null){
            channel.writeAndFlush(response);
            return;
        }
        Role fromRole = LocalUserMap.userRoleMap.get(request.getUserId());
        String msg = request.getContent();

        // 发送消息
        doChatToGroup(fromRole, msg,  channel);
    }


    /**
     * 全服消息发送
     *
     * @param fromRole
     * @param channel
     */
    private void doChatToGroup(Role fromRole, String msg, Channel channel) {
        // 获取全服channelGroup
        ChannelGroup group = ChannelCache.getAllRoleGroup();

        response = combineMsg(fromRole, msg, false);
        group.writeAndFlush(response);

        // 数据持久化
        saveRecordData(fromRole.getRoleId(), 0, msg);

        sendResponse(channel, true);
    }


    /**
     * 发送玩家消息
     *
     * @param request
     * @param channel
     */
    private void doChatToRole(MsgChatProto.RequestChat request, Channel channel) {
        long userId = request.getUserId();
        long roleId = request.getRoleId();
        String content = request.getContent();

        // 获取发送人信息
        Role fromRole = LocalUserMap.userRoleMap.get(userId);

        // 获取接收人的channel
        Role toRole = LocalUserMap.getIdRoleMap().get(roleId);
        Channel toChannel = ChannelCache.getUserIdChannelMap().get(toRole.getUserId());

        if (toChannel != null){

            // 向接收人发送消息
            sendMsg(toChannel, fromRole, content);

            // 返回写信人在线发送成功结果
            sendResponse(channel, true);
        }else {
            // 返回写信人在线发送失败结果
            sendResponse(channel, false);
        }

        // 聊天记录持久化
        saveRecordData(fromRole.getRoleId(), roleId, content);
    }




    /**
     * 持久化玩家的聊天记录
     *
     * @param fromRoleId
     * @param toRoleId
     * @param content
     */
    private void saveRecordData(Long fromRoleId, long toRoleId, String content) {
        ChatRecord record = new ChatRecord();
        record.setFromRole(fromRoleId);
        record.setToRole(toRoleId);
        record.setContent(content);
        record.setSendTime(new Date());

        int n = recordDao.insertChatRecord(record);
        log.info("新增玩家的私聊记录，插入的记录条数为：" + n);
    }

    /**
     * 组合成消息进行返回
     *
     * @param fromRole
     * @param content
     * @param alone
     * @return
     */
    private MsgChatProto.ResponseChat combineMsg(Role fromRole, String content, boolean alone) {
        String title;
        if (alone){
            title = ContentType.CHAT_NEW_MSG_FROM_ROLE;
        }else {
            title = ContentType.CHAT_NEW_MSG_FROM_WORLD;
        }

        return MsgChatProto.ResponseChat.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(title)
                .setType(MsgChatProto.RequestType.MSG)
                .setMsg(content)
                .setRole(protoService.transToRole(fromRole))
                .build();
    }



    /**
     * 聊天参数校验拦截器
     *
     * @param request
     * @return
     */
    private MsgChatProto.ResponseChat chanInterceptor(MsgChatProto.RequestChat request, boolean toRole){
        long userId = request.getUserId();
        if (userId <= 0){
            return MsgChatProto.ResponseChat.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.USER_EMPTY_LOGIN_PARAM)
                    .build();
        }

        String content = request.getContent();
        if (Strings.isNullOrEmpty(content)){
            return MsgChatProto.ResponseChat.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.CHAT_NOT_CONTENT)
                    .build();
        }

        if (toRole){
            long roleId = request.getRoleId();
            if (roleId <= 0){
                return MsgChatProto.ResponseChat.newBuilder()
                        .setResult(ResultCode.FAILED)
                        .setContent(ContentType.ROLE_EMPTY)
                        .build();
            }
        }

        return  null;
    }

    /**
     * 返回发送的消息
     *
     * @param channel
     */
    private void sendResponse(Channel channel, boolean line) {
        String content;
        if (line){
            content = ContentType.CHAT_SEND_SUCCESS;
        }else {
            content = ContentType.CHAT_SEND_UN_LINE_SUCCESS;
        }

        response = MsgChatProto.ResponseChat.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(content)
                .build();

        channel.writeAndFlush(response);
    }

    /**
     * 返回失败消息
     *
     * @param channel
     * @param content
     */
    private void responseFailed(Channel channel, String content) {
        response = MsgChatProto.ResponseChat.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(content)
                .build();
        channel.writeAndFlush(response);
    }


    /**
     * 向玩家发送离线消息
     *
     * @param role
     */
    public void receiveOfflineMsg(Role role) {

        // 消息的基本信息
        long toRoleId = role.getRoleId();
        Channel channel = ChannelCache.getUserIdChannelMap().get(role.getUserId());

        // 获取玩家在线信息
        RoleState state = RoleStateCache.getCache().getIfPresent(toRoleId);
        if (state == null){
            state = recordDao.selectUserOffline(toRoleId);
        }

        // 查询历史消息
        List<ChatRecord> recordList = recordDao.selectOfflineMsg(toRoleId, state.getOfflineTime());
        if (recordList == null || recordList.isEmpty()){
            return;
        }

        // 发送离线消息
       recordList.forEach(r -> {
           Role fromRole = LocalUserMap.idRoleMap.get(r.getFromRole());
           sendMsg(channel, fromRole, r.getContent());
       });
    }


    /**
     * 向接收方发送消息
     *
     * @param channel
     * @param fromRole
     * @param msg
     */
    private void sendMsg(Channel channel, Role fromRole, String msg){
        response = combineMsg(fromRole, msg, true);
            channel.writeAndFlush(response);
    }
}
