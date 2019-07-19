package com.ljh.gamedemo.service;

import com.google.common.base.Strings;
import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.MD5Util;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.dao.UserDao;
import com.ljh.gamedemo.dao.UserRoleDao;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.entity.User;

import com.ljh.gamedemo.entity.UserToken;
import com.ljh.gamedemo.local.LocalUserMap;

import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.proto.protoc.RoleProto;
import com.ljh.gamedemo.run.UserExecutorManager;
import com.ljh.gamedemo.util.SessionUtil;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserRoleDao userRoleDao;


    /**
     * 用户登录后，并没有角色状态，需要通过 getState() 初始化玩家角色
     *
     * @param requestUserInfo
     * @return
     */
    public MsgUserInfoProto.ResponseUserInfo getState(MsgUserInfoProto.RequestUserInfo requestUserInfo){
        // 解析得到userId
        long userId = requestUserInfo.getUserId();
        Role role = null;

        // 优先查找本地角色
        role = LocalUserMap.userRoleMap.get(userId);
        if (role == null){

            // 数据库查找
            List<Role> roles = userRoleDao.selectUserRole(userId);
            if (roles.isEmpty()){
                return MsgUserInfoProto.ResponseUserInfo.newBuilder()
                        .setContent(ContentType.ROLE_EMPTY)
                        .build();
            }

            // 这里暂时只存在一个角色
            role = roles.get(0);
        }

        // 本地保存
        LocalUserMap.userRoleMap.put(userId, role);

        // 分配用户的业务线程
        UserExecutorManager.bindUserExecutor(userId);

        // 确定角色成功，返回角色信息
        RoleProto.Role roleMsg = RoleProto.Role.newBuilder()
                .setRoleId(role.getRoleId())
                .setName(role.getName())
                .setType(role.getType())
                .setLevel(role.getLevel())
                .setAlive(role.getAlive())
                .setHp(role.getHp())
                .setMp(role.getMp())
                .build();

        return MsgUserInfoProto.ResponseUserInfo.newBuilder()
                .setType(MsgUserInfoProto.RequestType.STATE)
                .setUserId(userId)
                .setContent(ContentType.ROLE_CHOOSE)
                .setResult(ResultCode.SUCCESS)

                // 设置role对象
                .setRole(roleMsg)

                .build();
    }


    /**
     * 登录操作7
     *
     * @param requestUserInfo
     * @return
     */
    public MsgUserInfoProto.ResponseUserInfo login(Channel channel, MsgUserInfoProto.RequestUserInfo requestUserInfo){
        // 解析message的请求参数
        String userName = requestUserInfo.getUsername();
        String password = requestUserInfo.getPassword();

        // 校验请求参数
        if (Strings.isNullOrEmpty(userName) || Strings.isNullOrEmpty(password)){
            return MsgUserInfoProto.ResponseUserInfo.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.USER_EMPTY_LOGIN_PARAM)
                    .build();
        }

        // 获取混淆的pwd，数据库查玩家信息
        String md5Pwd = MD5Util.hashPwd(password);
        User user = userDao.selectUser(userName);
        if (user == null){
            return MsgUserInfoProto.ResponseUserInfo.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.USER_EMPTY_DATA)
                    .build();
        }

        // 校验密码的正确性
        if (!user.getPassword().equals(md5Pwd)){
            return MsgUserInfoProto.ResponseUserInfo.newBuilder()
                    .setContent(ContentType.BAD_PASSWORD)
                    .setResult(ResultCode.FAILED)
                    .build();
        }

        // 登录成功，获取ID和token
        long userId = user.getUserId();

        UserToken userToken = userDao.selectUserTokenByID(userId);
        String token = userToken.getToken();

        // 在本地缓存Map中存储当前的用户信息
        LocalUserMap.userMap.put(userId, user);

        // 绑定channel
        SessionUtil.bindSession(userId, channel);

        // 分配用户的业务线程
        UserExecutorManager.bindUserExecutor(userId);

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
     * 注册操作
     *
     * @param requestUserInfo
     * @return
     */
    public MsgUserInfoProto.ResponseUserInfo register(Channel channel, MsgUserInfoProto.RequestUserInfo requestUserInfo){
        // 解析message请求参数
        String userName = requestUserInfo.getUsername();
        String password = requestUserInfo.getPassword();

        // 判断请求参数
        if (Strings.isNullOrEmpty(userName) || Strings.isNullOrEmpty(password)){
            return MsgUserInfoProto.ResponseUserInfo.newBuilder()
                    .setContent(ContentType.USER_EMPTY_REGISTER_PARAM)
                    .setResult(ResultCode.FAILED)
                    .build();
        }

        // 获取md5Pwd，存数据库user_account
        String md5Pwd = MD5Util.hashPwd(password);
        int n = userDao.insertUserAccount(0l, userName, md5Pwd);
        if (n <= 0){
            return MsgUserInfoProto.ResponseUserInfo.newBuilder()
                    .setContent(ContentType.REGISTER_FAILED)
                    .setResult(ResultCode.FAILED)
                    .build();
        }

        // 接着查找user信息
        User user = userDao.selectUser(userName);
        long userId = user.getUserId();

        // 写入token返回
        String token = MD5Util.hashToken(userName);
        int m = userDao.insertUserToken(userId, token);

        // 注册成功，将玩家信息写如本地缓存
        LocalUserMap.userMap.put(userId, user);

        SessionUtil.bindSession(userId, channel);

        // 分配用户的业务线程
        UserExecutorManager.bindUserExecutor(userId);

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
     * @param requestUserInfo
     * @return
     */
    public MsgUserInfoProto.ResponseUserInfo exit(Channel channel, MsgUserInfoProto.RequestUserInfo requestUserInfo){
        // 用户判断
        long userId = requestUserInfo.getUserId();
        if (userId <= 0){
            return MsgUserInfoProto.ResponseUserInfo.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.USER_EMPTY_DATA)
                    .build();
        }

        // 获取当前的玩家角色
        Role role;
        role = LocalUserMap.userRoleMap.get(userId);
        if (role == null){
            role = userRoleDao.selectUserRole(userId).get(0);
        }
        if (role == null){
            return MsgUserInfoProto.ResponseUserInfo.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.ROLE_EMPTY)
                    .build();
        }

        // 获取当前的位置信息
        int siteId = role.getSiteId();
        long roleId = role.getRoleId();

        // 移除当前玩家角色的位置信息
        List<Role> roleList = LocalUserMap.siteRolesMap.get(siteId);

        // Iterator解决并发修改的问题
        Iterator<Role> iterator = roleList.iterator();
        while (iterator.hasNext()){
            if (iterator.next().getRoleId() == roleId){
                iterator.remove();
            }
        }

        // 移除当前的玩家在线信息
        LocalUserMap.userMap.remove(userId);

        // 移除当前玩家的角色在线信息
        LocalUserMap.userRoleMap.remove(userId);

        SessionUtil.unBindSession(channel);

        // 解除绑定用户线程
        UserExecutorManager.unBindUserExecutor(userId);

        // 更新数据库role的site信息
        int n = userRoleDao.updateRoleSiteInfo(userId, roleId, siteId, role.getLevel(), role.getHp(), role.getMp());
        if (n <= 0){
            return MsgUserInfoProto.ResponseUserInfo.newBuilder()
                    .setResult(ResultCode.FAILED)
                    .setContent(ContentType.UPDATE_ROLE_SITE)
                    .build();
        }

        // 成功操作
        return MsgUserInfoProto.ResponseUserInfo.newBuilder()
                .setType(MsgUserInfoProto.RequestType.EXIT)
                .setResult(ResultCode.FAILED)
                .setContent(ContentType.EXIT_SUCCESS)
                .build();
    }
}
