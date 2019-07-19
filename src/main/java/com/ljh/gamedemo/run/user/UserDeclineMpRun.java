package com.ljh.gamedemo.run.user;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.entity.Spell;
import com.ljh.gamedemo.entity.SpellTimeStamp;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.local.cache.SpellCdCache;
import com.ljh.gamedemo.proto.protoc.MsgAttackCreepProto;
import com.ljh.gamedemo.run.util.CountDownLatchUtil;
import com.ljh.gamedemo.service.ProtoService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Heiku
 * @Date: 2019/7/19
 *
 * 用户施放技能后，进行技能cd的判断，并且扣除蓝量
 */

@Slf4j
public class UserDeclineMpRun implements Runnable{

    // 施放的技能
    private Spell spell;

    // 玩家
    private long roleId;

    private Channel channel;

    private ProtoService protoService = ProtoService.getInstance();

    private MsgAttackCreepProto.ResponseAttackCreep response;

    public UserDeclineMpRun(long roleId, Spell spell, Channel channel){
        this.roleId = roleId;
        this.spell = spell;
        this.channel = channel;
    }

    @Override
    public void run() {
        // 技能需要消耗的蓝量
        int needMp = spell.getCost();

        // 当前的玩家信息
        Role role = LocalUserMap.idRoleMap.get(roleId);

        // 1. 如果 mp 值不足以施放，直接返回
        if (role.getMp() < needMp){

            response = MsgAttackCreepProto.ResponseAttackCreep.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setType(MsgAttackCreepProto.RequestType.SPELL)
                    .setContent(ContentType.ATTACK_SPELL_MP_NO_ENOUGH)
                    .build();
            channel.writeAndFlush(response);
            return;
        }

        // 2.施放技能前，进行一次时间戳判断，判断是否在cd时间内
        int cd = spell.getCoolDown();
        long last = 0L;
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
                }
            }
        }
        // 获取上一次施放的时间 last
        last = ts.getTimeStamp();

        if (last > 0) {
            // time interval
            long t = now - last;

            // 还在cd中，通知用户等待时间点
            if (t < cd * 1000) {
                int interval = (int) Math.floor((cd * 1000 - t) / 1000);

                response = MsgAttackCreepProto.ResponseAttackCreep.newBuilder()
                        .setResult(ResultCode.FAILED)
                        .setType(MsgAttackCreepProto.RequestType.SPELL)
                        .setContent(ContentType.ATTACK_SPELL_CD + interval + "秒\n")
                        .build();

                channel.writeAndFlush(response);
                return;
            }
        }

        // 3. 技能释放成功，更新用户蓝量数据，及记录本次的技能施放时间
        ts.setSpellId(spell.getSpellId());
        ts.setTimeStamp(now);

        if (list == null || list.isEmpty()){
            list = new ArrayList<>();
            list.add(ts);
        }else {
            for (SpellTimeStamp i : list) {
                if (i.getSpellId() == ts.getSpellId()) {
                    i.setTimeStamp(ts.getTimeStamp());
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
        log.info("技能施放前的蓝为：" + role.getMp());

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

        CountDownLatchUtil.abort();
        //CountDownLatchUtil.countDownLatch.countDown();
    }

}
