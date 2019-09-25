package com.ljh.gamedemo.module.role.asyn.run;

import com.google.common.collect.Lists;
import com.ljh.gamedemo.module.role.service.RoleService;
import com.ljh.gamedemo.module.spell.cache.SpellCdCache;
import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.spell.bean.Spell;
import com.ljh.gamedemo.module.spell.tmp.SpellTimeStamp;
import com.ljh.gamedemo.module.user.local.LocalUserMap;
import com.ljh.gamedemo.module.base.cache.ChannelCache;
import com.ljh.gamedemo.proto.protoc.MsgAttackCreepProto;
import com.ljh.gamedemo.module.base.service.ProtoService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * 用户施放技能后，进行技能cd的判断，并且扣除蓝量
 *
 * @Author: Heiku
 * @Date: 2019/7/19
 */

@Slf4j
public class UserDeclineMpRun implements Callable<Boolean> {

    /**
     * 玩家信息
     */
    private Role role;

    /**
     * 技能信息
     */
    private Spell spell;

    /**
     * channel
     */
    private Channel channel;


    /**
     * 玩家服务
     */
    private RoleService roleService = SpringUtil.getBean(RoleService.class);

    /**
     * 协议服务
     */
    private ProtoService protoService = SpringUtil.getBean(ProtoService.class);

    /**
     * 协议返回
     */
    private MsgAttackCreepProto.ResponseAttackCreep response;



    public UserDeclineMpRun(Role role, Spell spell){
        this.role = role;
        this.spell = spell;

        this.channel = ChannelCache.getUserIdChannelMap().get(role.getUserId());
    }


    /**
     * 1.蓝量是否足够
     * 2.判断是否在cd内
     * 3.施放成功，返回结果
     *
     * @return      是否扣蓝成功
     */
    @Override
    public Boolean call() {
        // 技能需要消耗的蓝量
        int needMp = spell.getCost();

        // 判断当前蓝量是否足够
        if (role.getMp() < needMp){
            protoService.sendFailedMsg(channel, ContentType.ATTACK_SPELL_MP_NO_ENOUGH);
            return false;
        }


        // 判断是否在cd时间内
        int cd = spell.getCoolDown();
        long now = System.currentTimeMillis();

        // 初始化当前的技能施放时间
        SpellTimeStamp ts = new SpellTimeStamp();

        // 获取施放过的技能时间戳
        List<SpellTimeStamp> list = Optional.ofNullable(SpellCdCache.getCache().getIfPresent(role.getRoleId()))
                .orElse(Lists.newArrayList());

        // 判断是否存在对应的技能
        Optional<SpellTimeStamp> result = list.stream()
                .filter(s -> s.getSpellId() == spell.getSpellId())
                .findFirst();
        if (result.isPresent()){

            // 获取上次当前技能的施放时间
            ts = result.get();

            long last = ts.getTimeStamp();
            // time interval
            long t = now - last;

            // 还在cd中，通知等待时间
            if (t < cd * 1000) {
                int interval = (int) Math.floor((cd * 1000 - t) / 1000);
                protoService.sendFailedMsg(channel, ContentType.ATTACK_SPELL_CD + interval + "秒\n");
                return false;
            }
        }

        // 施放成功
        ts.setSpellId(spell.getSpellId());
        ts.setTimeStamp(now);

        // 更新技能施放时间戳
        list.removeIf(s -> s.getSpellId() == spell.getSpellId());
        list.add(ts);
        SpellCdCache.getCache().put(role.getRoleId(), list);


        // 技能施放成功，扣蓝
        role.setMp(role.getMp() - needMp);
        roleService.updateRoleInfo(role);

        // 消息返回
        protoService.sendCommonMsg(channel, ContentType.ATTACK_SPELL_SUCCESS);
        return true;
    }
}
