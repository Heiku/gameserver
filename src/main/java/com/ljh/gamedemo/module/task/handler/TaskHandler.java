package com.ljh.gamedemo.module.task.handler;

import com.ljh.gamedemo.proto.protoc.MsgTaskProto;
import com.ljh.gamedemo.proto.protoc.MsgUserInfoProto;
import com.ljh.gamedemo.module.task.service.TaskService;
import com.ljh.gamedemo.module.user.service.UserService;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.ljh.gamedemo.server.request.RequestTaskType.*;

/**
 * 任务请求处理器
 *
 * @Author: Heiku
 * @Date: 2019/9/6
 */
public class TaskHandler extends SimpleChannelInboundHandler<MsgTaskProto.RequestTask> {

    /**
     * 用户服务
     */
    private static UserService userService;

    /**
     * 任务服务
     */
    private static TaskService taskService;

    static {
        userService = SpringUtil.getBean(UserService.class);
        taskService = SpringUtil.getBean(TaskService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgTaskProto.RequestTask req) throws Exception {
        // 用户状态判断
        MsgUserInfoProto.ResponseUserInfo userResp;
        if ((userResp = userService.userStateInterceptor(req.getUserId())) != null){
            ctx.channel().writeAndFlush(userResp);
        }

        int type = req.getTypeValue();
        Channel channel = ctx.channel();

        switch (type){
            case TASK_ALL:
                taskService.taskAll(req, channel);
                break;
            case TASK_STATE:
                taskService.taskState(req, channel);
                break;
            case TASK_RECEIVE:
                taskService.taskReceive(req, channel);
                break;
            case TASK_GIVE_UP:
                taskService.taskGiveUp(req, channel);
                break;
            case TASK_SUBMIT:
                taskService.taskSubmit(req, channel);
                break;
        }
    }
}
