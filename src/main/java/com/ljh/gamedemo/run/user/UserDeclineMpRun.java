package com.ljh.gamedemo.run.user;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.entity.Spell;
import com.ljh.gamedemo.entity.tmp.SpellTimeStamp;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.local.cache.SpellCdCache;
import com.ljh.gamedemo.local.cache.ChannelCache;
import com.ljh.gamedemo.proto.protoc.MsgAttackCreepProto;
import com.ljh.gamedemo.service.ProtoService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
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
     * 释放的技能
     */
    private Spell spell;

    /**
     * 玩家id
     */
    private long roleId;

    /**
     * channel
     */
    private Channel channel;

    /**
     * 协议服务
     */
    private ProtoService protoService = SpringUtil.getBean(ProtoService.class);

    /**
     * 协议返回
     */
    private MsgAttackCreepProto.ResponseAttackCreep response;

    public UserDeclineMpRun(long roleId, Spell spell){
        this.roleId = roleId;
        this.spell = spell;
    }

    @Override
    public Boolean call() throws Exception {
        // 技能需要消耗的蓝量
        int needMp = spell.getCost();

        // 当前的玩家信息
        Role role = LocalUserMap.idRoleMap.get(roleId);
        channel = ChannelCache.getUserIdChannelMap().get(role.getUserId());

        // 1. 如果 mp 值不足以施放，直接返回
        if (role.getMp() < needMp){
            responseMpFailed(ContentType.ATTACK_SPELL_MP_NO_ENOUGH);
            return false;
        }

        // 2.施放技能前，进行一次时间戳判断，判断是否在cd时间内
        int cd = spell.getCoolDown();
        long now = System.currentTimeMillis();

        log.info("SpellCdCache 更新前的技能时间为：" + SpellCdCache.getCache().getIfPresent(roleId));

        List<SpellTimeStamp> list;
        SpellTimeStamp ts = new SpellTimeStamp();
        list = SpellCdCache.getCache().getIfPresent(roleId);
        if (list != null && !list.isEmpty()) {
            for (SpellTimeStamp e : list) {
                if (e.getSpellId() == spell.getSpellId()) {
                    // 标记上次的当前技能的施放记录
                    ts = e;
                    break;
                }
            }
        }
        // 获取上一次施放的时间 last
        long last = ts.getTimeStamp();
        if (last > 0) {
            // time interval
            long t = now - last;

            // 还在cd中，通知用户等待时间点
            if (t < cd * 1000) {
                int interval = (int) Math.floor((cd * 1000 - t) / 1000);

                responseMpFailed(ContentType.ATTACK_SPELL_CD + interval + "秒\n");
                //CountDownLatchUtil.abort();
                return false;
            }
        }

        // 3. 技能释放成功，更新用户蓝量数据，及记录本次的技能施放时间
        ts.setSpellId(spell.getSpellId());
        ts.setTimeStamp(now);

        if (list == null){
            list = new ArrayList<>();
            list.add(ts);
        }else {
            for (SpellTimeStamp i : list) {
                if (i.getSpellId() == ts.getSpellId()) {
                    i.setTimeStamp(ts.getTimeStamp());
                    break;
                }
            }
        }

        // 更新缓存
        SpellCdCache.getCache().put(roleId, list);
        log.info("SpellCdCache 更新后的技能时间为：" + SpellCdCache.getCache().getIfPresent(roleId));

        // 技能施放成功，扣蓝
        log.info("技能施放前的蓝为：" + role.getMp());
        role.setMp(role.getMp() - needMp);

        // 更新map
        LocalUserMap.idRoleMap.put(roleId, role);
        log.info("技能施放后的蓝为：" + role.getMp());

        LocalUserMap.userRoleMap.put(role.getUserId(), role);

        // 更新siteRolesMap
        List<Role> siteRoleList = LocalUserMap.siteRolesMap.get(role.getSiteId());
        for (Role role1 : siteRoleList) {
            if (role1.getRoleId().intValue() == role.getRoleId().intValue()){
                role1.setMp(role.getMp());
                log.info("更新siteRoleMap 后的蓝为：" + role1.getMp());
                break;
            }
        }

        // 4. 构造返回
        MsgAttackCreepProto.ResponseAttackCreep response = MsgAttackCreepProto.ResponseAttackCreep
                .newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgAttackCreepProto.RequestType.SPELL)
                .setContent(ContentType.ATTACK_SPELL_SUCCESS)
                .setRole(protoService.transToRole(role))
                .build();
        channel.writeAndFlush(response);

        return true;
    }

    /**
     * 返回扣除蓝量失败的消息
     *
     * @param content
     */
    private void responseMpFailed(String content){
        response = MsgAttackCreepProto.ResponseAttackCreep.newBuilder()
                .setResult(ResultCode.FAILED)
                .setType(MsgAttackCreepProto.RequestType.SPELL)
                .setContent(content)
                .build();
        channel.writeAndFlush(response);
    }


}
