package com.ljh.gamedemo.server;

import com.ljh.gamedemo.server.codec.CustomProtobufDecoder;
import com.ljh.gamedemo.server.codec.CustomProtobufEncoder;
import com.ljh.gamedemo.server.handler.*;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.springframework.stereotype.Component;

@Component
public class NettyServerHandlerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        pipeline.addLast(new ServerIdleStateHandler());

        pipeline.addLast(new NoDataReadHandler());

        // 添加自定义编码解码器
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
