package com.ljh.gamedemo.module.user.service;

import com.google.common.base.Strings;
import com.ljh.gamedemo.common.CommonDBType;
import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.module.base.cache.ChannelCache;
import com.ljh.gamedemo.module.role.asyn.RoleSaveManager;
import com.ljh.gamedemo.module.role.asyn.run.RoleSaveRun;
import com.ljh.gamedemo.module.role.bean.Role;
import com.ljh.gamedemo.module.role.dao.UserRoleDao;
import com.ljh.gamedemo.module.role.service.RoleService;
import com.ljh.gamedemo.module.user.asyn.UserSaveManager;
import com.ljh.gamedemo.module.user.asyn.run.UserSaveRun;
import com.ljh.gamedemo.module.user.bean.User;
import com.ljh.gamedemo.module.user.bean.UserAccount;
import com.ljh.gamedemo.module.user.dao.UserDao;
import com.ljh.gamedemo.module.user.local.LocalUserMap;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.module.user.asyn.UserExecutorManager;
import com.ljh.gamedemo.util.MD5Util;
import com.ljh.gamedemo.util.SessionUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
     * 玩家服务
     */
    @Autowired
    private RoleService roleService;

    /**
     * 用户协议返回
     */
    private MsgUserInfoProto.ResponseUserInfo userResp;



    /**
     * 登录操作
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

        // 判断是否异地登录
        Channel oldCh = ChannelCache.getUserIdChannelMap().get(user.getUserId());
        if (oldCh != null){
            doOutLineUser(oldCh);
        }

        // 登录成功，获取ID和token
        long userId = user.getUserId();

        // 在本地缓存Map中存储当前的用户信息
        LocalUserMap.userMap.put(userId, user);

        // 绑定 channel
        SessionUtil.bindSession(userId, channel);

        // 成功消息返回
        return MsgUserInfoProto.ResponseUserInfo.newBuilder()
                .setType(MsgUserInfoProto.RequestType.LOGIN)
                .setContent(ContentType.LOGIN_SUCCESS)
                .setUserId(userId)
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
        UserAccount userAccount = new UserAccount(userName, md5Pwd);
        UserSaveManager.getExecutorService().submit(new UserSaveRun(userAccount, CommonDBType.INSERT));

        // 接着查找user信息
        User user = userDao.selectUser(userName);
        long userId = user.getUserId();

        // 注册成功，将玩家信息写如本地缓存
        LocalUserMap.userMap.put(userId, user);

        return MsgUserInfoProto.ResponseUserInfo.newBuilder()
                .setType(MsgUserInfoProto.RequestType.REGISTER)
                .setResult(ResultCode.SUCCESS)
                .setUserId(userId)
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
        List<Role> roleList = LocalUserMap.getSiteRolesMap().get(siteId);
        roleList.removeIf(r -> r.getRoleId() == roleId);

        // 移除当前的玩家在线信息
        LocalUserMap.getUserMap().remove(userId);
        LocalUserMap.getUserRoleMap().remove(userId);

        SessionUtil.unBindSession(channel);

        // 更新玩家的在线信息
        roleService.updateRoleState(role, false);

        // 解除绑定用户线程
        UserExecutorManager.unBindUserExecutor(userId);

        // 更新数据库role的site信息
        RoleSaveManager.getExecutorService().submit(new RoleSaveRun(role, CommonDBType.UPDATE));

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
