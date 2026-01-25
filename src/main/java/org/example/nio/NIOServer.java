package org.example.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        Selector selector = Selector.open();

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress("localhost", 8088));
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        int cnt = 0;
        while (true) {
            cnt++;
            //监听感兴趣的事件，  阻塞函数
//            System.out.println("========等待感兴趣的事件=========" + System.currentTimeMillis() / 1000);
            selector.select();
//            System.out.println("========感兴趣的事件来了=========" + System.currentTimeMillis() / 1000);
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
//                System.out.println("====================第" + cnt + "次===================， key" + selectionKey.toString());
                iterator.remove();//一定要移除，表示感兴趣的key处理了。  不然会报空指针。
                if (selectionKey.isAcceptable()) {
                    System.out.println("acceptable事件来了");
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                    SocketChannel client = serverSocketChannel.accept();  //这个不能重复掉，  只有客户端连接上服务端时才能调
                    System.out.println("客户端连接了，地址：" + client.getRemoteAddress());

                    //这里要设置成非阻塞
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ);
                }
                if (selectionKey.isReadable()) {
                    System.out.println("readable事件来了");
                    Thread.sleep(5000);
                    SocketChannel client = (SocketChannel) selectionKey.channel();

                    //4半包现象
                    //16 粘包现象
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    int length = client.read(byteBuffer);
                    if (length == -1) {
                        System.out.println("客户端断开连接, 客户端远程地址" +  client.getRemoteAddress());
                        client.close();
                    } else {
                        byteBuffer.flip();
                        //是remaining
                        byte[] buffer = new byte[byteBuffer.remaining()];
                        byteBuffer.get(buffer);
                        String message = new String(buffer);
                        System.out.println("收到消息:" + message);
                    }

                }

            }


        }
    }
}
