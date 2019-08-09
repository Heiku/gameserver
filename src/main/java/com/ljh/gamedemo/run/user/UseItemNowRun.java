package com.ljh.gamedemo.run.user;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.entity.Items;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.local.LocalItemsMap;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.proto.protoc.MsgItemProto;
import com.ljh.gamedemo.run.record.RecoverBuff;
import com.ljh.gamedemo.service.ItemService;
import com.ljh.gamedemo.service.UserService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.ljh.gamedemo.run.user.RecoverUserRun.MAX_HP;
import static com.ljh.gamedemo.run.user.RecoverUserRun.MAX_MP;

/**
 * @Author: Heiku
 * @Date: 2019/7/22
 *
 * 玩家使用物品恢复蓝量 （立即）
 */

@Slf4j
public class UseItemNowRun implements Runnable {

    // 玩家id
    private long userId;

    // 使用的物品id
    private long itemId;

    // 玩家的回血回蓝buff
    private RecoverBuff buff;

    // 消息通道
    private Channel channel;

    private MsgItemProto.ResponseItem response;


    // 调用 itemsService
    private ItemService itemService = SpringUtil.getBean(ItemService.class);

    // 调用 userService
    private UserService userService = SpringUtil.getBean(UserService.class);


    public UseItemNowRun(long userId, long itemId, Channel channel, RecoverBuff buff){
        this.userId = userId;
        this.itemId = itemId;
        this.channel = channel;
        this.buff = buff;
    }

    @Override
    public void run() {
        Role role = LocalUserMap.userRoleMap.get(userId);
        List<Items> itemsList = LocalItemsMap.getRoleItemsMap().get(role.getRoleId());

        int hp = role.getHp();
        int mp = role.getMp();

        int hpBuf = buff.getHpBuf();
        int mpBuf = buff.getMpBuf();

        if (hpBuf > 0) {

            // 血量已满，任务退出
            if (hp >= MAX_HP) {
                response = MsgItemProto.ResponseItem.newBuilder()
                        .setResult(ResultCode.FAILED)
                        .setContent(ContentType.ITEM_USE_FAILED_FULL_BLOOD)
                        .build();
                channel.writeAndFlush(response);
                return;
            }else {
                hp += hpBuf;
                if (hp >= MAX_HP){
                    hp = MAX_HP;
                }
                role.setHp(hp);
            }
        }else {

            // 蓝量已满，退出
            if (mp >= MAX_MP){
                response = MsgItemProto.ResponseItem.newBuilder()
                        .setResult(ResultCode.FAILED)
                        .setContent(ContentType.ITEM_USE_FAILED_FULL_BLUE)
                        .build();
                channel.writeAndFlush(response);
                return;
            }else {
                mp += mpBuf;
                if (mp >= MAX_MP){
                    mp = MAX_MP;
                }
                role.setMp(mp);
            }
        }

        // 更新玩家的数据信息
        userService.updateRoleInfo(role);

        // 物品消耗，更新系统中的玩家物品信息
        itemService.updateRoleItems(role, itemId, -1);


        // 构造消息返回
        response = MsgItemProto.ResponseItem.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.ITEM_USE_SUCCESS)
                .build();
        channel.writeAndFlush(response);
    }
}

