package com.ljh.gamedemo.service;

import com.google.common.collect.Lists;
import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.common.TradeType;
import com.ljh.gamedemo.dao.TradeDao;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.entity.Trade;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.local.cache.TradeCache;
import com.ljh.gamedemo.proto.protoc.MsgTradeProto;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 交易的具体操作
 *
 * @Author: Heiku
 * @Date: 2019/8/29
 */

@Service
@Slf4j
public class TradeService {

    /**
     * TradeDao
     */
    @Autowired
    private TradeDao tradeDao;

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
     * 交易协议返回
     */
    private MsgTradeProto.ResponseTrade tradeResp;

    /**
     * 获取当前的交易状态
     *
     * @param req       请求
     * @param channel   channel
     */
    public void tradeState(MsgTradeProto.RequestTrade req, Channel channel) {
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());

        // 获取玩家关联的交易
        List<Trade> trades = TradeCache.getRoleTradeMap().get(role.getRoleId());

        tradeResp = combineResp(trades, MsgTradeProto.RequestType.TRADE_STATE);
        channel.writeAndFlush(tradeResp);
    }


    /**
     * 获取交易行中所有的 一口价交易
     *
     * @param req           请求
     * @param channel       channel
     */
    public void fixedPriceAll(MsgTradeProto.RequestTrade req, Channel channel) {
        // 获取一口价的所有交易信息
        List<Trade> trades = Lists.newArrayList(TradeCache.getFixTradeMap().values());

        tradeResp = combineResp(trades, MsgTradeProto.RequestType.FIXED_PRICE_ALL);
        channel.writeAndFlush(trades);
    }


    /**
     * 获取交易行中的所有 拍卖交易
     *
     * @param req       请求
     * @param channel   channel
     */
    public void auctionAll(MsgTradeProto.RequestTrade req, Channel channel) {
        // 获取拍卖的所有交易信息
        List<Trade> trades = Lists.newArrayList(TradeCache.getAuctionTradeMap().values());

        tradeResp = combineResp(trades, MsgTradeProto.RequestType.AUCTION_ALL);
        channel.writeAndFlush(trades);
    }


    /**
     * 上架物品交易信息
     *
     * 1.判断玩家是否拥有该物品
     * 2.创建交易单
     *
     * @param req       请求
     * @param channel   channel
     */
    public synchronized void putGoods(MsgTradeProto.RequestTrade req, Channel channel) {
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());

        // 获取参数的基本信息
        long goodsId = req.getGoodsId();
        int num = req.getNum();
        int price = req.getPrice();
        int type = req.getTradeType();

        // 判断玩家是否拥有该物品
        if (!goodsService.containGoods(role, goodsId, num)){
            sendFailedMsg(channel, ContentType.TRADE_PUT_FAILED_NOT_ENOUGH);
            return;
        }

        // 构建交易单
        doPutGoods(role, goodsId, num, price, type);

        // 消息返回
        sendCommonMsg(channel, ContentType.TRADE_PUT_SUCCESS);
    }



    public void buyFixed(MsgTradeProto.RequestTrade req, Channel channel) {
    }

    public void buyAuction(MsgTradeProto.RequestTrade req, Channel channel) {
    }

    public void outOfTrade(MsgTradeProto.RequestTrade req, Channel channel) {
    }


    /**
     * 构建交易行记录，记录存储
     *
     * @param role      玩家信息
     * @param goodsId   物品id
     * @param num       物品数量
     * @param price     交易价格
     * @param type      类型
     */
    private void doPutGoods(Role role, long goodsId, int num, int price, int type) {
        // 构建交易订单对象
        Trade trade = generateTrade(role, goodsId, num, price, type);

        // 存放总订单
        TradeCache.getAllTradeMap().put(trade.getTradeId(), trade);

        // 区分订单存放
        if (type == TradeType.FIXED){
            TradeCache.getFixTradeMap().put(trade.getTradeId(), trade);
        }
        TradeCache.getAuctionTradeMap().put(trade.getTradeId(), trade);

        // 玩家关联订单存放
        List<Trade> trades = Optional.ofNullable(TradeCache.getRoleTradeMap().get(role.getRoleId()))
                .orElse(Lists.newArrayList());
        trades.add(trade);
        TradeCache.getRoleTradeMap().put(role.getRoleId(), trades);
    }


    /**
     * 生成交易信息，并存储DB
     *
     * @param role      玩家
     * @param goodsId   物品id
     * @param num       物品数量
     * @param price     物品价格
     * @param type      物品类型
     * @return
     */
    private Trade generateTrade(Role role, long goodsId, int num, int price, int type) {
        Trade trade = new Trade();
        trade.setSeller(role.getRoleId());
        trade.setGoodsId(goodsId);
        trade.setNum(num);
        trade.setPrice(price);
        trade.setType(type);
        trade.setProcess(1);
        trade.setStartTime(new Date());

        // 设置结束的购买时间
        trade.setEndTime(DateTime.now().plus(Minutes.minutes(TradeType.AUCTION_DURATION)).toDate());

        int n = tradeDao.insertTrade(trade);
        log.info("inert into trade, affected rows: " + n);

        return trade;
    }


    /**
     * 构建交易协议返回
     *
     * @param trades        交易信息
     * @param type          交易类型
     * @return              协议返回
     */
    private MsgTradeProto.ResponseTrade combineResp(List<Trade> trades, MsgTradeProto.RequestType type) {
        return MsgTradeProto.ResponseTrade.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(type)
                .addAllTrade(protoService.transToTradeList(trades))
                .build();
    }


    /**
     * 直接返回失败消息
     *
     * @param channel   channel
     * @param msg       消息
     */
    private void sendFailedMsg(Channel channel, String msg){
        tradeResp = MsgTradeProto.ResponseTrade.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(msg)
                .build();
        channel.writeAndFlush(tradeResp);
    }


    /**
     * 直接返回结果消息
     *
     * @param channel   channel
     * @param msg       消息
     */
    private void sendCommonMsg(Channel channel, String msg){
        tradeResp = MsgTradeProto.ResponseTrade.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgTradeProto.RequestType.COMMON_TRADE)
                .setContent(msg)
                .build();
        channel.writeAndFlush(tradeResp);
    }
}
