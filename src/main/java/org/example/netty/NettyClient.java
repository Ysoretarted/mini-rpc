package org.example.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NettyClient {

    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup());
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline()
                        .addLast(new LineBasedFrameDecoder(1024))
                        .addLast(new MyStringEncoder())
                        .addLast(new StringDecoder())
                        .addLast(new SimpleChannelInboundHandler<String>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                System.out.println("客户端收到了服务端的消息：" + msg);
                            }

                        });
            }
        });

        ChannelFuture connect = bootstrap.connect("localhost", 8088);
        connect.addListener(f->{
            if (f.isSuccess()) {
                System.out.println("成功能连接了服务器");
//                connect.channel().writeAndFlush("这是zcz第一次手写netty_");
                connect.channel().eventLoop().scheduleAtFixedRate(
                        ()-> connect.channel().writeAndFlush("这是zcz第一次手写netty_" + System.currentTimeMillis() + "\n"), 0, 1, TimeUnit.SECONDS);
            }else{
                System.out.println("连接服务器失败");
            }
        });
    }


    private static class MyStringEncoder extends StringEncoder{

        @Override
        protected void encode(ChannelHandlerContext ctx, CharSequence msg, List<Object> out) throws Exception {
            System.out.println("MyStringEncoder开始处理消息");
            super.encode(ctx, msg, out);
            ctx.fireChannelReadComplete();
        }
    }
}
