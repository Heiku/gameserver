package com.ljh.gamedemo.module.items.service;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.module.items.dao.RoleItemsDao;
import com.ljh.gamedemo.module.equip.bean.Equip;
import com.ljh.gamedemo.module.items.bean.Items;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.equip.local.LocalEquipMap;
import com.ljh.gamedemo.module.items.local.LocalItemsMap;
import com.ljh.gamedemo.module.user.local.LocalUserMap;
import com.ljh.gamedemo.proto.protoc.MsgItemProto;
import com.ljh.gamedemo.run.manager.UserExecutorManager;
import com.ljh.gamedemo.run.db.SaveRoleItemRun;
import com.ljh.gamedemo.run.manager.SaveRoleItemManager;
import com.ljh.gamedemo.run.record.FutureMap;
import com.ljh.gamedemo.run.record.RecoverBuff;
import com.ljh.gamedemo.run.user.UseItemNowRun;
import com.ljh.gamedemo.run.user.UseItemScheduledRun;
import com.ljh.gamedemo.module.base.service.ProtoService;
import io.netty.channel.Channel;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.ljh.gamedemo.common.ItemsType.*;

/**
 * @Author: Heiku
 * @Date: 2019/7/16
 */

@Slf4j
@Service
public class ItemService {

    /**
     * RoleItemsDao
     */
    @Autowired
    private RoleItemsDao roleItemsDao;

    /**
     * 协议服务
     */
    @Autowired
    private ProtoService protoService;


    /**
     * 物品协议返回
     */
    private MsgItemProto.ResponseItem itemResp;


    /**
     * 获取当前用户角色的所有背包物品
     *
     * @param request       请求
     * @param channel       channel
     */
    public void getAll(MsgItemProto.RequestItem request, Channel channel){
        long userId = request.getUserId();
        long roleId = LocalUserMap.userRoleMap.get(userId).getRoleId();

        // 获取当前用户的背包物品
        List<Items> items = LocalItemsMap.getRoleItemsMap().get(roleId);

        // 获取所有的装备信息
        List<Equip> roleEquipList = LocalEquipMap.getHasEquipMap().get(roleId);

        // 非空背包
        itemResp = MsgItemProto.ResponseItem.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.FIND_SUCCESS)
                .setType(MsgItemProto.RequestType.ALL)
                .addAllItem(protoService.transToItemsList(items))
                .addAllEquip(protoService.transToEquipList(roleEquipList))
                .build();
        channel.writeAndFlush(itemResp);
    }


    /**
     * 物品使用
     *
     * @param requestItem       请求
     * @param channel           channel
     */
    public void useItem(MsgItemProto.RequestItem requestItem, Channel channel){
        // 物品状态判断
        itemResp = itemStateInterceptor(requestItem);
        if (itemResp != null){
            channel.writeAndFlush(itemResp);
            return;
        }

        long userId = requestItem.getUserId();
        long itemsId = requestItem.getItemsId();
        Role role = LocalUserMap.getUserRoleMap().get(userId);

        // 获取物品
        Items items = LocalItemsMap.getIdItemsMap().get(itemsId);

        // 使用物品
        doUseItem(role, items, channel);
    }


    /**
     * 根据物品的类别 进行对应的使用操作
     *
     * @param role          玩家信息
     * @param items         物品信息
     */
    private void doUseItem(Role role, Items items, Channel channel){
        int type = items.getType();
        switch (type){
            case BLOOD:
                recoverBlood(role, items, channel);
                break;
            case BLUE:
                recoverBlue(role, items, channel);
                break;
        }
    }



    /**
     * 具体的回血操作
     *
     * @param role          玩家信息
     * @param items         物品信息
     */
    private void recoverBlood(Role role, Items items, Channel channel){

        RecoverBuff buff = new RecoverBuff();
        buff.setHpBuf(items.getUp());
        buff.setMpBuf(0);

        // 直接恢复
        if (items.getSec() == 0){

            // 执行立即恢复的任务
            UserExecutorManager.addUserTask(role.getUserId(), new UseItemNowRun(role, items.getItemsId(), buff));
        }else {
            buff.setHpBuf(items.getUp() / items.getSec());
            UseItemScheduledRun r = new UseItemScheduledRun(role, items, buff);

            // 缓慢恢复，定期增加用户状态值
            ScheduledFuture future = UserExecutorManager.getUserExecutor(role.getUserId())
                    .scheduleAtFixedRate(r, 0, 1, TimeUnit.SECONDS);
            FutureMap.futureMap.put(r.hashCode(), future);
        }
    }


    /**
     * 具体的回蓝操作
     *
     * @param role          玩家信息
     * @param items         物品信息
     */
    private void recoverBlue(Role role, Items items, Channel channel){

        RecoverBuff buff = new RecoverBuff();
        buff.setHpBuf(0);
        buff.setMpBuf(items.getUp());
        // 直接恢复
        if (items.getSec() == 0){

            // 执行立即恢复的任务
            UserExecutorManager.addUserTask(role.getUserId(), new UseItemNowRun(role, items.getItemsId(), buff));

        }else {
            buff.setMpBuf(items.getUp() / items.getSec() * 2);
            UseItemScheduledRun r = new UseItemScheduledRun(role, items, buff);

            // 缓慢恢复，定期增加用户状态值
            ScheduledFuture future = UserExecutorManager.getUserExecutor(role.getUserId())
                    .scheduleAtFixedRate(r, 0, 2, TimeUnit.SECONDS);
            FutureMap.futureMap.put(r.hashCode(), future);
        }
    }


    /**
     * 为玩家添加物品
     *
     * @param r         玩家
     * @param gid       物品id
     * @param num       物品数量
     */
    public void addRoleItems(Role r, long gid, int num){

        Items items = LocalItemsMap.getIdItemsMap().get(gid);

        // 当前玩家所拥有的物品数量
        List<Items> itemsList = LocalItemsMap.getRoleItemsMap().get(r.getRoleId());
        // 玩家拥有的物品id
        List<Long> itemsIdList = LocalItemsMap.getRoleItemsIdMap().get(r.getRoleId());

        // 空背包物品
        if (itemsList == null){
            itemsList = new ArrayList<>();
            itemsIdList = new ArrayList<>();
        }

        // 添加的物品是新物品
        if (itemsIdList.isEmpty() || !itemsIdList.contains(gid)){

            // 插入物品信息
            insertItemInfo(r, itemsList, itemsIdList, items, num);

        // 已经拥有的物品
        }else {

            // 更新物品信息
            updateItemInfo(r, itemsList, items, num);
        }

        // 更新缓存信息
        LocalItemsMap.getRoleItemsMap().put(r.getRoleId(), itemsList);
        LocalItemsMap.getRoleItemsIdMap().put(r.getRoleId(), itemsIdList);

        log.info("玩家：" + r.getName() + " 购买物品后，物品列表为：" + items);
    }


    /**
     * 更新玩家物品
     *
     * @param r     玩家信息
     * @param gid   物品id
     * @param num   数量
     */
    public void updateRoleItems(Role r, long gid, int num){
        List<Items> itemsList = LocalItemsMap.getRoleItemsMap().get(r.getRoleId());
        if (itemsList == null){
            return;
        }

        Optional<Items> result = itemsList.stream().filter(i -> i.getItemsId() == gid).findFirst();
        if (result.isPresent()){
            Items items = result.get();
            items.setNum(items.getNum() + num);

            if (items.getNum() == 0){
                removeRoleItem(r, items);
            }

            LocalItemsMap.getRoleItemsMap().put(r.getRoleId(), itemsList);
            log.info("更新RoleItemList后，数量为：" + itemsList);

            // 同步更新数据库
            SaveRoleItemRun run = new SaveRoleItemRun(items, r);
            SaveRoleItemManager.addQueue(run);
        }
    }




    /**
     * 加入背包的物品是新物品（玩家未拥有的）， 直接新增玩家的物品信息
     *
     * @param r             玩家信息
     * @param itemsList     拥有的物品列表
     * @param idList        拥有的物品id列表
     * @param items         物品信息
     * @param num           数量
     */
    private void insertItemInfo(Role r, List<Items> itemsList, List<Long> idList, Items items, int num){

        // 复制一份物品信息到玩家下
        Items tmp = new Items();
        BeanUtils.copyProperties(items, tmp);

        // 设置数量信息
        tmp.setNum(num);
        itemsList.add(tmp);
        idList.add(tmp.getItemsId());

        // insert into db
        int n = roleItemsDao.insertRoleItems(r.getRoleId(), tmp.getItemsId(), tmp.getNum());
        log.info("insert into role_items, affected rows: " + n);
    }


    /**
     * 更新玩家的物品数量
     *
     * @param r             玩家信息
     * @param itemsList     物品列表
     * @param items         物品信息
     * @param num           物品数量
     */
    private void updateItemInfo(Role r, List<Items> itemsList, Items items, int num) {

        itemsList.forEach(i -> {
            if (i.getItemsId().longValue() == items.getItemsId()) {
                i.setNum(i.getNum() + num);

                // manager update db
                SaveRoleItemManager.addQueue(new SaveRoleItemRun(i, r));
            }
        });
    }


    /**
     * 删除玩家的物品记录
     *
     * @param r     玩家信息
     * @param i     物品信息
     */
    private void removeRoleItem(Role r, Items i){
        // 更新缓存信息
        List<Items> itemsList = LocalItemsMap.getRoleItemsMap().get(r.getRoleId());
        itemsList.removeIf(it -> it.getItemsId().longValue() == i.getItemsId());

        // 移除id集合
        LocalItemsMap.getRoleItemsIdMap().get(r.getRoleId()).remove(i.getItemsId());

        // 更新db
        int n = roleItemsDao.deleteItem(r.getRoleId(), i.getItemsId());
        log.info("delete from role_items, affected rows:"  + n);
    }



    /**
     * 物品状态拦截器
     *
     * @param req       请求
     * @return          物品消息返回
     */
    private MsgItemProto.ResponseItem itemStateInterceptor(MsgItemProto.RequestItem req){

        // 判断itemId param
        long itemId = req.getItemsId();
        if (itemId <= 0){
            return combineFailedMsg(ContentType.ITEM_PARAM_EMPTY);
        }


        // 判断用户是否存有该物品
        long userId = req.getUserId();
        long roleId = LocalUserMap.userRoleMap.get(userId).getRoleId();

        // 读取玩家的物品id列表，判断玩家是否拥有该物品
        List<Long> itemsIdList = LocalItemsMap.getRoleItemsIdMap().get(roleId);
        if (!itemsIdList.contains(itemId)){
            return combineFailedMsg(ContentType.ITEM_NOT_CONTAIN);
        }

        // 判断物品是否存在
        Items items = LocalItemsMap.getIdItemsMap().get(itemId);
        if (items == null){
            return combineFailedMsg(ContentType.ITEM_EMPTY);
        }
        return null;
    }


    /**
     * 组成失败消息协议
     *
     * @param msg       消息信息
     * @return          物品协议返回
     */
    private MsgItemProto.ResponseItem combineFailedMsg(String msg) {
        return MsgItemProto.ResponseItem.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(msg)
                .build();
    }
}
