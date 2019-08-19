package com.ljh.gamedemo.service;

import com.ljh.gamedemo.common.ContentType;
import com.ljh.gamedemo.common.ResultCode;
import com.ljh.gamedemo.dao.UserRoleDao;
import com.ljh.gamedemo.entity.Role;
import com.ljh.gamedemo.entity.RoleInit;
import com.ljh.gamedemo.local.LocalRoleInitMap;
import com.ljh.gamedemo.local.LocalSiteMap;
import com.ljh.gamedemo.local.LocalUserMap;
import com.ljh.gamedemo.proto.protoc.MsgRoleProto;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
     * 用户服务
     */
    @Autowired
    private UserService userService;

    /**
     * 协议转换
     */
    @Autowired
    private ProtoService protoService;

    /**
     * userResponse
     */
    private MsgUserInfoProto.ResponseUserInfo userResp;

    /**
     * roleResponse
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
        if (userResp != null){
            channel.writeAndFlush(userResp);
            return;
        }

        // 获取玩家信息
        Role role = LocalUserMap.getUserRoleMap().get(req.getUserId());
        roleResp = MsgRoleProto.ResponseRole.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setType(MsgRoleProto.RequestType.ROLE)
                .setRole(protoService.transToRole(role))
                .build();
        channel.writeAndFlush(roleResp);
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
        roleResp = MsgRoleProto.ResponseRole.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.ROLE_TYPE)
                .setType(MsgRoleProto.RequestType.ROLE_TYPE)
                .addAllRoleInit(protoService.transToRoleInitList(roleInitList))
                .build();
        channel.writeAndFlush(roleResp);
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
            roleResp = responseRoleFailed(ContentType.ROLE_HAS);
            channel.writeAndFlush(roleResp);
            return;
        }
        // 读取请求参数
        int type = req.getRoleType();
        String name = req.getRoleName();

        // 构造玩家角色信息，并保存数据
        Role r = generateRoleInfo(type, name, req.getUserId());

        // 消息返回
        roleResp = MsgRoleProto.ResponseRole.newBuilder()
                .setResult(ResultCode.SUCCESS)
                .setContent(ContentType.ROLE_CREATE_SUCCESS)
                .setRole(protoService.transToRole(r))
                .build();
        channel.writeAndFlush(roleResp);
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
     * 返回失败消息
     *
     * @param msg   消息
     * @return      协议返回
     */
    private MsgRoleProto.ResponseRole responseRoleFailed(String msg){
        roleResp = MsgRoleProto.ResponseRole.newBuilder()
                .setResult(ResultCode.FAILED)
                .setContent(msg)
                .build();
        return roleResp;
    }
}
