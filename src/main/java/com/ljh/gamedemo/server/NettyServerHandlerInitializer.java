package com.ljh.gamedemo.server;

import com.ljh.gamedemo.server.codec.CustomProtobufDecoder;
import com.ljh.gamedemo.server.codec.CustomProtobufEncoder;
import com.ljh.gamedemo.server.handler.CommonServerHandler;
import com.ljh.gamedemo.server.handler.EntityInfoServerHandler;
import com.ljh.gamedemo.server.handler.SiteInfoServerHandler;
import com.ljh.gamedemo.server.handler.UserInfoServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.springframework.stereotype.Component;

@Component
public class NettyServerHandlerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        // 添加心跳机制
       /* pipeline.addLast(new ServerIdleStateHandler())
                .addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new ProtobufDecoder(MessageBase.Message.getDefaultInstance()))

                .addLast(new ProtobufVarint32LengthFieldPrepender())
                .addLast(new ProtobufEncoder())

                .addLast(new NettyServerHandler());*/

        pipeline.addLast("decoder",new CustomProtobufDecoder());
        pipeline.addLast("encoder",new CustomProtobufEncoder());

        // 公共处理器
        pipeline.addLast(new CommonServerHandler());

        // 用户请求处理器
        pipeline.addLast(new UserInfoServerHandler());

        // 实体请求处理器
        pipeline.addLast(new EntityInfoServerHandler());

        // 场景请求处理器
        pipeline.addLast(new SiteInfoServerHandler());
    }
}
