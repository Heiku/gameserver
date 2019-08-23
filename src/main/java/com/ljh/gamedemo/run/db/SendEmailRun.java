package com.ljh.gamedemo.run.db;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.EmailType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.dao.EmailGoodsDao;
import com.ljh.gamedemo.dao.RoleEmailDao;
import com.ljh.gamedemo.entity.Email;
import com.ljh.gamedemo.entity.EmailGoods;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.local.channel.ChannelCache;
import com.ljh.gamedemo.proto.protoc.EmailProto;
import com.ljh.gamedemo.proto.protoc.GoodsProto;
import com.ljh.gamedemo.proto.protoc.MsgEmailProto;
import com.ljh.gamedemo.service.ProtoService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 发送邮件的task
 * 1.邮件数据持久
 * 2.消息发送
 *
 * @Author: Heiku
 * @Date: 2019/8/8
 */

@Slf4j
public class SendEmailRun implements Runnable {

    /**
     * 邮件信息
     */
    private Email email;

    /**
     * 附件信息
     */
    private List<EmailGoods> egList;

    /**
     * 协议返回
     */
    private MsgEmailProto.ResponseEmail response;

    /**
     * 数据库 role_email
     */
    private RoleEmailDao emailDao = SpringUtil.getBean(RoleEmailDao.class);

    /**
     * 数据库 email_goods
     */
    private EmailGoodsDao emailGoodsDao = SpringUtil.getBean(EmailGoodsDao.class);

    /**
     * 协议转换服务
     */
    private ProtoService protoService = ProtoService.getInstance();

    public SendEmailRun(Email email, List<EmailGoods> egList, EmailType type){
        this.email = email;
        this.egList = egList;
    }

    @Override
    public void run() {
        log.info("发送邮件发送记录！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！");
        // 获取基本信息
        long roleId = email.getToRoleId();
        Role role = LocalUserMap.getIdRoleMap().get(roleId);

        // 插入数据库
        insertEmailDb(email, egList);

        // 构造返回消息
        response = generateResp(role, email, egList);

        // 发送 email resp
        Channel channel = ChannelCache.getUserIdChannelMap().get(role.getUserId());
        channel.writeAndFlush(response);
    }




    /**
     * 插入邮件记录，邮件物品记录
     *
     * @param email
     * @param egList
     */
    private void insertEmailDb(Email email, List<EmailGoods> egList){
        // 插入邮件记录
        int n = emailDao.insertEmail(email);
        log.info("插入邮件记录成功，插入的条数为：" + n + " 插入的id为；" + email.getId());

        // 设置物品的邮件 id
        egList.forEach(e -> {
            e.setEid(email.getId());

            // 再插入到数据库中
            int i = emailGoodsDao.insertEmailGoods(e);
            log.info("插入邮件物品记录成功，插入的条数为：" + i + " 插入的id为：" + e.getId());
        });
    }


    /**
     * 构造邮件消息Resp
     *
     * @param email
     * @param egList
     * @return
     */
    private MsgEmailProto.ResponseEmail generateResp(Role r, Email email, List<EmailGoods> egList) {
        // 邮件物品协议
        List<GoodsProto.Goods> goodsList = protoService.transToGoodsList(egList);

        // 邮件协议
        List<EmailProto.Email> eList = new ArrayList<>();
        EmailProto.Email eProto = protoService.transToEmail(r, email, goodsList);
        eList.add(eProto);

        response = MsgEmailProto.ResponseEmail.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgEmailProto.RequestType.READ)
                .setContent(ContentType.EMAIL_NEW_MSG)
                .addAllEmail(eList)
                .build();

        return response;
    }
}
