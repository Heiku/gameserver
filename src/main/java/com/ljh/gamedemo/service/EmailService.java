package com.ljh.gamedemo.service;

import com.ljh.gamedemo.common.EmailType;
import com.ljh.gamedemo.dao.EmailGoodsDao;
import com.ljh.gamedemo.dao.RoleEmailDao;
import com.ljh.gamedemo.entity.Email;
import com.ljh.gamedemo.entity.EmailGoods;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.proto.protoc.MsgEmailProto;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.run.db.SendEmailRun;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author: Heiku
 * @Date: 2019/8/8
 */

@Service
@Slf4j
public class EmailService {


    @Autowired
    private RoleEmailDao emailDao;

    @Autowired
    private EmailGoodsDao emailGoodsDao;


    @Autowired
    private UserService userService;

    @Autowired
    private ProtoService protoService;

    private MsgUserInfoProto.ResponseUserInfo userResp;

    private MsgEmailProto.ResponseEmail emailResp;

    /**
     * 获取所有的邮件信息
     *
     * @param request
     * @param channel
     */
    public void getAllEmail(MsgEmailProto.RequestEmail request, Channel channel){

        // 玩家状态判断
        userResp = userService.userStateInterceptor(request.getUserId());
        if (userResp != null){
            channel.writeAndFlush(userResp);
        }

        long userId = request.getUserId();
        Role role = LocalUserMap.userRoleMap.get(userId);

        // 获取该玩家的所有邮件信息
        List<Email> emailList = emailDao.selectUnReceiveEmail(role.getRoleId());

        // response msg
        emailResp = combineEmailList(emailList);
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

        // 插入邮件记录
        int n = emailDao.insertEmail(email);
        log.info("插入邮件记录成功，插入的条数为：" + n + " 插入的id为；" + email.getId());

        // 设置物品的邮件 id
        egList.forEach(e -> {
            e.setEid(email.getId());

            // 再插入到数据库中
            emailGoodsDao.insertEmailGoods(e);
        });

        SendEmailRun run = new SendEmailRun();
    }


    public void receiveEmail(MsgEmailProto.RequestEmail request, Channel channel) {
    }


    /**
     * 组合邮件消息
     *
     * @param type
     * @param role
     * @return  
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
     * 组合邮件列表消息返回
     *
     * @param emailList
     * @return
     */
    private MsgEmailProto.ResponseEmail combineEmailList(List<Email> emailList) {

        /*// 获取 Email proto List
        List<EmailProto.Email> resList = protoService.transToEmailList(emailList);

        emailResp = MsgEmailProto.ResponseEmail.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.FIND_SUCCESS)
                .setType(MsgEmailProto.RequestType.EMAIL)
                .addAllEmail(resList)
                .build();
        return emailResp;*/

        return null;
    }
}
