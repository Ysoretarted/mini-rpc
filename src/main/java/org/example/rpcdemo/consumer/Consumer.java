package org.example.rpcdemo.consumer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.example.rpcdemo.properties.ConsumerProperties;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Consumer {
    private String host;

    private int port;

    private EventLoopGroup workEventLoopGroup;

    public Consumer(ConsumerProperties properties) {

        this.host = properties.getHost();
        this.port = properties.getPort();
        this.workEventLoopGroup = new NioEventLoopGroup();
    }

    public int add(int a, int b) throws InterruptedException, ExecutionException {
        CompletableFuture<Integer> addFuture = new CompletableFuture<>();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline()
                                .addLast(new LineBasedFrameDecoder(1024))
                                .addLast(new StringDecoder())
                                .addLast(new StringEncoder())
                                .addLast(new SimpleChannelInboundHandler<String>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String message) throws Exception {
                                        int result = Integer.parseInt(message);
                                        addFuture.complete(result);

                                        //关闭
                                        channelHandlerContext.close();
                                    }
                                });
                    }
                });

        ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
        //这里也要加换行
        channelFuture.channel().writeAndFlush("add," + a + "," + b + "\n");

        return addFuture.get();
    }

}
