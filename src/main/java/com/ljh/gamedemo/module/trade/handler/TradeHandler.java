package com.ljh.gamedemo.module.trade.handler;

import com.ljh.gamedemo.proto.protoc.MsgTradeProto;
import com.ljh.gamedemo.module.trade.service.TradeService;
import com.ljh.gamedemo.module.user.service.UserService;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Objects;

import static com.ljh.gamedemo.server.request.RequestTradeType.*;

/**
 * 交易行请求处理器
 *
 * @Author: Heiku
 * @Date: 2019/8/29
 */
public class TradeHandler extends SimpleChannelInboundHandler<MsgTradeProto.RequestTrade> {

    /**
     * 用户服务
     */
    private static UserService userService;

    /**
     * 交易服务
     */
    private static TradeService tradeService;

    static {
        userService = SpringUtil.getBean(UserService.class);
        tradeService = SpringUtil.getBean(TradeService.class);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgTradeProto.RequestTrade req) throws Exception {
        // 用户判断
        MsgUserInfoProto.ResponseUserInfo userResp = userService.userStateInterceptor(req.getUserId());
        if (!Objects.isNull(userResp)){
            ctx.channel().writeAndFlush(userResp);
            return;
        }


        int type = req.getTypeValue();
        Channel channel = ctx.channel();

        switch (type){
            case TRADE_STATE:
                tradeService.tradeState(req, channel);
                break;
            case FIXED_PRICE_ALL:
                tradeService.fixedPriceAll(req, channel);
                break;
            case AUCTION_ALL:
                tradeService.auctionAll(req, channel);
                break;
            case PUT_GOODS:
                tradeService.putGoods(req, channel);
                break;
            case BUY_FIXED:
                tradeService.buyFixed(req, channel);
                break;
            case BUY_AUCTION:
                tradeService.buyAuction(req, channel);
                break;
            case OUT_OF_TRADE:
                tradeService.outOfTrade(req, channel);
                break;
        }
    }
}
