package com.ljh.gamedemo.module.face.service;

import com.google.protobuf.Message;
import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.EmailType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.module.face.dao.FaceTransDao;
import com.ljh.gamedemo.module.email.bean.EmailGoods;
import com.ljh.gamedemo.module.email.service.EmailService;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.face.bean.Transaction;
import com.ljh.gamedemo.module.face.tmp.FaceTransApply;
import com.ljh.gamedemo.module.user.local.LocalUserMap;
import com.ljh.gamedemo.module.face.cache.FaceTransCache;
import com.ljh.gamedemo.module.base.cache.ChannelCache;
import com.ljh.gamedemo.module.site.service.SiteService;
import com.ljh.gamedemo.proto.protoc.MsgFaceTransProto;
import com.ljh.gamedemo.proto.protoc.MsgRoleProto;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.module.role.service.RoleService;
import com.ljh.gamedemo.module.user.service.UserService;
import com.ljh.gamedemo.module.goods.service.GoodsService;
import com.ljh.gamedemo.module.base.service.ProtoService;
import com.ljh.gamedemo.util.CommonUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 面对面交易的具体操作
 *
 * @Author: Heiku
 * @Date: 2019/8/22
 */

@Service
@Slf4j
public class FaceTransService {

    /**
     * 发起面对面交易的最低等级
     */
    public static final int MIN_TRANS_LEVEL = 30;

    /**
     * 发起面对面交易的最低荣誉值（信誉值）
     */
    public static final int MIN_TRANS_HONOR = 50;

    /**
     * 交易记录
     */
    @Autowired
    private FaceTransDao transDao;

    /**
     * 用户服务
     */
    @Autowired
    private UserService userService;

    /**
     * 玩家服务
     */
    @Autowired
    private RoleService roleService;

    /**
     * 场景服务
     */
    @Autowired
    private SiteService siteService;

    /**
     * 物品服务
     */
    @Autowired
    private GoodsService goodsService;

    /**
     * 邮件服务
     */
    @Autowired
    private EmailService emailService;

    /**
     * 协议服务
     */
    @Autowired
    private ProtoService protoService;

    /**
     * 回复
     */
    private Message message;

    /**
     * 用户回复
     */
    private MsgUserInfoProto.ResponseUserInfo userResp;

    /**
     * 玩家回复
     */
    private MsgRoleProto.ResponseRole roleResp;

    /**
     * 面对面交易回复
     */
    private MsgFaceTransProto.ResponseFaceTrans faceResp;



    /**
     * 发起面对面交易申请 (参考楚留香的面对面交易交互)
     *
     * 1. 验证参数
     * 2. 判断双方是否在同一场景下
     * 3. 判断双方的等级
     * 4. 判断双方的荣誉值（信誉值）
     *
     * @param req       请求
     * @param channel   channel
     */
    public void initiate(MsgFaceTransProto.RequestFaceTrans req, Channel channel) {
        // 用户认证
        userResp = userService.userStateInterceptor(req.getUserId());
        if (userResp != null){
            channel.writeAndFlush(userResp);
            return;
        }
        // 玩家认证
        roleResp = roleService.roleInterceptor(req.getRoleId());
        if (roleResp != null){
            channel.writeAndFlush(roleResp);
            return;
        }

        // 获取双方的信息
        Role promoter = LocalUserMap.getUserRoleMap().get(req.getUserId());
        Role receiver = LocalUserMap.getIdRoleMap().get(req.getRoleId());

        // 判断双方是否在统一场景下
        if (!siteService.inSameSite(promoter, receiver)){
            sendFailedMsg(channel, ContentType.FACE_TRANS_APPLY_NOT_INT_SAME_SITE);
            return;
        }

        // 判断双方等级
        if (promoter.getLevel() < MIN_TRANS_LEVEL || receiver.getLevel() < MIN_TRANS_LEVEL){
            sendFailedMsg(channel, String.format(ContentType.FACE_TRANS_APPLY_FAILED_LEVEL, MIN_TRANS_LEVEL));
            return;
        }

        // 判断双方荣誉值
        if (promoter.getHonor() < MIN_TRANS_HONOR || receiver.getHonor() < MIN_TRANS_HONOR){
            sendFailedMsg(channel, String.format(ContentType.FACE_TRANS_APPLY_FAILED_HONOR, MIN_TRANS_HONOR));
            return;
        }

        // 申请交易申请
        applyFaceTrans(promoter, receiver);
    }


    /**
     * 接受面对面交易申请
     *
     * @param req       请求
     * @param channel   channel
     */
    public void yesTrans(MsgFaceTransProto.RequestFaceTrans req, Channel channel) {
        // 用户判断
        userResp = userService.userStateInterceptor(req.getUserId());
        if (userResp != null){
            channel.writeAndFlush(userResp);
            return;
        }

        // 判断玩家是否已经交易中
        faceResp = faceTransInterceptor(req.getUserId());
        if (faceResp != null){
            channel.writeAndFlush(faceResp);
            return;
        }

        // 判断交易申请是否过期
        long applyId = req.getTmpId();
        FaceTransApply apply = FaceTransCache.getApplyFaceTransCache().getIfPresent(applyId);
        if (apply == null){
            sendFailedMsg(channel, ContentType.FACE_TRANS_APPLY_JOIN_FAILED);
            return;
        }
        Role promoter = LocalUserMap.getIdRoleMap().get(apply.getPromoter());
        Role receiver = LocalUserMap.getIdRoleMap().get(apply.getReceiver());

        // 将双方的信息写入到缓存中，建立交易信息
        FaceTransCache.getRoleFaceTransMap().put(apply.getPromoter(), apply);
        FaceTransCache.getRoleFaceTransMap().put(apply.getReceiver(), apply);

        // 消息返回
        // 申请人发送申请被接受信息
        Channel proCh = ChannelCache.getUserIdChannelMap().get(promoter.getUserId());
        sendCommonMsg(proCh, String.format(ContentType.FACE_TRANS_APPLY_AGREE, receiver.getName()));

        // 向接收方发送同意成功消息
        sendCommonMsg(channel, ContentType.FACE_TRANS_APPLY_AGREE_SUCCESS);
    }


    /**
     * 拒绝面对面交易申请
     *
     * @param req       请求
     * @param channel   channel
     */
    public void noTrans(MsgFaceTransProto.RequestFaceTrans req, Channel channel) {
        // 用户判断
        userResp = userService.userStateInterceptor(req.getUserId());
        if (userResp != null){
            channel.writeAndFlush(userResp);
            return;
        }

        // 获取申请信息
        long applyId = req.getTmpId();
        FaceTransApply apply = FaceTransCache.getApplyFaceTransCache().getIfPresent(applyId);
        if (apply == null){
            sendFailedMsg(channel, ContentType.FACE_TRANS_APPLY_NOT_FOUND);
            return;
        }
        // 获取双方的玩家信息
        Role pro = LocalUserMap.getUserRoleMap().get(apply.getPromoter());
        Role rec = LocalUserMap.getUserRoleMap().get(apply.getReceiver());

        // 拒绝申请，将交易申请记录删除
        FaceTransCache.getApplyFaceTransCache().invalidate(applyId);

        // 向申请方发送拒绝消息
        Channel proCh = ChannelCache.getUserIdChannelMap().get(pro.getUserId());
        sendCommonMsg(proCh, String.format(ContentType.FACE_TRANS_APPLY_REFUSE, rec.getName()));

        // 接收方发送拒绝成功消息
        sendCommonMsg(channel, ContentType.FACE_TRANS_APPLY_REFUSE_SUCCESS);
    }


    /**
     * 发起交易信息
     *
     * @param req       请求
     * @param channel   channel
     */
    public void askTrans(MsgFaceTransProto.RequestFaceTrans req, Channel channel) {
        message = faceCommonInterceptor(req);
        if (message != null){
            channel.writeAndFlush(message);
            return;
        }

        Role promoter = LocalUserMap.getUserRoleMap().get(req.getUserId());
        FaceTransApply apply = FaceTransCache.getRoleFaceTransMap().get(promoter.getRoleId());
        Role receiver = LocalUserMap.getIdRoleMap().get(apply.getReceiver());

        // 获取请求参数
        long goodsId = req.getGoodsId();
        int num = req.getNum();
        int amount = req.getAmount();

        // 检查检查交易是否合理
        faceResp = checkTrans(promoter, goodsId, num, amount);
        if (faceResp != null){
            channel.writeAndFlush(faceResp);
            return;
        }

        // 记录下为确认的记录
        Transaction transaction = recordUnConfirmTrans(promoter, receiver, goodsId, num, amount);

        // 消息发送
        // 发送给订单接收人
        Channel reCh = ChannelCache.getUserIdChannelMap().get(receiver.getUserId());
        sendTransMsg(reCh, transaction);
        sendCommonMsg(channel, ContentType.FACE_TRANS_SUBMIT_RECORD_SEND_SUCCESS);
    }


    /**
     * 接受交易
     *
     * @param req       请求
     * @param channel   channel
     */
    public void acceptTrans(MsgFaceTransProto.RequestFaceTrans req, Channel channel) {
        // 状态判断
        message = faceCommonInterceptor(req);
        if (message != null){
            channel.writeAndFlush(message);
            return;
        }

        // 获取基本信息
        Role receiver = LocalUserMap.getUserRoleMap().get(req.getUserId());
        Transaction trans = FaceTransCache.getUnConfirmTransMap().get(receiver.getRoleId());

        // 判断玩家的金币是否足够购买
        boolean b = roleService.enoughPay(receiver, trans.getAmount());

        // 购买成功，获取玩家A的出售商品，调用发送物品邮件
        if (b){
            // 更新交易状态
            trans.setSuccess(1);
            trans.setModifyTime(new Date());
            int n = transDao.updateFaceTrans(trans);
            log.info("update face_trans, affected row: " + n);

            // 移除当前的交易单
            removeCurrentTrans(trans.getPromoter(), trans.getReceiver());

            // 移除交易人的物品信息
            goodsService.removeRoleGoods(trans.getPromoter(), trans.getGoodsId(), trans.getNum());

            // 交易人获得金币
            Role promoter = LocalUserMap.getIdRoleMap().get(trans.getPromoter());
            promoter.setGold(promoter.getGold() + trans.getAmount());
            roleService.updateRoleInfo(promoter);

            // 消息返回
            Channel proCh = ChannelCache.getUserIdChannelMap().get(promoter.getUserId());
            sendCommonMsg(proCh, String.format(ContentType.FACE_TRANS_SUCCESS_GAIN_GOLD, trans.getAmount()));


            // 接收人
            List<EmailGoods> goods = new ArrayList<>();
            EmailGoods ed = new EmailGoods();
            ed.setGid(trans.getGoodsId());
            ed.setNum(trans.getNum());
            goods.add(ed);

            // 邮件发送通知
            emailService.sendEmail(receiver, goods, EmailType.FACE_TRANS);
            sendCommonMsg(channel, ContentType.FACE_TRANS_SUCCESS);
        }else {
            sendFailedMsg(channel, ContentType.FACE_TRANS_NO_ENOUGH_GOLD_PAY);
        }
    }


    /**
     * 移除缓存中的交易订单
     *
     * @param promoter      交易人
     * @param receiver      接收人
     */
    private void removeCurrentTrans(Long promoter, Long receiver) {
        FaceTransCache.getUnConfirmTransMap().remove(promoter);
        FaceTransCache.getUnConfirmTransMap().remove(receiver);
    }



    /**
     * 拒绝交易
     *
     * @param req           请求
     * @param channel       channel
     */
    public void refuseTrans(MsgFaceTransProto.RequestFaceTrans req, Channel channel) {
        // 状态判断
        message = faceCommonInterceptor(req);
        if (message != null){
            channel.writeAndFlush(message);
            return;
        }
        // 获取基本信息
        Role receiver = LocalUserMap.getUserRoleMap().get(req.getUserId());
        Transaction trans = FaceTransCache.getUnConfirmTransMap().get(receiver.getRoleId());
        Role promoter = LocalUserMap.getIdRoleMap().get(trans.getPromoter());

        // 移除交易订单信息
        removeCurrentTrans(trans.getPromoter(), trans.getReceiver());

        // 消息通知双方
        sendCommonMsg(channel, ContentType.FACE_TRANS_RECEIVER_REFUSE_SUCCESS);
        Channel proCh = ChannelCache.getUserIdChannelMap().get(promoter.getUserId());
        sendCommonMsg(proCh, ContentType.FACE_TRANS_PROMOTER_REFUSE_SUCCESS);
    }


    /**
     * 离开交易状态
     *
     * @param req       请求
     * @param channel   channel
     */
    public void leaveTrans(MsgFaceTransProto.RequestFaceTrans req, Channel channel) {
        userResp = userService.userStateInterceptor(req.getUserId());
        if (userResp != null){
            channel.writeAndFlush(userResp);
            return;
        }
        // 获取用户信息
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());

        // 将交易状态去除
        FaceTransApply apply = FaceTransCache.getRoleFaceTransMap().get(role.getRoleId());
        if (apply != null){
            long otherId = apply.getReceiver().longValue() != role.getRoleId() ? apply.getReceiver() : apply.getPromoter();

            // 移除 trans, apply
            FaceTransCache.getUnConfirmTransMap().remove(role.getRoleId());
            FaceTransCache.getUnConfirmTransMap().remove(otherId);

            FaceTransCache.getRoleFaceTransMap().remove(role.getRoleId());
            FaceTransCache.getRoleFaceTransMap().remove(otherId);
            FaceTransCache.getApplyFaceTransCache().invalidate(apply.getId());

            // 消息通知双方
            Channel otherCh = ChannelCache.getUserIdChannelMap().get(LocalUserMap.getIdRoleMap().get(otherId).getUserId());
            sendCommonMsg(otherCh, ContentType.FACE_TRANS_OTHERS_LEAVE_STATE);
            sendCommonMsg(channel, ContentType.FACE_TRANS_LEAVE_STATE_SUCCESS);
        }
    }


    /**
     * 发起面对面交易申请
     *
     * @param promoter      申请人
     * @param receiver      接收人
     */
    private void applyFaceTrans(Role promoter, Role receiver) {
        // 构造申请信息
        FaceTransApply apply = new FaceTransApply();
        apply.setId(CommonUtil.generateLong());
        apply.setPromoter(promoter.getRoleId());
        apply.setReceiver(receiver.getRoleId());
        FaceTransCache.getApplyFaceTransCache().put(apply.getId(), apply);

        // 向接收方发送申请信息
        Channel recCh = ChannelCache.getUserIdChannelMap().get(receiver.getUserId());
        String msg = String.format(ContentType.FACE_TRANS_APPLY_RECEIVE, promoter.getName(), apply.getId());
        sendCommonMsg(recCh, msg);

        // 向发送方发送成功消息
        Channel proCh = ChannelCache.getUserIdChannelMap().get(promoter.getUserId());
        sendCommonMsg(proCh, ContentType.FACE_TRANS_APPLY_SEND_SUCCESS);
    }


    /**
     * 判断交易发起人的交易信息是否正确
     *
     * @param promoter      发起人
     * @param goodsId       交易物品
     * @param num           交易的数量
     * @param amount        交易的金额
     * @return              返回
     */
    private MsgFaceTransProto.ResponseFaceTrans checkTrans(Role promoter, long goodsId, int num, int amount) {
        if (!goodsService.containGoods(promoter, goodsId, num)){
            return combineFailedMsg(ContentType.FACE_TRANS_SUBMIT_FAILED_WITHOUT_GOODS);
        }

        // 获取物品的最低交易总额
        int all = goodsService.getGoodsMinTrans(goodsId, num);
        if (all > amount){
            return combineFailedMsg(ContentType.FACE_TRANS_SUMMIT_FAILED_NO_ENOUGH_GOLD);
        }
        return null;
    }



    /**
     * 记录下未确认的交易记录
     *
     * @param promoter  申请人
     * @param receiver  接收人
     * @param goodsId   物品id
     * @param num       数量
     * @param amount    交易金额
     */
    private Transaction recordUnConfirmTrans(Role promoter, Role receiver, long goodsId, int num, int amount) {
        // 构造交易订单对象，并写入数据库
        Transaction trans = new Transaction();
        trans.setPromoter(promoter.getRoleId());
        trans.setReceiver(receiver.getRoleId());
        trans.setGoodsId(goodsId);
        trans.setNum(num);
        trans.setAmount(amount);
        trans.setSuccess(0);
        trans.setCreateTime(new Date());
        trans.setModifyTime(new Date());

        int n = transDao.insertFaceTrans(trans);
        log.info("insert into face_trans, affected rows: " + n);

        // 本地存储记录
        FaceTransCache.getUnConfirmTransMap().put(receiver.getRoleId(), trans);
        FaceTransCache.getUnConfirmTransMap().put(promoter.getRoleId(), trans);

        return trans;
    }



    /**
     * 发送公共消息
     *
     * @param ch        channel
     * @param msg       消息
     */
    private void sendCommonMsg(Channel ch, String msg){
        faceResp = MsgFaceTransProto.ResponseFaceTrans.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(msg)
                .setType(MsgFaceTransProto.RequestType.INITIATE)
                .build();
        ch.writeAndFlush(faceResp);
    }



    /**
     * 发送失败消息
     *
     * @param ch    channel
     * @param msg   消息
     */
    private void sendFailedMsg(Channel ch, String msg){
        faceResp = MsgFaceTransProto.ResponseFaceTrans.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(msg)
                .build();
        ch.writeAndFlush(faceResp);
    }


    /**
     * 构造失败消息
     *
     * @param msg   消息
     * @return      协议返回
     */
    private MsgFaceTransProto.ResponseFaceTrans combineFailedMsg(String msg){
        faceResp = MsgFaceTransProto.ResponseFaceTrans.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(msg)
                .build();
        return faceResp;
    }

    /**
     * 判断玩家是否已经在交易当中
     *
     * @param userId    玩家id
     * @return          结果
     */
    private MsgFaceTransProto.ResponseFaceTrans faceTransInterceptor(long userId) {
        Role role = LocalUserMap.getUserRoleMap().get(userId);
        if (FaceTransCache.getRoleFaceTransMap().containsKey(role.getRoleId())){
            return MsgFaceTransProto.ResponseFaceTrans.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.FACE_TRANS_APPLY_HAS_IN)
                    .build();
        }
        return null;
    }

    /**
     * 判断玩家面对面交易连接是否断开
     *
     * @param role 玩家信息
     */
    private MsgFaceTransProto.ResponseFaceTrans faceTransConnInterceptor(Role role) {
        FaceTransApply apply = FaceTransCache.getRoleFaceTransMap().get(role.getRoleId());
        if (apply == null){
            return MsgFaceTransProto.ResponseFaceTrans.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.FACE_TRANS_APPLY_NO_IN)
                    .build();
        }
        return null;
    }


    /**
     * 公共拦截器
     *
     * @param req       请求
     * @return          返回
     */
    private Message faceCommonInterceptor(MsgFaceTransProto.RequestFaceTrans req){
        userResp = userService.userStateInterceptor(req.getUserId());
        if (userResp != null){
            return userResp;
        }
        // 判断交易连接
        Role receiver = LocalUserMap.getUserRoleMap().get(req.getUserId());
        faceResp = faceTransConnInterceptor(receiver);
        if (faceResp != null){
            return faceResp;
        }
        return null;
    }


    /**
     * 发送交易记录
     *
     * @param reCh          channel
     * @param transaction   交易记录
     */
    private void sendTransMsg(Channel reCh, Transaction transaction) {
        faceResp = MsgFaceTransProto.ResponseFaceTrans.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.FACE_TRANS_SUBMIT_RECORD_RECEIVE)
                .setType(MsgFaceTransProto.RequestType.ASK_TRANS)
                .setTrans(protoService.transToTransaction(transaction))
                .build();
        reCh.writeAndFlush(faceResp);
    }
}
