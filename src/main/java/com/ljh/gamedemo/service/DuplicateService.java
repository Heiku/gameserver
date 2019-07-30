package com.ljh.gamedemo.service;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.entity.Boss;
import com.ljh.gamedemo.entity.BossSpell;
import com.ljh.gamedemo.entity.Duplicate;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.local.LocalAttackCreepMap;
import com.ljh.gamedemo.local.LocalDuplicateMap;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.proto.protoc.DuplicateProto;
import com.ljh.gamedemo.proto.protoc.MsgDuplicateProto;
import com.ljh.gamedemo.run.CustomExecutor;
import com.ljh.gamedemo.run.DuplicateManager;
import com.ljh.gamedemo.run.UserExecutorManager;
import com.ljh.gamedemo.run.record.FutureMap;
import com.ljh.gamedemo.run.user.UserBeAttackedRun;
import io.netty.channel.Channel;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Heiku
 * @Date: 2019/7/30
 *
 * 副本相关操作
 */

@Service
@Slf4j
public class DuplicateService {

    private ProtoService protoService = ProtoService.getInstance();

    private MsgDuplicateProto.ResponseDuplicate response;


    /**
     * 获取当前的所有副本信息
     *
     * @param request
     * @return
     */
    public MsgDuplicateProto.ResponseDuplicate getDuplicate(MsgDuplicateProto.RequestDuplicate request){
        // 玩家角色状态判断
        response = userStateInterceptor(request);
        if (response != null){
            return response;
        }

        // 获取当前的副本列表
        List<Duplicate> duplicates = new ArrayList<>();
        LocalDuplicateMap.getDuplicateMap().forEach((k, v)  -> duplicates.add(v));

        // 返回消息
        List<DuplicateProto.Duplicate> resList = protoService.transToDuplicateList(duplicates);

        return MsgDuplicateProto.ResponseDuplicate.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgDuplicateProto.RequestType.DUPLICATE)
                .setContent(ContentType.DUPLICATE_ALL)
                .addAllDuplicate(resList)
                .build();
    }


    /**
     * 用户进入副本内
     *
     * 1.玩家的角色状态判断
     * 2.构建一个临时的副本
     * 3.野怪开始自动攻击玩家
     *
     *
     * @param request
     * @return
     */
    public MsgDuplicateProto.ResponseDuplicate enterDuplicate(MsgDuplicateProto.RequestDuplicate request, Channel channel) {
        // 玩家角色状态判断
        response = userStateInterceptor(request);
        if (response != null){
            return response;
        }

        // 副本信息判断
        response = duplicateStateInterceptor(request);
        if (response != null){
            return response;
        }

        response = MsgDuplicateProto.ResponseDuplicate.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.DUPLICATE_ENTER_SUCCESS)
                .setType(MsgDuplicateProto.RequestType.ENTER)
                .build();
        channel.writeAndFlush(response);

        // 创建临时副本信息, 便于后期进行对象释放
        Duplicate tmpDup = createTmpDuplicate(request);

        // 信息初始化
        Role role = LocalUserMap.userRoleMap.get(request.getUserId());
        // 绑定线程池，同时绑定副本对象
        DuplicateManager.bindDupExecutor(role.getRoleId());
        LocalAttackCreepMap.getCurDupMap().put(role.getRoleId(), tmpDup);

        // 当前副本线程池
        CustomExecutor executor = DuplicateManager.getExecutor(role.getRoleId());
        CustomExecutor userExecutor = UserExecutorManager.getUserExecutor(request.getUserId());

        // 取出第一只boss
        Boss boss = tmpDup.getBosses().get(0);
        List<BossSpell> spells = boss.getSpellList();
        log.info("boss: " + boss.getName() + " 的技能列表为：" + spells);
        BossSpell aSpell = boss.getSpellList().get(0);

        // Boss开始主动攻击玩家
        // 根据Boss的普攻攻击间隔，玩家自动扣血
        UserBeAttackedRun task = new UserBeAttackedRun(request.getUserId(), aSpell.getDamage(), channel);
        ScheduledFuture future = userExecutor.scheduleAtFixedRate(task, 0, aSpell.getCd(), TimeUnit.SECONDS);
        FutureMap.futureMap.put(task.hashCode(), future);

        return null;
    }


    /**
     * 用于创建临时副本信息，便于后续回收
     *
     * @param request
     * @return
     */
    private Duplicate createTmpDuplicate(MsgDuplicateProto.RequestDuplicate request) {
        long dupId = request.getDupId();

        // 读取本地的基本副本信息
        Duplicate data = LocalDuplicateMap.getDuplicateMap().get(dupId);
        Duplicate tmp = new Duplicate();

        // 加载到新创建的副本对象中
        BeanUtils.copyProperties(data, tmp);

        // boss开始攻击

        return tmp;
    }


    /**
     * 副本信息拦截器
     *
     * @param request
     * @return
     */
    private MsgDuplicateProto.ResponseDuplicate duplicateStateInterceptor(MsgDuplicateProto.RequestDuplicate request) {
        MsgDuplicateProto.ResponseDuplicate response = MsgDuplicateProto.ResponseDuplicate.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(ContentType.DUPLICATE_EMPTY)
                .build();

        long dupId = request.getDupId();
        if (dupId <= 0){
           return response;
        }

        Duplicate d = LocalDuplicateMap.getDuplicateMap().get(dupId);
        return d == null ? response : null;
    }


    /**
     * 用户状态拦截器，检验参数
     *
     * @param requestEquip
     * @return
     */
    private MsgDuplicateProto.ResponseDuplicate userStateInterceptor(MsgDuplicateProto.RequestDuplicate requestEquip){
        // 用户id标识判断
        long userId = requestEquip.getUserId();
        if (userId <= 0){
            return MsgDuplicateProto.ResponseDuplicate.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.USER_TOKEN_DATA_EMPTY)
                    .build();
        }
        // 找不到对应的角色信息
        Role role = LocalUserMap.userRoleMap.get(userId);
        if (role == null){
            return MsgDuplicateProto.ResponseDuplicate.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.ROLE_EMPTY)
                    .build();
        }
        return null;
    }



}
