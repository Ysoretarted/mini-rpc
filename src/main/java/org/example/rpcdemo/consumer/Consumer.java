package org.example.rpcdemo.consumer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.example.rpcdemo.codec.ZZDecoder;
import org.example.rpcdemo.codec.ZZRequestEncoder;
import org.example.rpcdemo.exception.RpcException;
import org.example.rpcdemo.message.Request;
import org.example.rpcdemo.message.Response;
import org.example.rpcdemo.properties.ConsumerProperties;
import org.example.rpcdemo.service.OperationService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public class Consumer {
    private final String host;

    private final int port;

    private final EventLoopGroup workEventLoopGroup;

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
//                                .addLast(new LineBasedFrameDecoder(1024))
                                .addLast(new ZZDecoder())
                                .addLast(new ZZRequestEncoder())
                                .addLast(new SimpleChannelInboundHandler<Response>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Response response) throws Exception {
                                        log.info("客户端收到服务端消息：{}", response);
                                        if (response.getCode() != 200) {
                                            log.warn("服务端运行失败");
                                            addFuture.completeExceptionally(new RpcException("服务运行失败"));
                                        } else {
                                            addFuture.complete((Integer) response.getResult());
                                        }

                                    }
                                });
                    }
                });

        log.info("客户端开始连接111");
        ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
        log.info("客户端成功连接222");
        //这里也要加换行
        Request request = new Request();
        request.setServiceName(OperationService.class.getName());
//        request.setMethodName("privateAdd");
        request.setMethodName("add");
        request.setParamClass(new Class[]{int.class, int.class});
        request.setParams(new Object[]{a, b});

//        channelFuture.channel().writeAndFlush("add," + a + "," + b + "\n");
        channelFuture.channel().writeAndFlush(request);
        log.info("客户端发送结束");
        Integer i = addFuture.get();
        log.info("客户端拿到结果_{}", i);
        return i;
    }

}
