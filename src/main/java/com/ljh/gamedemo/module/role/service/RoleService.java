package com.ljh.gamedemo.module.role.service;

import com.google.common.collect.Lists;
import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.module.chat.dao.ChatRecordDao;
import com.ljh.gamedemo.module.group.service.GroupService;
import com.ljh.gamedemo.module.role.bean.RoleBuff;
import com.ljh.gamedemo.module.creep.local.LocalAttackCreepMap;
import com.ljh.gamedemo.module.role.bean.RoleState;
import com.ljh.gamedemo.module.role.cache.RoleStateCache;
import com.ljh.gamedemo.module.role.local.LocalRoleInitMap;
import com.ljh.gamedemo.module.user.local.LocalUserMap;
import com.ljh.gamedemo.module.base.cache.ChannelCache;
import com.ljh.gamedemo.module.role.cache.RoleBuffCache;
import com.ljh.gamedemo.module.role.dao.UserRoleDao;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.role.bean.RoleInit;
import com.ljh.gamedemo.module.user.service.UserService;
import com.ljh.gamedemo.proto.protoc.MsgRoleProto;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.run.manager.UserExecutorManager;
import com.ljh.gamedemo.run.db.UpdateRoleInfoRun;
import com.ljh.gamedemo.module.duplicate.service.DuplicateService;
import com.ljh.gamedemo.module.base.service.ProtoService;
import com.ljh.gamedemo.run.record.FutureMap;
import com.ljh.gamedemo.run.user.RecoverUserRun;
import io.netty.channel.Channel;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 具体的玩家职业操作
 *
 * @Author: Heiku
 * @Date: 2019/8/19
 */
@Slf4j
@Service
public class RoleService {

    /**
     * roleDao
     */
    @Autowired
    private UserRoleDao roleDao;

    /**
     * recordDao
     */
    @Autowired
    private ChatRecordDao recordDao;

    /**
     * 用户服务
     */
    @Autowired
    private UserService userService;


    /**
     * 副本服务
     */
    @Autowired
    private DuplicateService duplicateService;


    /**
     * 组队服务
     */
    @Autowired
    private GroupService groupService;

    /**
     * 协议转换
     */
    @Autowired
    private ProtoService protoService;



    /**
     * 用户协议
     */
    private MsgUserInfoProto.ResponseUserInfo userResp;

    /**
     * 玩家协议
     */
    private MsgRoleProto.ResponseRole roleResp;



    /**
     * 玩家获取当前的职业信息
     *
     * @param req       请求
     * @param channel   channel
     */
    public void getRole(MsgRoleProto.RequestRole req, Channel channel) {
        // 用户认证
        userResp = userService.userStateInterceptor(req.getUserId());
        if (userResp != null) {
            channel.writeAndFlush(userResp);
            return;
        }

        // 获取玩家信息
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());

        // 消息返回
        sendRoleResponse(channel, role, null, null,
                MsgRoleProto.RequestType.ROLE, "");
    }



    /**
     * 获取可选择的职业类别
     *
     * @param channel   channel
     */
    public void getRoleType(Channel channel) {
        // 获取所有的角色职业信息
        List<RoleInit> roleInitList = new ArrayList<>();
        LocalRoleInitMap.getRoleInitMap().forEach((k,v) -> roleInitList.add(v));

        // 消息返回
        sendRoleResponse(channel, null, roleInitList, null,
                MsgRoleProto.RequestType.ROLE_TYPE, ContentType.ROLE_TYPE);
    }



    /**
     * 创建玩家信息
     *
     * @param req       请求
     * @param channel   channel
     */
    public void createRole(MsgRoleProto.RequestRole req, Channel channel) {
        // 判断是否已经存在角色信息
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());
        if (role != null){
            protoService.sendFailedMsg(channel, ContentType.ROLE_HAS);
            return;
        }
        // 读取请求参数
        int type = req.getRoleType();
        String name = req.getRoleName();

        // 构造玩家角色信息，并保存数据
        Role r = generateRoleInfo(type, name, req.getUserId());

        // 消息返回
        sendRoleResponse(channel, r, null, null,
                MsgRoleProto.RequestType.CREATE_ROLE, ContentType.ROLE_CREATE_SUCCESS);
    }


    /**
     * 获取当前账号下的所有角色信息
     *
     * @param req           请求
     * @param channel       channel
     */
    public void getRoleList(MsgRoleProto.RequestRole req, Channel channel) {
        // 获取userId
        long userId = req.getUserId();

        // 获取玩家的角色列表
        List<Role> roles = roleDao.selectUserRole(userId);

        // 消息返回
        sendRoleResponse(channel, null, null, roles,
                MsgRoleProto.RequestType.ROLE_LIST, ContentType.ROLE_lIST);
    }


    /**
     * 选择对应的角色信息
     *
     * @param req       请求
     * @param channel   channel
     */
    public void roleState(MsgRoleProto.RequestRole req, Channel channel) {
        // 获取选择的角色id
        long userId = req.getUserId();
        long roleId = req.getRoleId();

        // 角色选择
        List<Role> roles = roleDao.selectUserRole(userId);
        Optional<Role> result = roles.stream().filter(r -> r.getRoleId() == roleId).findFirst();
        if (!result.isPresent()){
            protoService.sendFailedMsg(channel, ContentType.ROLE_EMPTY);
            return;
        }

        Role role = result.get();

        // 初始化玩家状态
        initUserState(role);

        // 添加玩家回血回蓝task
        addRecoverTask(role);

        // 更新玩家在线记录
        updateRoleState(role, true);

        // 消息返回
        protoService.sendCommonMsg(channel, ContentType.ROLE_CHOOSE);
    }



    /**
     * 生成玩家角色信息
     *
     * @param type      职业类型
     * @param name      角色名
     * @param userId    用户id
     * @return          生成的角色信息
     */
    private Role generateRoleInfo(int type, String name, long userId) {
        // 获取职业信息
        RoleInit init = LocalRoleInitMap.getRoleInitMap().get(type);

        // 设置职业信息
        Role r = new Role();
        r.setName(name);
        r.setType(init.getType());
        r.setHp(init.getHp());
        r.setMaxHp(init.getHp());
        r.setMp(init.getMp());

        // 设置基本信息
        r.setUserId(userId);
        r.setSiteId(1);
        r.setGold(0);
        r.setHonor(0);
        r.setLevel(1);
        r.setAlive(1);

        // 插入数据库
        int n = roleDao.insertUserRole(r);
        log.info("insert into user_role, affect rows : " + n);

        // 更新缓存
        LocalUserMap.getIdRoleMap().put(r.getRoleId(), r);
        LocalUserMap.getUserRoleMap().put(r.getUserId(), r);

        List<Role> siteRoleList = LocalUserMap.getSiteRolesMap().get(r.getSiteId());
        siteRoleList.add(r);
        LocalUserMap.getSiteRolesMap().put(r.getSiteId(), siteRoleList);

        return r;
    }



    /**
     * 更新玩家的信息 （缓存 + DB）
     *
     * @param role  玩家
     */
    public void updateRoleInfo(Role role){
        UpdateRoleInfoRun task = new UpdateRoleInfoRun(role);
        UserExecutorManager.addUserTask(role.getUserId(), task);
    }



    /**
     * 玩家复活
     *
     * @param role  玩家
     */
    public void reliveRole(Role role){
        role.setHp(role.getMaxHp());

        // 更新玩家信息
        updateRoleInfo(role);

        // 消息通知
        Channel channel = ChannelCache.getUserIdChannelMap().get(role.getUserId());
        sendRoleResponse(channel, role, null, null,
                MsgRoleProto.RequestType.ROLE_RELIVE, ContentType.USER_RELIVE_SUCCESS);
    }



    /**
     * 治疗挑战队列的玩家
     *
     * @param deque     挑战队列
     */
    public void healRole(Deque<Long> deque, int heal, int range) {
        if (deque == null || deque.isEmpty()){
            return;
        }
        int i = 0;
        for (Long id : deque) {
            // 治疗范围
            if (i >= range){
                break;
            }

            // 获取玩家信息，并更新血量
            Role role = LocalUserMap.getIdRoleMap().get(id);
            if (role == null){
                continue;
            }
            role.setHp(role.getHp() + heal);
            if (role.getHp() > role.getMaxHp()){
                role.setHp(role.getMaxHp());
            }
            i++;
            // 更新玩家属性
            updateRoleInfo(role);

            // 发送治疗消息
            Channel channel = ChannelCache.getUserIdChannelMap().get(role.getUserId());
            protoService.sendCommonMsg(channel, String.format(ContentType.ROLE_GET_HEAL, heal));
        }
    }


    /**
     * 如果存在护盾Buff，扣除护盾值
     *
     * @param role  玩家信息
     * @return      返回最新的血量值
     */
    public synchronized int cutShield(Role role, int damage){
        // 获取血量信息
        int hp = role.getHp();

        // 判断是否有护盾Buff
        List<RoleBuff> buffList = Optional.ofNullable(RoleBuffCache.getCache().getIfPresent(role.getRoleId()))
                .orElse(Lists.newArrayList());
        Optional<RoleBuff> result = buffList.stream()
                .filter(b -> b.getType() == 1)
                .findFirst();
        if (result.isPresent()) {

            // 记录护盾打碎后多余的扣血值
            int blood;
            RoleBuff buff = result.get();

            // 获取当前时间点
            long nowTs = System.currentTimeMillis();
            long td = nowTs - buff.getCreateTime();
            long cd = buff.getSec() * 1000;

            // 护盾时间的有效期判断
            if (td < cd) {
                int shield = buff.getShield();

                // 护盾值有效
                if (shield >= 0) {
                    shield -= damage;

                    // 盾碎了
                    if (shield <= 0) {
                        // 获取扣血值
                        blood = Math.abs(shield - damage);

                        // 移除 buff
                        buffList.remove(buff);

                        // 同时玩家扣血
                        if (hp > 0) {
                            hp -= blood;
                        }
                    } else {
                        // 更新shield
                        buff.setShield(shield);
                    }
                }
            } else {
                // 护盾技能过期，移除护盾技能
                hp -= damage;
                buffList.remove(buff);
            }
            // 更新Buff缓存
            RoleBuffCache.getCache().put(role.getRoleId(), buffList);
        }
        hp -= damage;
        return hp;
    }




    /**
     * 加锁判断是否足够金额购买物品，能得话直接支付，否得话返回失败
     *
     * @param receiver      玩家信息
     * @param amount        交易金额
     */
    public synchronized boolean enoughPay(Role receiver, Integer amount) {
        if (receiver.getGold() < amount){
            return false;
        }
        // 能购买
        receiver.setGold(receiver.getGold() - amount);
        updateRoleInfo(receiver);
        return true;
    }


    /**
     * 玩家被击杀时的具体操作
     *
     * @param role      玩家信息
     */
    public void doRoleDeath(Role role){

        // 如果存在队伍信息，将玩家移除队伍
        groupService.removeGroup(role);

        // 如果存在副本中，移除副本队列
        duplicateService.removeAttackedQueue(role);

        // 复活
        reliveRole(role);
    }


    /**
     * 初始化玩家数据
     *
     *
     * @param role          玩家信息
     */
    private void initUserState(Role role) {

        // 本地保存
        LocalUserMap.getUserRoleMap().put(role.getUserId(), role);

        // 分配用户的业务线程
        UserExecutorManager.bindUserExecutor(role.getUserId());

        // 记录玩家在线信息
        updateRoleState(role, true);
    }



    /**
     * 更新玩家的在线记录
     *
     * @param role      玩家角色
     * @param line      是否下线
     */
    public void updateRoleState(Role role, boolean line) {
        Date date = new Date();

        // 获取本地缓存的玩家在线记录
        RoleState record = RoleStateCache.getCache().getIfPresent(role.getRoleId());

        // cache not found
        if (record == null){
            // 缓存为空，直接Db中查找
            record = recordDao.selectUserOffline(role.getRoleId());

            // db not found
            if (record == null){

                // 记录玩家的在线状态信息
                RoleState state = new RoleState();
                state.setRoleId(role.getRoleId());

                if (line) {
                    state.setOnlineTime(date);
                }else {
                    state.setOfflineTime(date);
                }

                // 插入新增记录
                recordDao.insertUserState(state);
                RoleStateCache.getCache().put(role.getRoleId(), state);
                return;
            }else{
                // 设置旧的在线下线属性信息
                if (line){
                    record.setOnlineTime(date);
                }else {
                    record.setOfflineTime(date);
                }
                recordDao.updateUserState(record);
            }
        }else {
            if (line) {
                record.setOnlineTime(date);
            } else {
                record.setOfflineTime(date);
            }
            recordDao.updateUserState(record);
        }
        RoleStateCache.getCache().put(role.getRoleId(), record);
    }



    /**
     * 将自动回血回蓝的任务添加到用户线程池中
     *
     * @param role  玩家信息
     */
    private void addRecoverTask(Role role) {

        long userId = role.getUserId();

        // 判断是否已经
        if (FutureMap.getRecoverFutureMap().get(role.getRoleId()) == null){
            return;
        }
        // 为每一个玩家添加自动恢复的task
        RecoverUserRun task = new RecoverUserRun(role, null);
        ScheduledFuture future = UserExecutorManager.getUserExecutor(userId).scheduleAtFixedRate(task, 0, 8, TimeUnit.SECONDS);
        FutureMap.getRecoverFutureMap().put(role.getRoleId(), future);
    }





    /**
     * 判断玩家是否存在
     *
     * @param roleId    玩家id
     * @return          返回
     */
    public MsgRoleProto.ResponseRole roleInterceptor(long roleId){
        if(roleId <= 0){
            return MsgRoleProto.ResponseRole.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.ROLE_EMPTY_PARAM)
                    .build();
        }
        Role role = LocalUserMap.getIdRoleMap().get(roleId);
        if (role == null){
            return MsgRoleProto.ResponseRole.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.ROLE_EMPTY)
                    .build();
        }
        return null;
    }


    /**
     * 将玩家身上的所有持续伤害移除
     *
     * @param role 玩家信息
     */
    public void removeRoleFutureList(Role role) {
        List<ScheduledFuture> roleFutureList = LocalAttackCreepMap.getRoleSchFutMap().get(role.getRoleId());
        if (roleFutureList == null || roleFutureList.isEmpty()){
            return;
        }
        roleFutureList.forEach(f -> f.cancel(true));
    }




    /**
     * 发送玩家角色消息
     *
     * @param channel       channel
     * @param role          玩家信息
     * @param roleInits     玩家初始化信息
     * @param roles         玩家角色列表
     * @param type          消息类型
     * @param msg           消息
     */
    private void sendRoleResponse(Channel channel, Role role, List<RoleInit> roleInits,
                                  List<Role> roles, MsgRoleProto.RequestType type, String msg){
        MsgRoleProto.ResponseRole roleResp = MsgRoleProto.ResponseRole.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(msg)
                .setType(type)
                .setRole(protoService.transToRole(role))
                .addAllRoleInit(protoService.transToRoleInitList(roleInits))
                .addAllHasRole(protoService.transToRoleList(roles))
                .build();
        channel.writeAndFlush(roleResp);
    }


}
