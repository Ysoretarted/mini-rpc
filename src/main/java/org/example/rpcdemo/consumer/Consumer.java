package org.example.rpcdemo.consumer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.example.rpcdemo.codec.ZZDecoder;
import org.example.rpcdemo.codec.ZZRequestEncoder;
import org.example.rpcdemo.exception.RpcException;
import org.example.rpcdemo.manager.ConnectManager;
import org.example.rpcdemo.message.Request;
import org.example.rpcdemo.message.Response;
import org.example.rpcdemo.properties.ConsumerProperties;
import org.example.rpcdemo.service.OperationService;

import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class Consumer {
    private final Map<Integer, CompletableFuture<?>> inFlightReqMap = new ConcurrentHashMap<>();

    private final String host;

    private final int port;

    private final EventLoopGroup workEventLoopGroup;

    private final ConnectManager connectManager;

    public Consumer(ConsumerProperties properties) {
        this.host = properties.getHost();
        this.port = properties.getPort();
        this.workEventLoopGroup = new NioEventLoopGroup();
        this.connectManager = new ConnectManager(buildBootStrap());
    }

    private Bootstrap buildBootStrap() {
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
                                        CompletableFuture requestFuture = inFlightReqMap.remove(response.getRequestId());
                                        if (requestFuture == null) {
                                            log.warn("收到未知的response: {}", response);
                                            return;
                                        }
                                        log.info("客户端收到服务端消息：{}", response);
                                        if (response.getCode() != 200) {
                                            log.warn("服务端运行失败");
                                            requestFuture.completeExceptionally(new RpcException(response.getErrorMsg()));
                                        } else {
                                            requestFuture.complete(response.getResult());
                                        }

                                        //关掉之后才会复用链接
//                                        channelHandlerContext.channel().close(); //通信完成后得关掉。    这里每一次链接都是新的channel

                                    }
                                });
                    }
                });
        return bootstrap;
    }


    public int add(int a, int b) throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture<Integer> addFuture = new CompletableFuture<>();

        Channel channel = connectManager.getChannel(host, port);
        if (channel == null) {
            throw new RpcException("连接provider失败");
        }
        //这里也要加换行
        Request request = new Request();
        request.setServiceName(OperationService.class.getName());
//        request.setMethodName("privateAdd");
        request.setMethodName("add");
        request.setParamClass(new Class[]{int.class, int.class});
        request.setParams(new Object[]{a, b});

//        channelFuture.channel().writeAndFlush("add," + a + "," + b + "\n");
        channel.writeAndFlush(request).addListener(future -> {
            if (future.isSuccess()) {
                inFlightReqMap.put(request.getRequestId(), addFuture);
            }
//            else {
//
//                addFuture.completeExceptionally(future.cause());
//            }
        });
        log.info("客户端发送结束");
        //即使这里超时了， channelRead0还是会收到服务端的消息。  所以再这之后维护了一个在途请求
        Integer i = addFuture.get(3, TimeUnit.SECONDS);
        log.info("客户端拿到结果_{}", i);
        return i;
    }

}
