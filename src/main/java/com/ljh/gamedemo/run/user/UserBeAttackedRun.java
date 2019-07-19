package com.ljh.gamedemo.run.user;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.entity.Creep;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.proto.protoc.MsgAttackCreepProto;
import com.ljh.gamedemo.service.ProtoService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @Author: Heiku
 * @Date: 2019/7/19
 *
 * 野怪攻击玩家，玩家掉血
 */

@Slf4j
public class UserBeAttackedRun implements Runnable {

    // 需要野怪的伤害
    private Creep creep;

    // 这里只需要一个userId，在去map中读取最新的role
    private Long userId;

    // 用于通知掉血
    private Channel channel;

    private ProtoService protoService = new ProtoService();

    public UserBeAttackedRun(long userId, Creep creep, Channel channel){
        this.userId = userId;
        this.creep = creep;
        this.channel = channel;
    }


    @Override
    public void run() {
        int damage = creep.getDamage();

        Role role = LocalUserMap.userRoleMap.get(userId);

        log.info("攻击前的 role 属性为："  + role);
        int hp = role.getHp();

        if (hp > 0){
            hp -= damage;
        }
        role.setHp(hp);

        // 更新map
        LocalUserMap.idRoleMap.put(role.getRoleId(), role);

        log.info("攻击后 role 属性为：" + role);

        List<Role> siteRoleList = LocalUserMap.siteRolesMap.get(role.getSiteId());
        for (Role role1 : siteRoleList) {
            if (role1.getRoleId().intValue() == role.getRoleId().intValue()){
                role1.setHp(hp);
                log.info("更新siteRoleMap " + role1.getHp());
                break;
            }
        }

        MsgAttackCreepProto.ResponseAttackCreep response = MsgAttackCreepProto.ResponseAttackCreep
                .newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgAttackCreepProto.RequestType.ATTACK)
                .setContent(ContentType.ATTACK_CURRENT)
                .setRole(protoService.transToRole(role))
                .build();
        channel.writeAndFlush(response);
    }
}
