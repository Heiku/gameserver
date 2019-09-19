package com.ljh.gamedemo.module.role.handler;

import com.ljh.gamedemo.proto.protoc.MsgRoleProto;
import com.ljh.gamedemo.module.role.service.RoleService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.ljh.gamedemo.server.request.RequestRoleType.*;

/**
 * 玩家职业选择处理器
 *
 * @Author: Heiku
 * @Date: 2019/8/19
 */
public class RoleHandler extends SimpleChannelInboundHandler<MsgRoleProto.RequestRole> {

    /**
     * 玩家职业处理
     */
    private static RoleService roleService;

    static {
        roleService = SpringUtil.getBean(RoleService.class);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgRoleProto.RequestRole req) throws Exception {
        int type = req.getTypeValue();
        switch (type){
            case ROLE:
                roleService.getRole(req, ctx.channel());
                break;
            case ROLE_TYPE:
                roleService.getRoleType(ctx.channel());
                break;
            case CREATE_ROLE:
                roleService.createRole(req, ctx.channel());
                break;
            case ROLE_LIST:
                roleService.getRoleList(req, ctx.channel());
                break;
            case ROLE_STATE:
                roleService.roleState(req, ctx.channel());
                break;
        }
    }
}
