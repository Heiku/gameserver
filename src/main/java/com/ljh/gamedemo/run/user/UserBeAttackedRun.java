package com.ljh.gamedemo.run.user;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.entity.Creep;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.entity.dto.RoleBuff;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.local.cache.RoleBuffCache;
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

    private Integer blood;

    private ProtoService protoService = new ProtoService();

    public UserBeAttackedRun(long userId, Creep creep, Channel channel){
        this.userId = userId;
        this.creep = creep;
        this.channel = channel;
    }


    /**
     * 用户扣血：
     *      优先扣护盾
     *          判断是否有护盾技能
     *          判断护盾是否再有效期内
     *          判断护盾值是否 > 0 ?
     *
     *          扣护盾值，如果护盾碎了，额外扣血
     *      再扣血
     */
    @Override
    public void run() {
        int damage = creep.getDamage();

        Role role = LocalUserMap.userRoleMap.get(userId);
        int hp = role.getHp();
        log.info("攻击前的 role 属性为："  + role);


        // 掉血优先扣护盾值，判断护盾的值，及护盾是否有效
        List<RoleBuff> buffList = RoleBuffCache.getCache().getIfPresent(role.getRoleId());
        if (buffList != null && !buffList.isEmpty()){
            RoleBuff buff = new RoleBuff();
            for (RoleBuff b : buffList) {
                if (b.getType() == 1){
                    buff = b;
                }
            }

            // 获取当前时间点
            long nowTs = System.currentTimeMillis();
            long td = nowTs - buff.getCreateTime();
            long cd = buff.getSec() * 1000;

            // 护盾时间的有效期判断
            if (td < cd){
                int shield = buff.getShield();
                log.info("存在护盾buff，护盾值为：" + shield);

                // 护盾值有效
                if (shield >= 0) {
                    shield -= damage;
                    log.info("护盾收到伤害：当前护盾值为: " + shield);

                    // 盾碎了
                    if (shield <= 0) {
                        // 获取扣血值
                        blood = Math.abs(shield - damage);
                        log.info("护盾碎了，将收到额外的伤害为：" + blood);

                        // 移除 buff
                        buffList.remove(buff);
                        log.info("移除护盾buff后，当前的玩家buff有：" + buffList);

                        // 同时玩家扣血
                        if (hp > 0) {
                            hp -= blood;
                        }
                    } else {
                        // 更新shield
                        buff.setShield(shield);
                    }
                }
            }else {
                // 护盾技能过期，移除护盾技能
                hp -= damage;
                buffList.remove(buff);
            }
        } else {
            // 没有buff，也直接扣血
            hp -= damage;
        }
        role.setHp(hp);
        log.info("扣血完，当前玩家的血量为：" + role.getHp());

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
                .setCreep(protoService.transToCreep(creep))
                .build();
        channel.writeAndFlush(response);
    }
}
