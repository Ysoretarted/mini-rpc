package org.example.rpcdemo.consumer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.example.rpcdemo.codec.ZZDecoder;
import org.example.rpcdemo.codec.ZZRequestEncoder;
import org.example.rpcdemo.exception.RpcException;
import org.example.rpcdemo.manager.ConnectionManager;
import org.example.rpcdemo.message.Request;
import org.example.rpcdemo.message.Response;
import org.example.rpcdemo.properties.ConsumerProperties;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ConsumerFactory {

    private static final Map<Integer, CompletableFuture<Response>> inFlightReqMap = new ConcurrentHashMap<>();

    private static ConnectionManager connectionManager;

    private static ConsumerProperties consumerProperties;

    private static EventLoopGroup workEventLoopGroup;


    public ConsumerFactory() {
        consumerProperties = new ConsumerProperties();
        workEventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = buildBootStrap();
        connectionManager = new ConnectionManager(bootstrap);
    }


    public <I> I getProxy(Class<I> interfaceClass) {
        InvocationHandler h = (proxy, method, args) -> {
            if (method.getDeclaringClass() == Object.class) {
                if (method.getName().equals("toString")) {
                    return "Proxy Consumer " + proxy.getClass().getName();
                } else if (method.getName().equals("hashCode")) {
                    return System.identityHashCode(proxy);
                } else if (method.getName().equals("equals")) {
                    return proxy == args[0];
                }
                throw new RuntimeException("代理对象不支持方法: " + method.getName());
            }

            try {
                CompletableFuture<Response> addFuture = new CompletableFuture<>();

                Channel channel = connectionManager.getChannel(consumerProperties.getHost(), consumerProperties.getPort());
                if (channel == null) {
                    throw new RpcException("连接provider失败");
                }
                //这里也要加换行
                Request request = new Request();
                request.setServiceName(interfaceClass.getName());
//        request.setMethodName("privateAdd");
                request.setMethodName(method.getName());
                request.setParamClass(method.getParameterTypes());
                request.setParams(args);

//        channelFuture.channel().writeAndFlush("add," + a + "," + b + "\n");
                inFlightReqMap.put(request.getRequestId(), addFuture);
                channel.writeAndFlush(request).addListener(future -> {
                    if (future.isSuccess()) {

                    } else if (!future.isSuccess()) {
                        addFuture.completeExceptionally(future.cause());
                    }
                });
                log.info("客户端发送结束");
                //即使这里超时了， channelRead0还是会收到服务端的消息。  所以再这之后维护了一个在途请求
                Response response = addFuture.get(3, TimeUnit.SECONDS);

                log.info("客户端拿到结果_{}", response);
                return response.getResult();
            } catch (RpcException rpcException) {
                throw rpcException;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        return (I)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{interfaceClass}, h);


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
                                        CompletableFuture<Response> requestFuture = inFlightReqMap.remove(response.getRequestId());
                                        if (requestFuture == null) {
                                            log.warn("收到未知的response: {}", response);
                                            return;
                                        }
                                        log.info("客户端收到服务端消息：{}", response);
                                        if (response.getCode() != 200) {
                                            log.warn("服务端运行失败");
                                            requestFuture.completeExceptionally(new RpcException(response.getErrorMsg()));
                                        } else {
                                            requestFuture.complete(response);
                                        }

                                        //关掉之后才会复用链接
//                                        channelHandlerContext.channel().close(); //通信完成后得关掉。    这里每一次链接都是新的channel

                                    }
                                });
                    }
                });
        return bootstrap;
    }
}
