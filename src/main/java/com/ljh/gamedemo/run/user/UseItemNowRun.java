package com.ljh.gamedemo.run.user;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.dao.RoleItemsDao;
import com.ljh.gamedemo.entity.Items;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.local.LocalItemsMap;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.proto.protoc.MsgItemProto;
import com.ljh.gamedemo.run.manager.SaveRoleItemManager;
import com.ljh.gamedemo.run.db.SaveRoleItemRun;
import com.ljh.gamedemo.run.record.RecoverBuff;
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

    private long userId;

    private long itemId;

    private RecoverBuff buff;

    private Channel channel;

    private MsgItemProto.ResponseItem response;

    private RoleItemsDao itemsDao = SpringUtil.getBean(RoleItemsDao.class);

    private static UseItemNowRun run = new UseItemNowRun();

    public static UseItemNowRun getInstance(){
        return run;
    }

    public UseItemNowRun(){

    }

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

        // 更新
        LocalUserMap.idRoleMap.put(role.getRoleId(), role);

        List<Role> siteRoleList = LocalUserMap.siteRolesMap.get(role.getSiteId());
        for (Role role1 : siteRoleList) {
            if (role1.getRoleId().intValue() == role.getRoleId().intValue()){
                role1.setHp(role.getHp());
                role1.setMp(role.getMp());
                break;
            }
        }

        // 更新用户背包信息
        updateRoleItem(itemsList, role);

        response = MsgItemProto.ResponseItem.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.ITEM_USE_SUCCESS)
                .build();
        channel.writeAndFlush(response);
    }


    public void updateRoleItem(List<Items> itemsList, Role role){
        Items items = new Items();

        // 缓存更新
        log.info("更新RoleItemList前，数量为：" + itemsList);
        for (Items i : itemsList) {
            if (i.getItemsId() == itemId){
                items = i;
                i.setNum(i.getNum() - 1);

                if (i.getNum() == 0){
                    // 移除记录
                    removeRoleItem(i, role);
                }
            }
        }
        LocalItemsMap.getRoleItemsMap().put(role.getRoleId(), itemsList);
        log.info("更新RoleItemList后，数量为：" + itemsList);


        // 同步更新数据库
        SaveRoleItemRun run = new SaveRoleItemRun(items, role);
        SaveRoleItemManager.addQueue(run);
    }


    private void removeRoleItem(Items items, Role role){
        // 更新缓存
        List<Items> itemsList = LocalItemsMap.getRoleItemsMap().get(role.getRoleId());
        itemsList.forEach(i -> {
            if (i.getItemsId().intValue() == items.getItemsId()){
                itemsList.remove(i);
            }
        });

        // 更新db
        itemsDao.deleteItem(role.getRoleId(), items.getItemsId());
    }
}

