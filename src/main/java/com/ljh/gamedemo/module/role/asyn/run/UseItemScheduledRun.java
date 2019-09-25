package com.ljh.gamedemo.module.role.asyn.run;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.module.base.cache.ChannelCache;
import com.ljh.gamedemo.module.base.service.ProtoService;
import com.ljh.gamedemo.module.items.bean.Items;
import com.ljh.gamedemo.module.items.service.ItemService;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.role.service.RoleService;
import com.ljh.gamedemo.module.base.asyn.run.FutureMap;
import com.ljh.gamedemo.module.base.asyn.run.RecoverBuff;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import static com.ljh.gamedemo.common.RecoverType.MAX_MP;

/**
 * @Author: Heiku
 * @Date: 2019/7/22
 *
 * 用户使用物品缓慢恢复血量或是蓝量
 */

@Slf4j
public class UseItemScheduledRun implements Runnable {

    /**
     * 玩家信息
     */
    private Role role;

    /**
     * 恢复buff
     */
    private RecoverBuff buff;

    /**
     * 物品id
     */
    private long itemId;

    /**
     * 恢复的最大值
     */
    private int allUp;

    /**
     * 累加恢复值
     */
    private int up;

    /**
     * channel
     */
    private Channel channel;

    /**
     * 是否已经更新物品信息
     */
    private boolean decline = true;


    /**
     * 玩家服务
     */
    private RoleService roleService = SpringUtil.getBean(RoleService.class);


    /**
     * 物品服务
     */
    private ItemService itemService = SpringUtil.getBean(ItemService.class);


    /**
     * 协议服务
     */
    private ProtoService protoService = SpringUtil.getBean(ProtoService.class);



    public UseItemScheduledRun(Role role, Items items, RecoverBuff buff){
        this.role = role;
        this.buff = buff;

        this.itemId = items.getItemsId();
        this.allUp = items.getUp();
        this.channel = ChannelCache.getUserIdChannelMap().get(role.getUserId());
    }

    @Override
    public void run() {
        // 获取恢复的基本信息
        int hpBuf = buff.getHpBuf();
        int mpBuf = buff.getMpBuf();

        // 当前玩家属性值
        int hp = role.getHp();
        int mp = role.getMp();

        // 判断是否正常回血
        if (hpBuf > 0){
            int maxHp = role.getMaxHp();

            if (hp >= maxHp){
                // 消息返回
                protoService.sendFailedMsg(channel, ContentType.ITEM_USE_FAILED_FULL_BLOOD);

                cancelRecoverTask();
                return;

            }else {
                // 达到最大恢复生命值
                hp += hpBuf;
                if (hp >= maxHp){
                    hp = maxHp;
                    cancelRecoverTask();
                }

                up += hpBuf;
                if (up >= allUp){
                    // 已经达到物品的最大恢复值，移除任务
                    cancelRecoverTask();
                }

                role.setHp(hp);
            }
        }else {
            if (mp >= MAX_MP){

                // 满蓝无法恢复
                protoService.sendFailedMsg(channel, ContentType.ITEM_USE_FAILED_FULL_BLUE);
                cancelRecoverTask();
                return;

            }else {

                mp += mpBuf;
                if (mp >= MAX_MP){
                    mp = MAX_MP;
                    cancelRecoverTask();
                }

                up += mpBuf;
                if (up >= allUp){
                    cancelRecoverTask();
                }

                role.setMp(mp);
            }
        }
        // 更新缓存
        roleService.updateRoleInfo(role);

        if (decline) {
            itemService.updateRoleItems(role, itemId, -1);
            decline = false;

            protoService.sendCommonMsg(channel, ContentType.ITEM_USE_SUCCESS);
        }
    }


    /**
     * 取消当前的恢复任务
     */
    private void cancelRecoverTask(){
        FutureMap.futureMap.get(this.hashCode()).cancel(true);
    }
}
