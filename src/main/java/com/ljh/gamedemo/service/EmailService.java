package com.ljh.gamedemo.service;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.EmailType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.dao.EmailGoodsDao;
import com.ljh.gamedemo.dao.RoleEmailDao;
import com.ljh.gamedemo.entity.Email;
import com.ljh.gamedemo.entity.EmailGoods;
import com.ljh.gamedemo.entity.Goods;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.local.LocalGoodsMap;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.proto.protoc.EmailProto;
import com.ljh.gamedemo.proto.protoc.MsgEmailProto;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.run.db.SendEmailRun;
import com.ljh.gamedemo.run.manager.SendEmailManager;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Heiku
 * @Date: 2019/8/8
 *
 * 邮件操作
 */

@Service
@Slf4j
public class EmailService {

    /**
     * EmailDao
     */
    @Autowired
    private RoleEmailDao emailDao;

    /**
     * EmailGoodsDao
     */
    @Autowired
    private EmailGoodsDao emailGoodsDao;

    /**
     * 物品服务
     */
    @Autowired
    private GoodsService goodsService;

    /**
     * 协议服务
     */
    @Autowired
    private ProtoService protoService;

    /**
     * 邮件协议返回
     */
    private MsgEmailProto.ResponseEmail emailResp;

    /**
     * 获取所有的邮件信息
     *
     * @param request
     * @param channel
     */
    public void getAllEmail(MsgEmailProto.RequestEmail request, Channel channel){
        long userId = request.getUserId();
        Role role = LocalUserMap.userRoleMap.get(userId);

        // 获取该玩家的所有邮件信息
        List<Email> emailList = emailDao.selectAllEmail(role.getRoleId());

        // response msg
        emailResp = combineEmailList(emailList, role);
        channel.writeAndFlush(emailResp);
    }


    /**
     * 向玩家发送邮件提醒
     *
     *   生成邮件实体
     *   将实体加入到邮件发送的队列中
     *
     * @param role      玩家信息
     * @param egList    邮件物品
     * @param type      邮件类型
     */
    public void sendEmail(Role role, List<EmailGoods> egList , EmailType type){
        // 构造邮件信息
        Email email = combineEmail(role, type);

        // 构造发送邮件的任务，加入发送的队列中
        SendEmailRun emailTask = new SendEmailRun(email, egList, type);
        SendEmailManager.addQueue(emailTask);
    }


    /**
     * 接收邮件道具
     *
     * @param request   请求
     * @param channel   channel
     */
    public void receiveEmail(MsgEmailProto.RequestEmail request, Channel channel) {
        // 获取基本信息
        long eid = request.getEid();
        Role role = LocalUserMap.userRoleMap.get(request.getUserId());

        // 判断邮件是否已经被领取
        Email email = emailDao.selectEmailById(eid);
        if (email.getState() == 1){
            responseFailed(channel, ContentType.EMAIL_RECEIVE_FAILED);
            return;
        }

        // 查询邮件内的相关物品
        List<EmailGoods> emailGoods = emailGoodsDao.selectAllEmailGoods(eid);
        if (emailGoods == null || emailGoods.isEmpty()){
            // 邮件内无物品
            responseFailed(channel, ContentType.EMAIL_EMPTY_GOODS);
            return;
        }

        // 存在物品，将物品存入玩家的背包信息中
        emailGoods.forEach( eg -> {
            Goods goods = LocalGoodsMap.getIdGoodsMap().get(eg.getGid());

            // 直接发放到背包中
            goodsService.sendRoleGoods(role, goods, eg.getNum());
        });


        // 更新邮件表的领取状态
        email = emailDao.selectEmailById(eid);
        email.setState(1);
        email.setModifyTime(new Date());
        emailDao.updateEmail(email);

        responseSuccess(channel, ContentType.EMAIL_RECEIVE_SUCCESS);
    }


    /**
     * 组合邮件消息
     *
     * @param type      邮件类型
     * @param role      玩家信息
     * @return          返回邮件信息
     */
    private Email combineEmail(Role role, EmailType type){
        Email email = new Email();

        // 设置邮件文本
        email.setTheme(type.getTheme());
        email.setContent(type.getContent());

        // 统一邮件的发送方为系统
        email.setFromId(1);
        email.setToRoleId(role.getRoleId());

        // 未领取状态
        email.setState(0);
        email.setCreateTime(new Date());

        return email;
    }



    /**
     * 组合邮件列表消息返回  <email, List<EmailGoods>>
     *
     * @param emailList     邮件列表
     * @return
     */
    private MsgEmailProto.ResponseEmail combineEmailList(List<Email> emailList, Role role) {
        Map<Email, List<EmailGoods>> emailGoodsMap = new HashMap<>();

        if (emailList == null || emailList.isEmpty()){
            return null;
        }
        emailList.forEach( e -> {
            List<EmailGoods> emailGoods = emailGoodsDao.selectAllEmailGoods(e.getId());
            emailGoodsMap.put(e, emailGoods);
        });

        // 获取 Email proto List
        List<EmailProto.Email> resList = protoService.transToEmailList(emailGoodsMap, role);

        emailResp = MsgEmailProto.ResponseEmail.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.FIND_SUCCESS)
                .setType(MsgEmailProto.RequestType.EMAIL)
                .addAllEmail(resList)
                .build();
        return emailResp;
    }


    /**
     * 成功消息返回
     *
     * @param channel       channel
     * @param content       消息
     */
    private void responseSuccess(Channel channel, String content) {
        emailResp = MsgEmailProto.ResponseEmail.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgEmailProto.RequestType.RECEIVE)
                .setContent(content)
                .build();
        channel.writeAndFlush(emailResp);
    }



    /**
     * 失败消息返回
     *
     * @param channel       channel
     * @param content       消息
     */
    private void responseFailed(Channel channel, String content) {
        emailResp = MsgEmailProto.ResponseEmail.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(content)
                .build();

        channel.writeAndFlush(emailResp);
    }
}
