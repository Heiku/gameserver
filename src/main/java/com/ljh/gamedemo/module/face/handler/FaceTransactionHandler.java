package com.ljh.gamedemo.module.face.handler;

import com.ljh.gamedemo.proto.protoc.MsgFaceTransProto;
import com.ljh.gamedemo.module.face.service.FaceTransService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.ljh.gamedemo.server.request.RequestFaceTransType.*;

/**
 * 面对面交易请求处理器
 *
 * @Author: Heiku
 * @Date: 2019/8/22
 */
public class FaceTransactionHandler extends SimpleChannelInboundHandler<MsgFaceTransProto.RequestFaceTrans> {

    /**
     * 面对面交易服务
     */
    private static FaceTransService faceTransService;

    static {
        faceTransService = SpringUtil.getBean(FaceTransService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgFaceTransProto.RequestFaceTrans req) throws Exception {
        int type = req.getTypeValue();
        Channel channel = ctx.channel();

        switch (type){
            case INITIATE:
                faceTransService.initiate(req, channel);
                break;
            case YES_TRANS:
                faceTransService.yesTrans(req, channel);
                break;
            case NO_TRANS:
                faceTransService.noTrans(req, channel);
                break;
            case ASK_TRANS:
                faceTransService.askTrans(req, channel);
                break;
            case ACCEPT_TRANS:
                faceTransService.acceptTrans(req, channel);
                break;
            case REFUSE_TRANS:
                faceTransService.refuseTrans(req, channel);
                break;
            case LEAVE_TRANS:
                faceTransService.leaveTrans(req, channel);
                break;
        }
    }
}
