package com.ljh.gamedemo.run.user;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.dao.RoleItemsDao;
import com.ljh.gamedemo.entity.Items;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.local.LocalItemsMap;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.proto.protoc.MsgItemProto;
import com.ljh.gamedemo.run.record.FutureMap;
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
 * 用户使用物品缓慢恢复血量或是蓝量
 */

@Slf4j
public class UseItemScheduledRun implements Runnable {

    // 玩家id
    private long userId;

    // 物品id
    private long itemId;

    // 恢复的最大值
    private int allUp;

    // 累加恢复值
    private int up;

    // 恢复buff
    private RecoverBuff buff;

    // 通信channel
    private Channel channel;

    // 是否已经更新物品信息
    private boolean decline = true;


    private MsgItemProto.ResponseItem response;

    // 调用userService
    private UserService userService = SpringUtil.getBean(UserService.class);

    // 调用itemsService
    private ItemService itemService = SpringUtil.getBean(ItemService.class);


    public UseItemScheduledRun(long userId, Items items,  Channel channel, RecoverBuff buff){
        this.userId = userId;
        this.itemId = items.getItemsId();
        this.allUp = items.getUp();
        this.channel = channel;
        this.buff = buff;
    }

    @Override
    public void run() {
        int hpBuf = buff.getHpBuf();
        int mpBuf = buff.getMpBuf();

        Role role = LocalUserMap.userRoleMap.get(userId);
        int hp = role.getHp();
        int mp = role.getMp();

        if (hpBuf > 0){
            if (hp == MAX_HP){
                response = MsgItemProto.ResponseItem.newBuilder()
                        .setResult(ResultCode.FAILED)
                        .setContent(ContentType.ITEM_USE_FAILED_FULL_BLOOD)
                        .build();

                // 血量已满，不需要执行任务，移除任务队列
                FutureMap.futureMap.get(this.hashCode()).cancel(true);

                channel.writeAndFlush(response);
                return;
            }else {
                // 用户的物品数量只减少一次
                if (decline) {
                    itemService.updateRoleItems(role, itemId, -1);
                    decline = false;
                }

                hp += hpBuf;
                log.info("用户使用物品，缓慢恢复血量：" + hpBuf);

                if (hp >= MAX_HP){
                    hp = MAX_HP;

                    // 血量已满，移除该任务
                    FutureMap.futureMap.get(this.hashCode()).cancel(true);
                    log.info("用户使用物品，血量已满，移除物品Buff");
                }


                up += hpBuf;
                if (up >= allUp){
                    // 已经达到物品的最大恢复值，移除任务
                    FutureMap.futureMap.get(this.hashCode()).cancel(true);
                    log.info("已经达到物品的最大恢复值，移除恢复任务");
                }


                role.setHp(hp);
            }
        }else {
            if (mp == MAX_MP){
                response = MsgItemProto.ResponseItem.newBuilder()
                        .setResult(ResultCode.FAILED)
                        .setContent(ContentType.ITEM_USE_FAILED_FULL_BLUE)
                        .build();

                // 血量已满，不需要执行任务，移除任务队列
                FutureMap.futureMap.get(this.hashCode()).cancel(true);

                channel.writeAndFlush(response);
            }else {
                // 用户的物品数量只减少一次
                if (decline) {
                    itemService.updateRoleItems(role, itemId, -1);
                    decline = false;
                }

                mp += mpBuf;
                log.info("用户使用物品，缓慢恢复蓝量：" + mpBuf);

                if (mp >= MAX_MP){
                    mp = MAX_MP;

                    // 蓝量已满，移除任务
                    FutureMap.futureMap.get(this.hashCode()).cancel(true);
                    log.info("用户使用物品，蓝量已满，移除物品Buff");
                }

                up += mpBuf;
                if (up >= allUp){
                    // 已经达到物品的最大恢复值，移除任务
                    log.info("当前任务为："  + this);
                    FutureMap.futureMap.get(this.hashCode()).cancel(true);
                    log.info("已经达到物品的最大恢复值，移除恢复任务");
                }

                role.setMp(mp);
            }
        }

        // 更新缓存
        userService.updateRoleInfo(role);
        log.info("用户使用物品，状态缓慢恢复，已成功更新用户缓存");
    }

}
