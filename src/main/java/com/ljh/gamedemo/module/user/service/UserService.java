package com.ljh.gamedemo.module.user.service;

import com.google.common.base.Strings;
import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.module.chat.dao.ChatRecordDao;
import com.ljh.gamedemo.module.user.local.LocalUserMap;
import com.ljh.gamedemo.module.base.cache.ChannelCache;
import com.ljh.gamedemo.module.role.cache.RoleStateCache;
import com.ljh.gamedemo.module.role.dao.UserRoleDao;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.role.bean.RoleState;
import com.ljh.gamedemo.module.user.dao.UserDao;
import com.ljh.gamedemo.module.user.bean.User;
import com.ljh.gamedemo.module.user.bean.UserToken;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.run.UserExecutorManager;
import com.ljh.gamedemo.run.record.FutureMap;
import com.ljh.gamedemo.run.user.RecoverUserRun;
import com.ljh.gamedemo.module.chat.service.ChatService;
import com.ljh.gamedemo.module.guild.service.GuildService;
import com.ljh.gamedemo.module.base.service.ProtoService;
import com.ljh.gamedemo.util.MD5Util;
import com.ljh.gamedemo.util.SessionUtil;
import io.netty.channel.Channel;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class UserService {

    /**
     * UserDao
     */
    @Autowired
    private UserDao userDao;

    /**
     * UserRoleDao
     */
    @Autowired
    private UserRoleDao userRoleDao;

    /**
     * ChatRecordDao
     */
    @Autowired
    private ChatRecordDao recordDao;

    /**
     * 聊天服务
     */
    @Autowired
    private ChatService chatService;

    /**
     * 公会服务
     */
    @Autowired
    private GuildService guildService;

    /**
     * 协议服务
     */
    @Autowired
    private ProtoService protoService;

    /**
     * 用户协议返回
     */
    private MsgUserInfoProto.ResponseUserInfo userResp;

    /**
     * 用户登录后，并没有角色状态，需要通过 getState() 初始化玩家角色
     *
     * @param requestUserInfo       请求
     * @return                      用户协议返回
     */
    public MsgUserInfoProto.ResponseUserInfo getState(Channel channel, MsgUserInfoProto.RequestUserInfo requestUserInfo){
        // 解析得到userId
        long userId = requestUserInfo.getUserId();

        // 优先查找本地角色
        Role role = LocalUserMap.getUserRoleMap().get(userId);
        if (role == null){

            // 数据库查找
            List<Role> roles = userRoleDao.selectUserRole(userId);
            if (roles.isEmpty()){
                return combineFailedMsg(ContentType.ROLE_EMPTY);
            }

            // 这里暂时只存在一个角色
            role = roles.get(0);
        }

        // 初始化玩家状态
        initUserState(role, channel);

        // 添加玩家回血回蓝task
        addRecoverTask(role);

        // 获取离线的私信消息
        chatService.receiveOfflineMsg(role);

        // 进入公会聊天
        //guildService.joinGuildChannelGroup(role);

        // 确定角色成功，返回角色信息
        return MsgUserInfoProto.ResponseUserInfo.newBuilder()
                .setType(MsgUserInfoProto.RequestType.STATE)
                .setUserId(userId)
                .setContent(ContentType.ROLE_CHOOSE)
                .setResult(ResultCode.SUCCESS)
                .setRole(protoService.transToRole(role))
                .build();
    }

    /**
     * 将自动回血回蓝的任务添加到用户线程池中
     *
     * @param role  玩家信息
     */
    private void addRecoverTask(Role role) {

        long userId = role.getUserId();

        // 为每一个玩家添加自动恢复的task
        RecoverUserRun task = new RecoverUserRun(userId, null, null);
        ScheduledFuture future = UserExecutorManager.getUserExecutor(userId).scheduleAtFixedRate(task, 0, 5, TimeUnit.SECONDS);
        FutureMap.getRecoverFutureMap().put(role.getRoleId(), future);
    }


    /**
     * 初始化玩家数据
     *
     *
     * @param role          玩家信息
     * @param channel       channel
     */
    private void initUserState(Role role, Channel channel) {

        // 本地保存
        LocalUserMap.userRoleMap.put(role.getUserId(), role);

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
     * 登录操作7
     *
     * @param requestUserInfo   请求
     * @return                  用户协议返回
     */
    public MsgUserInfoProto.ResponseUserInfo login(Channel channel, MsgUserInfoProto.RequestUserInfo requestUserInfo){
        // 解析message的请求参数
        String userName = requestUserInfo.getUsername();
        String password = requestUserInfo.getPassword();

        // 校验请求参数
        if (Strings.isNullOrEmpty(userName) || Strings.isNullOrEmpty(password)){
            return combineFailedMsg(ContentType.USER_EMPTY_LOGIN_PARAM);
        }

        // 获取混淆的pwd，数据库查玩家信息
        String md5Pwd = MD5Util.hashPwd(password);
        User user = userDao.selectUser(userName);
        if (user == null){
            return combineFailedMsg(ContentType.USER_EMPTY_DATA);
        }
        // 校验密码的正确性
        if (!user.getPassword().equals(md5Pwd)){
            return combineFailedMsg(ContentType.BAD_PASSWORD);
        }

        // 判断是否已经有账号登录
        Channel oldCh = ChannelCache.getUserIdChannelMap().get(user.getUserId());
        if (oldCh != null){
            doOutLineUser(oldCh);
        }

        // 登录成功，获取ID和token
        long userId = user.getUserId();

        UserToken userToken = userDao.selectUserTokenByID(userId);
        String token = userToken.getToken();

        // 在本地缓存Map中存储当前的用户信息
        LocalUserMap.userMap.put(userId, user);

        // 绑定 channel
        SessionUtil.bindSession(userId, channel);

        // 成功消息返回
        return MsgUserInfoProto.ResponseUserInfo.newBuilder()
                .setType(MsgUserInfoProto.RequestType.LOGIN)
                .setContent(ContentType.LOGIN_SUCCESS)
                .setUserId(userId)
                .setToken(token)
                .setResult(ResultCode.SUCCESS)
                .build();
    }


    /**
     * 顶号
     *
     * @param oldCh     old channel
     */
    private void doOutLineUser(Channel oldCh) {
        // 消息通知
        userResp = combineFailedMsg(ContentType.USER_OUT_OF);
        oldCh.writeAndFlush(userResp);

        // 旧channel 关闭
        oldCh.close();
    }


    /**
     * 注册操作
     *
     * @param requestUserInfo   请求
     * @return                  用户协议返回
     */
    public MsgUserInfoProto.ResponseUserInfo register(Channel channel, MsgUserInfoProto.RequestUserInfo requestUserInfo){
        // 解析message请求参数
        String userName = requestUserInfo.getUsername();
        String password = requestUserInfo.getPassword();

        // 判断请求参数
        if (Strings.isNullOrEmpty(userName) || Strings.isNullOrEmpty(password)){
            return combineFailedMsg(ContentType.USER_EMPTY_REGISTER_PARAM);
        }

        // 获取md5Pwd，存数据库user_account
        String md5Pwd = MD5Util.hashPwd(password);
        int n = userDao.insertUserAccount(0L, userName, md5Pwd);
        log.info("insert into user_account, affected rows: " + n);

        // 接着查找user信息
        User user = userDao.selectUser(userName);
        long userId = user.getUserId();

        // 写入token返回
        String token = MD5Util.hashToken(userName);
        int m = userDao.insertUserToken(userId, token);
        log.info("insert into user_token, affected rows: " + m);

        // 注册成功，将玩家信息写如本地缓存
        LocalUserMap.userMap.put(userId, user);


        return MsgUserInfoProto.ResponseUserInfo.newBuilder()
                .setType(MsgUserInfoProto.RequestType.REGISTER)
                .setResult(ResultCode.SUCCESS)
                .setUserId(userId)
                .setToken(token)
                .setContent(ContentType.REGISTER_SUCCESS)
                .build();
    }


    /**
     * 玩家退出游戏，玩家状态改变，角色最终位置持久化
     *
     * @param requestUserInfo       请求
     * @return                      用户协议返回
     */
    public MsgUserInfoProto.ResponseUserInfo exit(Channel channel, MsgUserInfoProto.RequestUserInfo requestUserInfo){
        // 用户判断
        long userId = requestUserInfo.getUserId();
        userResp = userStateInterceptor(requestUserInfo.getUserId());
        if (userResp != null){
            return userResp;
        }

        // 获取当前的玩家角色
        Role role;
        role = LocalUserMap.userRoleMap.get(userId);
        if (role == null){
            role = userRoleDao.selectUserRole(userId).get(0);
        }
        if (role == null){
            return combineFailedMsg(ContentType.ROLE_EMPTY);
        }

        // 获取当前的位置信息
        int siteId = role.getSiteId();
        long roleId = role.getRoleId();

        // 移除当前玩家角色的位置信息
        List<Role> roleList = LocalUserMap.siteRolesMap.get(siteId);
        roleList.removeIf(r -> r.getRoleId() == roleId);

        // 移除当前的玩家在线信息
        LocalUserMap.userMap.remove(userId);
        LocalUserMap.userRoleMap.remove(userId);

        SessionUtil.unBindSession(channel);

        // 更新玩家的在线信息
        updateRoleState(role, false);

        // 解除绑定用户线程
        UserExecutorManager.unBindUserExecutor(userId);

        // 更新数据库role的site信息
        int n = userRoleDao.updateRoleSiteInfo(role);
        if (n <= 0){
            return combineFailedMsg(ContentType.UPDATE_ROLE_SITE);
        }

        // 成功操作
        return combineFailedMsg(ContentType.EXIT_SUCCESS);
    }



    /**
     * 用户状态判断拦截器
     *
     * @param userId    请求携带的认证id
     * @return          消息饭返回
     */
    public MsgUserInfoProto.ResponseUserInfo userStateInterceptor(long userId){
        // 判断玩家账号
        if (userId <= 0){
            return combineFailedMsg(ContentType.USER_EMPTY_DATA);
        }

        // 判断角色信息
        Role role = LocalUserMap.userRoleMap.get(userId);
        if (role == null){
            return combineFailedMsg(ContentType.ROLE_EMPTY);
        }
        return null;
    }


    /**
     * 返回失败消息
     *
     * @param msg 消息文本
     * @return      消息返回
     */
    private MsgUserInfoProto.ResponseUserInfo combineFailedMsg(String msg){
        return MsgUserInfoProto.ResponseUserInfo.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(msg)
                .build();
    }
}
