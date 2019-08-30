package com.ljh.gamedemo.service;

import com.google.common.collect.Lists;
import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.EmailType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.common.TradeType;
import com.ljh.gamedemo.dao.TradeDao;
import com.ljh.gamedemo.entity.EmailGoods;
import com.ljh.gamedemo.entity.Goods;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.entity.Trade;
import com.ljh.gamedemo.local.LocalGoodsMap;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.local.cache.TradeCache;
import com.ljh.gamedemo.local.channel.ChannelCache;
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
     * 玩家服务
     */
    @Autowired
    private RoleService roleService;

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
        channel.writeAndFlush(tradeResp);
    }


    /**
     * 获取交易行中的所有 拍卖交易
     *
     * @param req       请求
     * @param channel   channel
     */
    public void auctionAll(MsgTradeProto.RequestTrade req, Channel channel) {
        System.out.println(TradeCache.getTradeCache().asMap());
        TradeCache.getTradeCache().cleanUp();

        // 获取拍卖的所有交易信息
        List<Trade> trades = Lists.newArrayList(TradeCache.getAuctionTradeMap().values());

        tradeResp = combineResp(trades, MsgTradeProto.RequestType.AUCTION_ALL);
        channel.writeAndFlush(tradeResp);
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


    /**
     * 一口价购买商品
     *
     * @param req       请求
     * @param channel   channel
     */
    public synchronized void buyFixed(MsgTradeProto.RequestTrade req, Channel channel) {
        Role buyer = LocalUserMap.getUserRoleMap().get(req.getUserId());

        // 判断订单是否存在
        long tradeId = req.getTradeId();
        Trade trade = TradeCache.getTradeCache().getIfPresent(tradeId);
        if (trade == null){
            sendFailedMsg(channel, ContentType.TRADE_NOT_FOUND);
            return;
        }

        // 判断玩家是否有足够的钱购买物品
        if (!roleService.enoughPay(buyer, trade.getPrice())){
            sendFailedMsg(channel, ContentType.FACE_TRANS_NO_ENOUGH_GOLD_PAY);
            return;
        }
        // 购买成功，获取卖家信息
        Role seller = LocalUserMap.getIdRoleMap().get(trade.getSeller());

        // 实际购买操作
        doBuyFixed(seller, buyer, trade, trade.getPrice());

    }


    /**
     * 实际一口价购买操作
     *
     * @param seller    卖家信息
     * @param buyer     买家信息
     * @param trade     交易信息
     * @param price     交易价格
     */
    private void doBuyFixed(Role seller, Role buyer, Trade trade, int price) {
        // 买家获得金币
        seller.setGold(seller.getGold() + price);
        roleService.updateRoleInfo(seller);

        // 更新记录，并删除关联的交易信息
        updateTradeRecord(trade, seller);

        // 通知买家交易成功
        Channel channel = ChannelCache.getUserIdChannelMap().get(buyer.getUserId());
        sendCommonMsg(channel, ContentType.TRADE_BUY_SUCCESS);

        // 通知卖家交易完成
        notifySellerTradeComplete(seller, trade);

        // 发送物品
        emailService.sendEmail(buyer, Lists.newArrayList(new EmailGoods(trade.getGoodsId(), trade.getNum())),
                EmailType.TRADE_AUCTION);
    }


    /**
     * 交易竞拍操作
     *
     * @param req       请求
     * @param channel   channel
     */
    public synchronized void buyAuction(MsgTradeProto.RequestTrade req, Channel channel) {
        // 判断订单是否存在
        long tradeId = req.getTradeId();
        Trade trade = TradeCache.getTradeCache().getIfPresent(tradeId);
        if (trade == null){
            sendFailedMsg(channel, ContentType.TRADE_NOT_FOUND);
            return;
        }

        // 判断出价是否正确
        int price = req.getPrice();
        int low = trade.getPrice() + TradeType.LOWEST_AUCTION_PRICE;
        if (price < low){
            sendFailedMsg(channel, String.format(ContentType.TRADE_AUCTION_PRICE_FAILED, low));
            return;
        }
        Role buyer = LocalUserMap.getUserRoleMap().get(req.getUserId());

        // 进行竞拍操作
        doBuyAuction(buyer, trade, price);
    }


    /**
     * 具体的竞拍操作
     *
     * @param buyer     竞拍人
     * @param trade     交易记录
     * @param price     交易价格
     */
    private void doBuyAuction(Role buyer, Trade trade, int price) {
        // 竞拍他人
        if (trade.getBuyer() != 0L) {
            long othersId = trade.getBuyer();
            Role others = LocalUserMap.getIdRoleMap().get(othersId);

            // 将原先的竞拍人金币返回
            others.setGold(others.getGold() + trade.getPrice());
            roleService.updateRoleInfo(others);

            // 通知交易的物品被他人竞拍
            String name = goodsService.getGoodsName(trade.getGoodsId());
            Channel ch = ChannelCache.getUserIdChannelMap().get(others.getUserId());
            sendCommonMsg(ch, String.format(ContentType.TRADE_AUCTION_BY_OTHERS, name));

        }
        // 首次竞拍
        trade.setBuyer(buyer.getRoleId());
        trade.setPrice(price);

        buyer.setGold(buyer.getGold() - price);
        roleService.updateRoleInfo(buyer);

        // 更新交易信息
        List<Trade> tradeList = Optional.ofNullable(TradeCache.getRoleTradeMap().get(buyer.getRoleId()))
                .orElse(Lists.newArrayList(trade));
        TradeCache.getRoleTradeMap().put(buyer.getRoleId(), tradeList);
        TradeCache.getAuctionTradeMap().put(trade.getId(), trade);

        // 消息通知，竞拍成功
        Channel ch = ChannelCache.getUserIdChannelMap().get(buyer.getUserId());
        sendCommonMsg(ch, ContentType.TRADE_AUCTION_SUCCESS);
    }


    /**
     * 取消已经上架的交易信息
     *
     * @param req       请求
     * @param channel   channel
     */
    public void outOfTrade(MsgTradeProto.RequestTrade req, Channel channel) {
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());

        // 判断订单是否存在
        long tradeId = req.getTradeId();
        Trade trade = TradeCache.getTradeCache().getIfPresent(tradeId);
        if (trade == null){
            sendFailedMsg(channel, ContentType.TRADE_NOT_FOUND);
            return;
        }

        // 判断交易信息是否属于自己
        if (role.getRoleId().longValue() != trade.getSeller()){
            sendFailedMsg(channel, ContentType.TRADE_OUT_OF_FAILED_OTHERS);
            return;
        }

        // 判断交易物品是不是已经被他人竞拍
        if (trade.getType() == TradeType.AUCTION && trade.getBuyer() != 0L){
            sendFailedMsg(channel, ContentType.TRADE_OUT_OF_FAILED_SOMEONE_HAS_TRADE);
            return;
        }

        // 实际下架操作
        doOutOfTrade(role, trade);

        sendCommonMsg(channel, ContentType.TRADE_OUT_OF_SUCCESS);
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

        // 将物品移除背包
        goodsService.removeRoleGoods(role.getRoleId(), goodsId, num);

        // 存放总订单
        TradeCache.getTradeCache().put(trade.getId(), trade);

        // 区分订单存放
        if (type == TradeType.FIXED){
            TradeCache.getFixTradeMap().put(trade.getId(), trade);
        }
        TradeCache.getAuctionTradeMap().put(trade.getId(), trade);

        // 玩家关联订单存放
        List<Trade> trades = Optional.ofNullable(TradeCache.getRoleTradeMap().get(role.getRoleId()))
                .orElse(Lists.newArrayList());
        trades.add(trade);
        TradeCache.getRoleTradeMap().put(role.getRoleId(), trades);
    }


    /**
     * 实际下架物品操作
     *
     * @param role      玩家信息
     * @param trade     交易信息
     */
    private void doOutOfTrade(Role role, Trade trade) {
        // 将物品返回给玩家背包
        Goods goods = LocalGoodsMap.getIdGoodsMap().get(trade.getGoodsId());
        goodsService.sendRoleGoods(role, goods, trade.getNum());

        // 去除交易信息记录
        removeCurTradeRecord(trade);

        updateTradeRecord(trade, role);

        TradeCache.getTradeCache().invalidate(trade.getId());
    }



    /**
     * 生成交易信息，并存储DB
     *
     * @param role      玩家
     * @param goodsId   物品id
     * @param num       物品数量
     * @param price     物品价格
     * @param type      物品类型
     * @return          交易信息
     */
    private Trade generateTrade(Role role, long goodsId, int num, int price, int type) {
        Trade trade = new Trade();
        trade.setSeller(role.getRoleId());
        trade.setBuyer(0L);
        trade.setGoodsId(goodsId);
        trade.setNum(num);
        trade.setPrice(price);
        trade.setType(type);
        trade.setProcess(TradeType.TRADE_STATE_ON);
        trade.setStartTime(new Date());

        // 设置结束的购买时间
        trade.setEndTime(DateTime.now().plus(Minutes.minutes(TradeType.AUCTION_DURATION)).toDate());

        int n = tradeDao.insertTrade(trade);
        log.info("inert into trade, affected rows: " + n);

        return trade;
    }


    /**
     * 完成拍卖交易
     *
     * @param trade     交易信息
     */
    public synchronized void completeAuctionTrade(Trade trade) {
        Role seller = LocalUserMap.getIdRoleMap().get(trade.getSeller());
        Role buyer = LocalUserMap.getIdRoleMap().get(trade.getBuyer());

        // 更新交易信息
        updateTradeRecord(trade, seller);

        // 移除买家的交易记录
        TradeCache.getRoleTradeMap().get(buyer.getRoleId()).removeIf(t -> t.getBuyer().longValue() == buyer.getRoleId());

        // 向买家发送物品邮件
        List<EmailGoods> emailGoods = Lists.newArrayList(new EmailGoods(trade.getGoodsId(), trade.getNum()));
        emailService.sendEmail(buyer, emailGoods, EmailType.TRADE_AUCTION);

        // 卖家获取交易金币
        seller.setGold(seller.getGold() + trade.getPrice());
        roleService.updateRoleInfo(seller);

        // 通知买卖双方交易完成
        notifySellerTradeComplete(seller, trade);
        notifyBuyerTradeComplete(buyer, trade);
    }




    /**
     * 交易时间到，无人竞拍，物品返回，删除物品信息
     *
     * @param trade     交易信息
     */
    public void removeTradeReturn(Trade trade) {
        // 获取卖家，物品信息
        Role role = LocalUserMap.getIdRoleMap().get(trade.getSeller());
        Goods goods = LocalGoodsMap.getIdGoodsMap().get(trade.getGoodsId());

        // 将物品返回给玩家
        goodsService.sendRoleGoods(role, goods, trade.getNum());

        // 更新交易记录
        updateTradeRecord(trade, role);
    }


    /**
     * 更新 Db 中的交易记录，并更新本地缓存
     *
     * @param trade     交易信息
     * @param role      玩家信息
     */
    private void updateTradeRecord(Trade trade, Role role){
        // 更新交易信息
        trade.setProcess(TradeType.TRADE_STATE_OFF);
        int n = tradeDao.updateTrade(trade);
        log.info("update trade, affected rows: " + n);

        // 移除玩家的当前交易记录
        TradeCache.getRoleTradeMap().get(role.getRoleId()).removeIf(t -> t.getId().longValue() == trade.getId());

        // 移除类型在线交易记录
        removeCurTradeRecord(trade);
    }


    /**
     * 通知卖家交易已经完成
     *
     * @param seller    卖家
     * @param trade     交易记录
     */
    private void notifySellerTradeComplete(Role seller, Trade trade){
        // 向买家发送售出的消息
        String goodsName = goodsService.getGoodsName(trade.getGoodsId());
        Channel sellerCh = ChannelCache.getUserIdChannelMap().get(seller.getUserId());
        sendCommonMsg(sellerCh, String.format(ContentType.TRADE_AUCTION_SELL_SUCCESS, goodsName, trade.getPrice()));
    }


    /**
     * 通知买家成功竞拍到商品
     *
     * @param buyer
     * @param trade
     */
    private void notifyBuyerTradeComplete(Role buyer, Trade trade) {
        Channel ch = ChannelCache.getUserIdChannelMap().get(buyer.getUserId());
        String name = goodsService.getGoodsName(trade.getGoodsId());
        sendCommonMsg(ch, String.format(ContentType.TRADE_AUCTION_SUCCESS_GET, trade.getPrice(), name));
    }



    /**
     * 移除当前交易行中的交易记录
     *
     * @param trade     交易信息
     */
    public void removeCurTradeRecord(Trade trade){
        if (trade.getType() == TradeType.AUCTION){
            TradeCache.getAuctionTradeMap().remove(trade.getId());
        }
        TradeCache.getFixTradeMap().remove(trade.getId());
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
