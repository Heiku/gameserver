package com.ljh.gamedemo.run.user;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.module.base.cache.ChannelCache;
import com.ljh.gamedemo.module.base.service.ProtoService;
import com.ljh.gamedemo.module.items.service.ItemService;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.role.service.RoleService;
import com.ljh.gamedemo.run.record.RecoverBuff;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import static com.ljh.gamedemo.common.RecoverType.MAX_MP;

/**
 * @Author: Heiku
 * @Date: 2019/7/22
 *
 * 玩家使用物品恢复蓝量 （立即）
 */

@Slf4j
public class UseItemNowRun implements Runnable {

    /**
     * 玩家信息
     */
    private Role role;

    /**
     * 物品id
     */
    private long itemId;

    /**
     * 玩家恢复 Buff
     */
    private RecoverBuff buff;

    /**
     * channel
     */
    private Channel channel;


    /**
     * 物品服务
     */
    private ItemService itemService = SpringUtil.getBean(ItemService.class);


    /**
     * 玩家服务
     */
    private RoleService roleService = SpringUtil.getBean(RoleService.class);


    /**
     * 协议服务
     */
    private ProtoService protoService = SpringUtil.getBean(ProtoService.class);



    public UseItemNowRun(Role role, long itemId, RecoverBuff buff){
        this.role = role;
        this.itemId = itemId;
        this.buff = buff;

        this.channel = ChannelCache.getUserIdChannelMap().get(role.getRoleId());
    }

    @Override
    public void run() {
        // 获取血量信息
        int hp = role.getHp();
        int mp = role.getMp();

        // 获取恢复数据
        int hpBuf = buff.getHpBuf();
        int mpBuf = buff.getMpBuf();


        // 判断时恢复蓝量还是恢复血量
        if (hpBuf > 0) {
            int maxHp = role.getMaxHp();

            // 血量已满
            if (hp >= maxHp) {
                protoService.sendFailedMsg(channel, ContentType.ITEM_USE_FAILED_FULL_BLOOD);
                return;
            }
            hp += hpBuf;
            hp = hp > maxHp ? maxHp : hp;

            // 更新血量属性
            role.setHp(hp);
        }else {

            // 蓝量已满
            if (mp >= MAX_MP){
                protoService.sendFailedMsg(channel, ContentType.ITEM_USE_FAILED_FULL_BLUE);
                return;
            }
            mp += mpBuf;
            mp = mp > MAX_MP ? MAX_MP : mp;

            // 更新蓝量属性
            role.setMp(mp);
        }

        // 物品消耗，更新系统中的玩家物品信息
        itemService.updateRoleItems(role, itemId, -1);

        // 更新玩家的数据信息
        roleService.updateRoleInfo(role);

        // 消息返回
        protoService.sendCommonMsg(channel, ContentType.ITEM_USE_SUCCESS);
    }
}

