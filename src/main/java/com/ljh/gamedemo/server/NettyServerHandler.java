package com.ljh.gamedemo.server;

import com.ljh.gamedemo.service.EntityService;
import com.ljh.gamedemo.message.HeartbeatResponsePacket;
import com.ljh.gamedemo.proto.MessageBase;
import com.ljh.gamedemo.service.SiteService;
import com.ljh.gamedemo.service.UserService;
import com.ljh.gamedemo.util.DateUtil;
import com.ljh.gamedemo.util.SpringUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;


@Slf4j
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<MessageBase.Message> {

    @Autowired
    private static EntityService entityService;

    @Autowired
    private static SiteService siteService;

    @Autowired
    private static UserService userService;


    private MessageBase.Message msg;

    static {
        entityService = SpringUtil.getBean(EntityService.class);
        siteService = SpringUtil.getBean(SiteService.class);
        userService = SpringUtil.getBean(UserService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageBase.Message message) throws Exception {

        // Base Message
        if (message.getCmd().equals(MessageBase.Message.CommandType.HEARTBEAT_REQUEST)){
            log.info("收到客户端的心跳请求信息：{}", message.toString());
            // 回应
            ctx.writeAndFlush(new HeartbeatResponsePacket());
        }else if (message.getCmd().equals(MessageBase.Message.CommandType.NORMAL)){
            log.info("收到客户端的业务信息：{}", message.toString());
        }


        // 业务消息
        // 获取当前位置
        if (message.getCmd().equals(MessageBase.Message.CommandType.SITE)){
            log.info("收到客户端的业务信息: {}", message.toString());

            // 封装返回消息
            msg = tranformMessage("你当前的位置是：" + siteService.getNowSiteCName(message));
            ctx.writeAndFlush(msg);

            // 获取当前时间
        }else if (message.getCmd().equals(MessageBase.Message.CommandType.DATE)){
            log.info("收到客户端的业务信息：{}", message.toString());

            msg = tranformMessage(DateUtil.getCurrentDate());
            ctx.writeAndFlush(msg);

            // 移动指令
        }else if (message.getCmd().equals(MessageBase.Message.CommandType.MOVE)){
            log.info("收到客户端的业务信息：{}", message.toString());

            // 这里收到 msg(cmd:move, content:destination)
            msg = siteService.move(message);
            ctx.writeAndFlush(msg);

            // 获取当前场景的npc状态
        }else if (message.getCmd().equals(MessageBase.Message.CommandType.AOI)){
            log.info("收到客户端的业务消息：{}", message.toString());

            // 调用entityService 获取实体信息
            msg = entityService.getAoi(message);
            ctx.writeAndFlush(msg);

            // 获取当前的用户状态
        }else if (message.getCmd().equals(MessageBase.Message.CommandType.USER_STATE)){
            log.info("收到客户端的业务消息：{}", message.toString());

            msg = userService.getState(message);
            ctx.writeAndFlush(msg);
        }else if (message.getCmd().equals(MessageBase.Message.CommandType.LOGIN)){
            log.info("收到客户端的业务消息：{}", message.toString());

            msg = userService.login(message);
            ctx.writeAndFlush(msg);
        }else if (message.getCmd().equals(MessageBase.Message.CommandType.REGISTER)){
            log.info("收到客户端的业务消息：{}", message.toString());

            msg = userService.register(message);
            ctx.writeAndFlush(msg);
        }else if (message.getCmd().equals(MessageBase.Message.CommandType.EXIT)){
            log.info("收到客户端的业务消息：{}", message.toString());

            msg = userService.exit(message);
            ctx.writeAndFlush(msg);
        }
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    // 消息组装
    public static MessageBase.Message tranformMessage(String content){
        return MessageBase.Message.newBuilder()
                .setContent(content)
                .build();
    }
}
