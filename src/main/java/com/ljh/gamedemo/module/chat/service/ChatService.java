package com.ljh.gamedemo.module.chat.service;

import com.ljh.gamedemo.common.CommonDBType;
import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.module.base.cache.ChannelCache;
import com.ljh.gamedemo.module.base.service.ProtoService;
import com.ljh.gamedemo.module.chat.asyn.ChatRecordSaveManager;
import com.ljh.gamedemo.module.chat.asyn.run.ChatRecordSaveRun;
import com.ljh.gamedemo.module.chat.bean.ChatRecord;
import com.ljh.gamedemo.module.chat.dao.ChatRecordDao;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.role.bean.RoleState;
import com.ljh.gamedemo.module.role.cache.RoleStateCache;
import com.ljh.gamedemo.module.role.dao.RoleStateDao;
import com.ljh.gamedemo.module.user.local.LocalUserMap;
import com.ljh.gamedemo.proto.protoc.MsgChatProto;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * 聊天具体操作
 *
 * @Author: Heiku
 * @Date: 2019/8/6
 */
@Service
@Slf4j
public class ChatService  {

    /**
     * ChatRecordDao
     */
    @Autowired
    private ChatRecordDao recordDao;

    /**
     * RoleStateDao
     */
    @Autowired
    private RoleStateDao roleStateDao;

    /**
     * 协议服务
     */
    @Autowired
    private ProtoService protoService;

    /**
     * 聊天协议返回
     */
    private MsgChatProto.ResponseChat response;



    /**
     * 玩家聊天的具体操作
     *
     * @param request       请求
     * @param channel       channel
     */
    public void chatRole(MsgChatProto.RequestChat request, Channel channel) {
        // 具体的对话操作
        doChatToRole(request, channel);
    }


    /**
     * 群聊的具体操作
     *
     * @param request       请求
     * @param channel       channel
     */
    public void chatAllGroup(MsgChatProto.RequestChat request, Channel channel) {
        Role fromRole = LocalUserMap.userRoleMap.get(request.getUserId());
        String msg = request.getContent();

        // 发送消息
        doChatToGroup(fromRole, msg,  channel);
    }


    /**
     * 全服消息发送
     *
     * @param fromRole      发送者
     * @param channel       channel
     */
    private void doChatToGroup(Role fromRole, String msg, Channel channel) {
        // 获取全服channelGroup
        ChannelGroup group = ChannelCache.getAllRoleGroup();

        response = combineMsg(fromRole, msg, false);
        group.writeAndFlush(response);

        // 数据持久化
        saveRecordData(fromRole.getRoleId(), 0, msg);

        // 消息返回
        sendResponse(channel, true);
    }


    /**
     * 发送玩家消息
     *
     * @param request       请求
     * @param channel       channel
     */
    private void doChatToRole(MsgChatProto.RequestChat request, Channel channel) {
        // 参数信息
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
     * @param fromRoleId        发送者id
     * @param toRoleId          接收者id
     * @param content           消息文本
     */
    private void saveRecordData(Long fromRoleId, long toRoleId, String content) {
        // 生成聊天记录实体
        ChatRecord record = new ChatRecord();
        record.setFromRole(fromRoleId);
        record.setToRole(toRoleId);
        record.setContent(content);
        record.setSendTime(new Date());

        // 持久DB
        ChatRecordSaveManager.getExecutorService().submit(new ChatRecordSaveRun(record, CommonDBType.INSERT));
    }



    /**
     * 组合成消息进行返回
     *
     * @param fromRole      发送方
     * @param content       文本
     * @param alone         是否为单独发送
     * @return              聊天协议
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
     * 返回发送的消息
     *
     * @param channel       channel
     * @param line          是否为在线发送消息
     */
    private void sendResponse(Channel channel, boolean line) {
        // 消息信息
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
     * 向玩家发送离线消息
     *
     * @param role      玩家信息
     */
    public void receiveOfflineMsg(Role role) {

        // 消息的基本信息
        long toRoleId = role.getRoleId();
        Channel channel = ChannelCache.getUserIdChannelMap().get(role.getUserId());

        // 获取玩家在线信息
        RoleState state = RoleStateCache.getCache().getIfPresent(toRoleId);
        if (state == null){
            state = roleStateDao.selectUserOffline(toRoleId);
        }

        // 查询历史消息
        List<ChatRecord> recordList = recordDao.selectOfflineMsg(toRoleId, state.getOfflineTime());
        if (CollectionUtils.isEmpty(recordList)){
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
     * @param channel       channel
     * @param fromRole      发送方
     * @param msg           消息
     */
    private void sendMsg(Channel channel, Role fromRole, String msg){
        response = combineMsg(fromRole, msg, true);
        channel.writeAndFlush(response);
    }
}
