package com.ljh.gamedemo.service;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.dao.RoleItemsDao;
import com.ljh.gamedemo.entity.Items;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.local.LocalItemsMap;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.proto.protoc.MsgItemProto;
import com.ljh.gamedemo.run.UserExecutorManager;
import com.ljh.gamedemo.run.record.FutureMap;
import com.ljh.gamedemo.run.record.RecoverBuff;
import com.ljh.gamedemo.run.user.RecoverUserRun;
import com.ljh.gamedemo.run.user.UseItemNowRun;
import com.ljh.gamedemo.run.user.UseItemScheduledRun;
import io.netty.channel.Channel;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.ljh.gamedemo.common.ItemsType.*;

/**
 * @Author: Heiku
 * @Date: 2019/7/16
 */

@Slf4j
@Service
public class ItemService {

    private static final int BLOOD_MAX_VALUE = 1000;

    private static final int BLUE_MAX_VALUE = 300;


    @Autowired
    private RoleItemsDao roleItemsDao;

    @Autowired
    private ProtoService protoService;

    private MsgItemProto.ResponseItem response;


    /**
     * 获取当前用户角色的所有背包物品
     *
     * @param request
     * @return
     */
    public MsgItemProto.ResponseItem getAll(MsgItemProto.RequestItem request){

        // 用户状态判断
        response = userStateInterceptor(request);
        if (response != null){
            return response;
        }

        long userId = request.getUserId();
        long roleId = LocalUserMap.userRoleMap.get(userId).getRoleId();

        // 获取当前用户的背包物品
        List<Items> items = LocalItemsMap.getRoleItemsMap().get(roleId);
       // System.out.println(items);

        // 空背包，构造response
        if (items == null || items.isEmpty()){
            return MsgItemProto.ResponseItem.newBuilder()
                    .setResult(ResultCode.SUCCESS)
                    .setContent(ContentType.ITEM_EMPTY)
                    .setType(MsgItemProto.RequestType.ALL)
                    .build();
        }

        // 非空背包
        return MsgItemProto.ResponseItem.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.FIND_SUCCESS)
                .setType(MsgItemProto.RequestType.ALL)
                .addAllItem(protoService.transToItemsList(items))
                .build();
    }


    /**
     * 物品使用
     *
     * @param requestItem
     * @return
     */
    public MsgItemProto.ResponseItem useItem(MsgItemProto.RequestItem requestItem, Channel channel){
        // 用户状态判断
        response = userStateInterceptor(requestItem);
        if (response != null){
            return response;
        }

        // 物品状态判断
        response = itemStateInterceptor(requestItem);
        if (response != null){
            return response;
        }


        long userId = requestItem.getUserId();
        long roleId = LocalUserMap.userRoleMap.get(userId).getRoleId();
        long itemsId = requestItem.getItemsId();

        // 获取物品
        Items items = LocalItemsMap.getIdItemsMap().get(itemsId);

        // 使用物品
        doUseItem(userId, items, channel);

        return null;
    }


    /**
     * 根据物品的类别 进行对应的使用操作
     *
     * @param userId
     * @param items
     * @return
     */
    private void doUseItem(long userId, Items items, Channel channel){
        int type = items.getType();
        switch (type){
            case BLOOD:
                recoverBlood(userId, items, channel);
                break;
            case BLUE:
                recoverBlue(userId, items, channel);
                break;
        }
    }


    /**
     * 具体的回血操作
     *
     * @param userId
     * @param items
     * @return
     */
    private void recoverBlood(long userId, Items items, Channel channel){

        RecoverBuff buff = new RecoverBuff();
        buff.setHpBuf(items.getUp());
        buff.setMpBuf(0);

        // 直接恢复
        if (items.getSec() == 0){

            // 执行立即恢复的任务
            UserExecutorManager.addUserTask(userId, new UseItemNowRun(userId, items.getItemsId(), channel, buff));
        }else {
            buff.setHpBuf(items.getUp() / items.getSec());
            UseItemScheduledRun r = new UseItemScheduledRun(userId, items, channel, buff);

            // 缓慢恢复，定期增加用户状态值
            ScheduledFuture future = UserExecutorManager.getUserExecutor(userId).scheduleAtFixedRate(r, 0, 1, TimeUnit.SECONDS);
            FutureMap.futureMap.put(r.hashCode(), future);
        }
    }


    /**
     * 具体的回蓝操作
     *
     * @param userId
     * @param items
     * @return
     */
    private void recoverBlue(long userId, Items items, Channel channel){

        RecoverBuff buff = new RecoverBuff();
        buff.setHpBuf(0);
        buff.setMpBuf(items.getUp());
        // 直接恢复
        if (items.getSec() == 0){

            // 执行立即恢复的任务
            UserExecutorManager.addUserTask(userId, new UseItemNowRun(userId, items.getItemsId(), channel, buff));
        }else {
            buff.setMpBuf(items.getUp() / items.getSec() * 2);
            UseItemScheduledRun r = new UseItemScheduledRun(userId, items,  channel, buff);

            // 缓慢恢复，定期增加用户状态值
            ScheduledFuture future = UserExecutorManager.getUserExecutor(userId).scheduleAtFixedRate(r, 0, 2, TimeUnit.SECONDS);
            FutureMap.futureMap.put(r.hashCode(), future);
        }
    }


    /**
     * 更新缓存信息，返回消息
     *
     * @param role
     * @param items
     * @return
     */
    private MsgItemProto.ResponseItem updateCacheRepsonse(Role role, Items items){
        // 更新items的数量
        items.setNum(items.getNum() - 1);
        LocalItemsMap.getIdItemsMap().put(items.getItemsId(), items);

        // 数据库更新
        roleItemsDao.updateItem(items.getNum(), role.getRoleId(), items.getItemsId());


        // TODO: 暂时不更新 siteRolesMap
        // 直接返回消息
        return MsgItemProto.ResponseItem.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.ITEM_USE_SUCCESS)
                .setType(MsgItemProto.RequestType.USE)
                .setRole(protoService.transToRole(role))
                .build();
    }



    /**
     * 用户状态拦截器，检验参数
     *
     * @param requestItem
     * @return
     */
    private MsgItemProto.ResponseItem userStateInterceptor(MsgItemProto.RequestItem requestItem){
        // 用户id标识判断
        long userId = requestItem.getUserId();
        if (userId <= 0){
            return MsgItemProto.ResponseItem.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.USER_TOKEN_DATA_EMPTY)
                    .build();
        }
        // 找不到对应的角色信息
        Role role = LocalUserMap.userRoleMap.get(userId);
        if (role == null){
            return MsgItemProto.ResponseItem.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.ROLE_EMPTY)
                    .build();
        }
        return null;
    }

    /**
     * 物品状态拦截器
     *
     * @param requestItem
     * @return
     */
    private MsgItemProto.ResponseItem itemStateInterceptor(MsgItemProto.RequestItem requestItem){

        // 判断itemId param
        long itemId = requestItem.getItemsId();
        if (itemId <= 0){
            return MsgItemProto.ResponseItem.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.ITEM_PARAM_EMPTY)
                    .build();
        }


        // 判断用户是否存有该物品
        Items sign = null;
        long userId = requestItem.getUserId();
        long roleId = LocalUserMap.userRoleMap.get(userId).getRoleId();
        List<Items> itemsList = LocalItemsMap.getRoleItemsMap().get(roleId);
        for (Items i : itemsList){
            if (i.getItemsId() == itemId){
                sign = i;
            }
        }
        if (sign == null){
            return MsgItemProto.ResponseItem.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.ITEM_NOT_CONTAIN)
                    .build();
        }


        // 判断物品是否存在
        Items items = LocalItemsMap.getIdItemsMap().get(itemId);
        if (items == null){
            return MsgItemProto.ResponseItem.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.ITEM_EMPTY)
                    .build();
        }
        return null;
    }
}
