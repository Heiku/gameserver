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
import com.ljh.gamedemo.proto.MessageBase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserRoleDao userRoleDao;


    // TODO:这里的state命令后期改为：选择对应的角色进入游戏世界  (选角色)
    /**
     * 用户登录后，并没有角色状态，需要通过 getState() 初始化玩家角色
     *
     * @param message
     * @return
     */
    public MessageBase.Message getState(MessageBase.Message message){
        // 解析得到userId
        long userId = message.getUserId();
        Role role = null;

        // 优先查找本地角色
        role = LocalUserMap.userRoleMap.get(userId);
        if (role == null){

            // 数据库查找
            List<Role> roles = userRoleDao.selectUserRole(userId);
            if (roles.isEmpty()){
                return MessageBase.Message.newBuilder()
                        .setContent(ContentType.ROLE_EMPTY)
                        .build();
            }

            // 这里暂时只存在一个角色
            role = roles.get(0);
        }

        // 本地保存
        LocalUserMap.userRoleMap.put(userId, role);

        return MessageBase.Message.newBuilder()
                .setUserId(userId)
                .setContent(ContentType.ROLE_CHOOSE)
                .build();
    }


    /**
     * 登录操作
     *
     * @param message
     * @return
     */
    public MessageBase.Message login(MessageBase.Message message){
        // 解析message的请求参数
        MessageBase.UserInfo userInfo = message.getUserList().get(0);
        String userName = userInfo.getUserName();
        String password = userInfo.getPassword();

        // 校验请求参数
        if (Strings.isNullOrEmpty(userName) || Strings.isNullOrEmpty(password)){
            return MessageBase.Message.newBuilder()
                    .setContent(ContentType.USER_EMPTY_PARAM)
                    .setResult(ResultCode.FAILED)
                    .build();
        }

        // 获取混淆的pwd，数据库查玩家信息
        String md5Pwd = MD5Util.hashPwd(password);
        User user = userDao.selectUser(userName);
        if (user == null){
            return MessageBase.Message.newBuilder()
                    .setContent(ContentType.USER_EMPTY_DATA)
                    .setResult(ResultCode.FAILED)
                    .build();
        }

        // 校验密码的正确性
        if (!user.getPassword().equals(md5Pwd)){
            return MessageBase.Message.newBuilder()
                    .setContent(ContentType.BAD_PASSWORD)
                    .setResult(ResultCode.FAILED)
                    .build();
        }

        // 登录成功
        long userId = user.getUserId();
        UserToken userToken = userDao.selectUserTokenByID(userId);
        MessageBase.UserInfo respUser = MessageBase.UserInfo.newBuilder()
                .setToken(userToken.getToken())
                .setUserName(userName).build();

        // 在本地缓存Map中存储当前的用户信息
        LocalUserMap.userMap.put(userId, user);

        // 成功消息返回
        return MessageBase.Message.newBuilder()
                .setCmd(MessageBase.Message.CommandType.LOGIN)
                .setContent(ContentType.LOGIN_SUCCESS)
                .addUser(respUser)
                .setUserId(userId)
                .setResult(ResultCode.SUCCESS)
                .build();
    }


    /**
     * 注册操作
     *
     * @param message
     * @return
     */
    public MessageBase.Message register(MessageBase.Message message){
        // 解析message请求参数
        MessageBase.UserInfo userInfo = message.getUserList().get(0);
        String userName = userInfo.getUserName();
        String password = userInfo.getPassword();

        if (Strings.isNullOrEmpty(userName) || Strings.isNullOrEmpty(password)){
            return MessageBase.Message.newBuilder()
                    .setContent(ContentType.USER_EMPTY_PARAM)
                    .setResult(ResultCode.FAILED)
                    .build();
        }

        // 获取md5Pwd，存数据库user_account
        String md5Pwd = MD5Util.hashPwd(password);
        int n = userDao.insertUserAccount(0l, userName, md5Pwd);
        if (n <= 0){
            return MessageBase.Message.newBuilder()
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
        MessageBase.UserInfo backUserInfo = MessageBase.UserInfo.newBuilder()
                .setUserName(userName)
                .setToken(token)
                .build();

        // 注册成功，将玩家信息写如本地缓存
        LocalUserMap.userMap.put(userId, user);

        return MessageBase.Message.newBuilder()
                .addUser(backUserInfo)
                .setUserId(userId)
                .setContent(ContentType.REGISTER_SUCCESS)
                .setResult(ResultCode.SUCCESS)
                .setCmd(MessageBase.Message.CommandType.REGISTER)
                .build();
    }
}
