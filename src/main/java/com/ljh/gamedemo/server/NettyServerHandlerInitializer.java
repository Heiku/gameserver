package com.ljh.gamedemo.server;

import com.ljh.gamedemo.server.codec.CustomProtobufDecoder;
import com.ljh.gamedemo.server.codec.CustomProtobufEncoder;
import com.ljh.gamedemo.server.handler.CustomProtoServerHandler;
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
        pipeline.addLast(new CustomProtoServerHandler());
        pipeline.addLast(new UserInfoServerHandler());
    }
}
