package com.xauv.cases;

/*
       /\   /\             /\.__                      
___  __)/___)/  __ _____  _)/|  |   _______  __ ____  
\  \/  /\__  \ |  |  \  \/ / |  |  /  _ \  \/ // __ \ 
 >    <  / __ \|  |  /\   /  |  |_(  <_> )   /\  ___/ 
/__/\_ \(____  /____/  \_/   |____/\____/ \_/  \___  >
      \/     \/                                    \/
*/

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @Date 2021/06/19 21:55
 * @Author ling yue
 * @Package com.xauv.socket.io.netty.nio.cases
 * @Desc
 */
public class NettyClient {

    public static void main(String[] args) throws InterruptedException {
        // 客户端只需要一个事件循环组即可
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            // 客户端使用 Bootstrap，而不是 ServerBootstrap
            Bootstrap bootstrap = new Bootstrap();
            // 设置相关参数
            bootstrap
                // 设置线程组
                .group(eventLoopGroup)
                // 设置客户端通道实现类
                .channel(NioSocketChannel.class)
                .remoteAddress("localhost", 9100)
                // 客户端也是用 通道 方式来处理各种事件，因此也需要一个 通道 初始化器
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // 当然，客户端的一系列操作和事件也是经过 流水线 处理
                        ch.pipeline().addLast(new MyNettyClientHandler());
                    }
                });

            System.out.println("客户端已经准备连接了...");
            ChannelFuture sync = bootstrap.connect().sync();
            // 给关闭通道增加监听
            sync.channel().closeFuture().sync();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
