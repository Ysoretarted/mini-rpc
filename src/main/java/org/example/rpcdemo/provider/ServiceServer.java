package org.example.rpcdemo.provider;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.example.rpcdemo.codec.ZZDecoder;
import org.example.rpcdemo.codec.ZZResponseEncoder;
import org.example.rpcdemo.message.Request;
import org.example.rpcdemo.message.Response;
import org.example.rpcdemo.properties.ServerProperties;

@Slf4j
public class ServiceServer {
    private final int port;

    private final EventLoopGroup bossEventLoopGroup;

    private final EventLoopGroup workEventLoopGroup;

    private final ProviderRegistry providerRegistry;

    public ServiceServer(ServerProperties properties) {
        this.port = properties.getPort();
        this.bossEventLoopGroup = new NioEventLoopGroup();
        this.workEventLoopGroup = new NioEventLoopGroup(4);
        this.providerRegistry = new ProviderRegistry();
    }

    public <I> void registry(Class<I> interfaceClass, I serviceInstance){
        providerRegistry.registry(interfaceClass, serviceInstance);
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
//                                .addLast(new LineBasedFrameDecoder(1024))
                                .addLast(new ZZDecoder())
                                .addLast(new ZZResponseEncoder())
                                .addLast(new ProviderHandler());
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

    public class ProviderHandler extends SimpleChannelInboundHandler<Request>{

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Request request) throws Exception {
            ProviderRegistry.InvocationWrapper<?> service = providerRegistry.getService(request.getServiceName());
            if(service == null){
                log.error("接口没找到:{}", request.getServiceName());
                Response fail = Response.FAIL("接口没找到" + request.getServiceName());
                channelHandlerContext.channel().writeAndFlush(fail);
            }

            try {

                Object invoke = service.invoke(request.getMethodName(), request.getParamClass(), request.getParams());

                Response response = Response.SUCCESS(invoke);
                channelHandlerContext.channel().writeAndFlush(response);
            } catch (Exception e) {
                log.error("接口:{}运行失败,方法:{},参数:{}", request.getServiceName(),request.getMethodName(), request.getParams());
                Response fail = Response.FAIL("接口没找到" + request.getServiceName());
                channelHandlerContext.channel().writeAndFlush(fail);
            }
//          String[] split = message.split(",");
//          String method = split[0];
//          int a = Integer.parseInt(split[1]);
//          int b = Integer.parseInt(split[2]);
//          if (method.equals("add")) {
//              int result = add(a, b);
//              //这里要加换行
//              channelHandlerContext.writeAndFlush(result + "\n");
//          }

        }
    }

}
