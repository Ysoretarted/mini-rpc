package org.example.bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

public class BIOClient {

    public static void main(String[] args) throws IOException, InterruptedException {
        Thread tomThread = new Thread(() -> {
            try {
                sendHello();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "tom");
        Thread zczThread = new Thread(() -> {
            try {
                sendHello();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "zcz");
        tomThread.start();
        zczThread.start();
        tomThread.join();;
        zczThread.join();

    }

    private static void sendHello() throws IOException, InterruptedException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("localhost", 8088));
        OutputStream outputStream = socket.getOutputStream();
        for(int i = 0; i < 10; ++i){
            String msgContent = Thread.currentThread().getName() + "_hello_" + i;
            outputStream.write(msgContent.getBytes());
            outputStream.flush();
        }
        Thread.sleep(10000);
        socket.close();
        System.out.println("发送完成");
    }
}
