package org.example.rpcdemo.provider;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import org.example.rpcdemo.properties.ServerProperties;

@Slf4j
public class ServiceServer {
    private final int port;

    private final EventLoopGroup bossEventLoopGroup;

    private final EventLoopGroup workEventLoopGroup;

    public ServiceServer(ServerProperties properties) {
        this.port = properties.getPort();
        this.bossEventLoopGroup = new NioEventLoopGroup();
        this.workEventLoopGroup = new NioEventLoopGroup(4);
    }

    public void start() throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossEventLoopGroup, workEventLoopGroup)
                .channel(NioServerSocketChannel.class)
                //这里是childHandler
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioServerSocketChannel) throws Exception {
                        nioServerSocketChannel.pipeline()
                                .addLast(new LineBasedFrameDecoder(1024))
                                .addLast(new StringDecoder())
                                .addLast(new StringEncoder())
                                .addLast(new SimpleChannelInboundHandler<String>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String message) throws Exception {
                                        log.info("收到消息：" + message);
                                        String[] split = message.split(",");
                                        String method = split[0];
                                        int a = Integer.parseInt(split[1]);
                                        int b = Integer.parseInt(split[2]);
                                        if (method.equals("add")) {
                                            int result = add(a, b);
                                            //这里要加换行
                                            channelHandlerContext.writeAndFlush(result + "\n");
                                        }

                                    }
                                });
                    }
                });

        serverBootstrap.bind(port).sync();
    }

    public void shutdown() {
        if (bossEventLoopGroup != null) {
            bossEventLoopGroup.shutdownGracefully();
        }
        if (workEventLoopGroup != null) {
            workEventLoopGroup.shutdownGracefully();
        }
    }

    private static int add(int a, int b) {
        return a + b;
    }
}
