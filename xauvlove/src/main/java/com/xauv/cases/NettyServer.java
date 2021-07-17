package com.xauv.cases;

/*
       /\   /\             /\.__                      
___  __)/___)/  __ _____  _)/|  |   _______  __ ____  
\  \/  /\__  \ |  |  \  \/ / |  |  /  _ \  \/ // __ \ 
 >    <  / __ \|  |  /\   /  |  |_(  <_> )   /\  ___/ 
/__/\_ \(____  /____/  \_/   |____/\____/ \_/  \___  >
      \/     \/                                    \/
*/

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Date 2021/06/19 21:19
 * @Author ling yue
 * @Package com.xauv.socket.io.netty.nio.cases.simple.netty.nio.cases
 * @Desc
 */
public class NettyServer {

    public static void main(String[] args) throws InterruptedException {

        // 创建 Boss Group，也即 Main Reactor，只处理连接请求，会无限循环
        // 创建 worker group，处理 read write 事件，用于业务处理，会无限循环
        // 默认，NioEventLoopGroup 的线程数量是 核心数量的2倍
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 创建服务器端 启动对象 配置参数
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    // 设置两个线程组
                    .group(bossGroup, workerGroup)
                    // 使用 NioServerSocketChannel类 作为服务器的 channel 实现
                    .channel(NioServerSocketChannel.class)
                    // 设置线程队列等待连接的个数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 设置保持活动连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 给 boss group 加 handler
                    //.handler(null)
                    // 给 worker group 的对应的管道 pipeline 设置处理器(handler)的初始化方式
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // 创建通道初始化对象
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            // 向 pipeline 最后增加处理器
                            // channel 初始化之后，往流水线 pipeline 添加一个处理器
                            channel.pipeline().addLast(new MyNettyServerHandler());
                        }
                    });
            System.out.println("服务器已经初始化完成");
            // 绑定一个端口 并且同步，生成一个 ChannelFuture 对象
            // ChannelFuture 是 netty 的异步模型
            ChannelFuture sync = serverBootstrap.bind(9100).sync();
            // 对关闭通道进行监听，当有关闭通道事件时，才会关闭
            sync.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}