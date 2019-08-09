package com.ljh.gamedemo.service;

import com.ljh.gamedemo.common.CommodityType;
import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.EmailType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.dao.MallOrderDao;
import com.ljh.gamedemo.dao.RoleEquipDao;
import com.ljh.gamedemo.dao.RoleItemsDao;
import com.ljh.gamedemo.dao.UserRoleDao;
import com.ljh.gamedemo.entity.*;
import com.ljh.gamedemo.entity.dto.RoleEquipHas;
import com.ljh.gamedemo.entity.tmp.MallBuyTimes;
import com.ljh.gamedemo.local.LocalCommodityMap;
import com.ljh.gamedemo.local.LocalEquipMap;
import com.ljh.gamedemo.local.LocalItemsMap;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.local.cache.MallBuyCache;
import com.ljh.gamedemo.proto.protoc.CommodityProto;
import com.ljh.gamedemo.proto.protoc.MsgMallProto;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: Heiku
 * @Date: 2019/8/2
 *
 *
 *  商城操作
 */

@Service
@Slf4j
public class MallService {

    @Autowired
    private UserRoleDao roleDao;

    @Autowired
    private RoleItemsDao itemsDao;

    @Autowired
    private RoleEquipDao equipDao;

    @Autowired
    private MallOrderDao orderDao;



    @Autowired
    private EmailService emailService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private EquipService equipService;

    @Autowired
    private ProtoService protoService;

    private MsgMallProto.ResponseMall response;

    /**
     * 获取商店的刷新列表
     *
     * @param request
     * @return
     */
    public MsgMallProto.ResponseMall getMall(MsgMallProto.RequestMall request, Channel channel) {
        Role role = LocalUserMap.userRoleMap.get(request.getUserId());


        // 获取随机的商品
        List<Items> items = LocalCommodityMap.getItemsList();
        List<Equip> equips = LocalCommodityMap.getEquipsList(role.getRoleId());

        // 组合返回列表
        List<CommodityProto.Commodity> commodities = combineToCommodities(items, equips);

        // 消息返回
        responseCommodityList(commodities, channel);

        return null;
    }


    /**
     * 购买商品
     *
     * @param request
     * @return
     */
    public MsgMallProto.ResponseMall buyMall(MsgMallProto.RequestMall request, Channel channel) {
        response = commonInterceptor(request);
        if (response != null){
            return response;
        }

        // 获取玩家属性
        long userId = request.getUserId();

        // 获取购买的商品id
        long cid = request.getCId();
        Commodity c = LocalCommodityMap.getIdCommodityMaps().get(cid);

        // 购买数量
        int num = request.getNum();

        // 同步购买
        synDoBuySelect(userId, c, num, channel);

        return null;
    }


    /**
     * 同步进行购买操作
     *
     * @param userId
     * @param c
     * @param channel
     */
    private synchronized void synDoBuySelect(long userId, Commodity c, int num, Channel channel) {
        // 获取玩家信息
        Role role = LocalUserMap.userRoleMap.get(userId);

        int ownGold = role.getGold();
        int price = c.getPrice();

        // 金钱不足，购买失败
        if (ownGold < price * num){
            responseFailed(channel, ContentType.MALL_MONEY_NOT_ENOUGH);
            return;
        }

        // 商品限制的最大购买量
        int maxLimit = c.getLimit();
        List<MallBuyTimes> timesList = MallBuyCache.getBuyTimesCache().getIfPresent(role.getRoleId());
        log.info("玩家：" + role.getName() + " 更新前的最大购买记录为；" + timesList);
        if (timesList != null && !timesList.isEmpty()){
            timesList.forEach(t -> {

                // 找到对应购买商品的id
                if (t.getCId().intValue() == c.getId()){

                    // 可进行购买
                    if (t.getTimes() + num <= maxLimit){

                        // 具体的购买操作
                        doBuy(role, c, num, channel);

                        // 更新缓存
                        t.setTimes(t.getTimes() + 1);
                        return;
                    }else {
                        responseFailed(channel, ContentType.MALL_BUY_OUT_OF_LIMIT);
                    }
                }
            });
        }

        // 第一次购买
        timesList = new ArrayList<>();
        MallBuyTimes times = new MallBuyTimes();
        times.setCId(c.getId());
        times.setTimes(times.getTimes() + num);
        timesList.add(times);
        MallBuyCache.getBuyTimesCache().put(role.getRoleId(), timesList);

        log.info("玩家：" + role.getName() + " 更新后的最大购买记录为：" + timesList);

        doBuy(role, c, num, channel);
    }


    /**
     * 玩家可以购买，具体的购买操作
     *
     * 1.玩家扣除金币
     * 2.玩家获得物品
     *
     *
     * @param role
     * @param c
     * @param channel
     */
    private void doBuy(Role role, Commodity c, int num, Channel channel) {

        // 更新玩家金币值
        updateRoleGold(role, c, num);

        // 生成订单，并存入Db 中
        MallOrder order = generateOrder(role, c, num);
        orderDao.insertMallOrder(order);

        // 异步发送邮件通知
        sendCommodityEmail(role, order, EmailType.BUY);

        response = MsgMallProto.ResponseMall.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgMallProto.RequestType.BUY)
                .setContent(ContentType.MALL_BUY_SUCCESS)
                .build();

        channel.writeAndFlush(response);
    }

    /**
     * 填充邮件物品信息，调用 emailService 发送邮件
     *
     * @param role
     * @param order
     * @param type
     */
    private void sendCommodityEmail(Role role, MallOrder order, EmailType type) {
        List<EmailGoods> edList = new ArrayList<>();

        // 构建邮件物品实体
        EmailGoods ed = new EmailGoods();
        ed.setGid(order.getCid());
        ed.setNum(order.getNum());

        edList.add(ed);

        // 发送邮件
        emailService.sendEmail(role, edList, EmailType.BUY);
    }


    /**
     * 生成订单实体类
     *
     * @param role
     * @param c
     * @param num
     * @return
     */
    private MallOrder generateOrder(Role role, Commodity c, int num) {
        MallOrder mallOrder = new MallOrder();
        mallOrder.setRoleId(role.getRoleId());
        mallOrder.setCid(c.getId());
        mallOrder.setNum(num);
        mallOrder.setCost(c.getPrice() * num);
        mallOrder.setCreateTime(new Date());

        return mallOrder;
    }


    /**
     * 缓存，Db 更新玩家的金币值
     *
     * @param role
     * @param c
     */
    private void updateRoleGold(Role role, Commodity c, int num){
        log.info("玩家：" + role.getName() + " 购买商品前，金币为：" + role.getGold());

        role.setGold(role.getGold() - c.getPrice() * num);

        // 更新 role 信息
        // cache
        LocalUserMap.idRoleMap.put(role.getRoleId(), role);

        LocalUserMap.siteRolesMap.get(role.getSiteId()).forEach(r -> {
            if (r.getRoleId().intValue() == role.getRoleId()){
                r.setGold(role.getGold());
            }
        });

        // db
        roleDao.updateRoleSiteInfo(role);

        log.info("玩家：" + role.getName() + " 购买商品后，金币为：" + role.getGold());
    }


    /**
     * 向玩家发放物品
     *
     * @param r         玩家
     * @param goods     物品
     * @param num       数量
     */
    public void sendRoleGoods(Role r, Goods goods, int num){
        // 获取发放物品的状态
        int type = goods.getType();
        if (type == CommodityType.ITEM.getCode()){

            // 添加玩家的物品数量
            itemService.addRoleItems(r, goods.getGid(), num);
        }

        else if (type == CommodityType.EQUIP.getCode()){

            // 添加玩家的装备数量
            List<Goods> gList = new ArrayList<>();
            gList.add(goods);
            equipService.addRoleEquips(r, gList);
        }
    }


    /**
     * 组合商品列表
     *
     * @param items
     * @param equips
     * @return
     */
    private List<CommodityProto.Commodity> combineToCommodities(List<Items> items, List<Equip> equips) {
        System.out.println(items);
        System.out.println(equips);
        List<Commodity> list = new ArrayList<>();

        items.forEach(i -> {
            long id = i.getItemsId();
            Commodity tmp = LocalCommodityMap.getIdCommodityMaps().get(id);
            Commodity c = new Commodity();
            BeanUtils.copyProperties(tmp, c);
            c.setItems(i);
            list.add(c);
        });

        equips.forEach(e -> {
            long id = e.getEquipId();
            Commodity tmp = LocalCommodityMap.getIdCommodityMaps().get(id);
            Commodity c = new Commodity();
            BeanUtils.copyProperties(tmp, c);
            c.setEquip(e);
            list.add(c);
        });

        System.out.println(list);
        List<CommodityProto.Commodity> res = protoService.transToCommodityList(list);

        return res;
    }


    /**
     * 构造消息返回
     *
     * @param commodities
     * @param channel
     */
    private void responseCommodityList(List<CommodityProto.Commodity> commodities, Channel channel) {
        response = MsgMallProto.ResponseMall.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgMallProto.RequestType.MALL)
                .setContent(ContentType.MALL_FIND_SUCCESS)
                .addAllCommodity(commodities)
                .build();

        channel.writeAndFlush(response);
    }


    /**
     * 返回失败的消息回复
     *
     * @param channel
     * @param content
     */
    private void responseFailed(Channel channel, String content) {
        response = MsgMallProto.ResponseMall.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(content)
                .build();

        channel.writeAndFlush(response);
    }










    /**
     * 用户状态拦截器
     *
     * @param request
     * @return
     */
    private MsgMallProto.ResponseMall userStateInterceptor(MsgMallProto.RequestMall request){
        // 用户id标识判断
        long userId = request.getUserId();
        if (userId <= 0){
            return MsgMallProto.ResponseMall.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.USER_TOKEN_DATA_EMPTY)
                    .build();
        }
        // 找不到对应的角色信息
        Role role = LocalUserMap.userRoleMap.get(userId);
        if (role == null){
            return MsgMallProto.ResponseMall.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.ROLE_EMPTY)
                    .build();
        }
        return null;
    }


    /**
     * 商品状态判断
     *
     * @param request
     * @return
     */
    private MsgMallProto.ResponseMall commodityInterceptor(MsgMallProto.RequestMall request) {

        long cid = request.getCId();
        if (cid <= 0 || LocalCommodityMap.getIdCommodityMaps().get(cid) == null){
            return MsgMallProto.ResponseMall.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.MALL_FIND_FAILED)
                    .build();
        }
        return null;
    }

    /**
     * 购买数量判断
     *
     * @param request
     * @return
     */
    private MsgMallProto.ResponseMall numInterceptor(MsgMallProto.RequestMall request) {
        int num = request.getNum();

        long cid = request.getCId();
        Commodity c = LocalCommodityMap.getIdCommodityMaps().get(cid);

        if (num > c.getLimit()){
            return MsgMallProto.ResponseMall.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.MALL_BUY_NUM_GT_LIMIT)
                    .build();
        }
        return null;
    }


    /**
     * 公共消息拦截器
     *
     * @param request
     * @return
     */
    private MsgMallProto.ResponseMall commonInterceptor(MsgMallProto.RequestMall request){
        // 用户状态判断
        response = userStateInterceptor(request);
        if (response != null){
            return response;
        }

        // 商品状态判断
        response = commodityInterceptor(request);
        if (response != null){
            return response;
        }

        // 购买商品的判断
        response = numInterceptor(request);
        if (response != null){
            return response;
        }
        return null;
    }
}
