package com.ljh.gamedemo.server;

import com.ljh.gamedemo.module.base.handler.DispatcherHandler;
import com.ljh.gamedemo.server.codec.CustomProtobufDecoder;
import com.ljh.gamedemo.server.codec.CustomProtobufEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

@Component
@Slf4j
public class NettyServer {

    private EventLoopGroup boss = new NioEventLoopGroup();
    private EventLoopGroup worker = new NioEventLoopGroup();

    private  EventExecutorGroup businessGroup = new DefaultEventExecutorGroup(2);

    @Value("${netty.port}")
    private Integer port;

    private SocketChannel socketChannel;

    // 通过 postConstruct 启动
    @PostConstruct
    public void start() throws InterruptedException{

        ServerBootstrap b = new ServerBootstrap();
        b.group(boss, worker)
                // 指定 接收的channel
                .channel(NioServerSocketChannel.class)

                // 指定端口
                .localAddress(new InetSocketAddress(port))

                // option() childOption() -> 前者适用于服务端接受连接的NioServerSocketChannel，
                //                            后者适用于对每个客户的连接新创建的 Channel  （一个处理boss, 一个worker）
                // 服务端可连接队列数，对应TCP/IP协议中大的 listen函数中的backlog
                .option(ChannelOption.SO_BACKLOG, 1024)

                // 设置TCP 长连接，一般如果两个小时内没有数据的通信，TCP会自动发送一个活动探测数据报文
                .childOption(ChannelOption.SO_KEEPALIVE, true)

                // 将小的数据包包装成更大的帧进行传送，提高网络的负载，即TCP延迟传输(Nagle算法)
                .childOption(ChannelOption.TCP_NODELAY, true)

                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();

                        pipeline.addLast(new ServerIdleStateHandler());

                        // 添加自定义编码解码器
                        pipeline.addLast("decoder",new CustomProtobufDecoder());
                        pipeline.addLast("encoder",new CustomProtobufEncoder());

                        // 添加业务线程池，处理耗时任务
                        pipeline.addLast(businessGroup, DispatcherHandler.INSTANCE);
                    }
                });

        ChannelFuture channelFuture = b.bind().sync();
        if (channelFuture.isSuccess()){
            log.info("启动 Netty Server");
        }
    }


    @PreDestroy
    public void destory() throws InterruptedException{
        boss.shutdownGracefully().sync();
        worker.shutdownGracefully().sync();

        log.info("关闭 Netty");
    }
}
