package org.example.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NettyServer {

    public static void main(String[] args) {
        Map<Channel, List<String>> db = new ConcurrentHashMap<>(64);

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup(4));
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline()
                        .addLast(new LineBasedFrameDecoder(1024))
                        .addLast(new StringDecoder())
                        .addLast(new SimpleChannelInboundHandler<String>() {
                            @Override
                            public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                                System.out.println(ctx.channel() + "注册了" + "_第一个SimpleChannelInboundHandler");
                                ctx.fireChannelRegistered();
                            }

                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                System.out.println("服务端收到了消息:" + msg);

                                String message = msg + "_加油,真棒!!" + "\n";
                                ctx.channel().writeAndFlush(message);

                                //channelRead0事件的传播
                                ctx.fireChannelRead(msg);
                            }
                        })
                        .addLast(new SimpleChannelInboundHandler<String>() {
                            @Override
                            public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                                System.out.println(ctx.channel() + "注册了");
                            }

                            @Override
                            public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
                                System.out.println(ctx.channel() + "解除注册了");
                            }

                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                System.out.println(ctx.channel() + "可以用了");
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                System.out.println(ctx.channel() + "_inActive了");
                                System.out.println(db.get(ctx.channel()));
                            }

                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

                                List<String> msgList = db.computeIfAbsent(ctx.channel(), k -> new ArrayList<>());
                                msgList.add(msg);
                            }
                        })
                        .addLast(new StringEncoder());
            }
        });

        ChannelFuture channelFuture = serverBootstrap.bind(8088);
        channelFuture.addListener(f -> {
            if (f.isSuccess()) {
                System.out.println("服务端成功注册端口");
            } else {
                System.out.println("服务端监听端口失败");
            }
        });
//
    }
}
