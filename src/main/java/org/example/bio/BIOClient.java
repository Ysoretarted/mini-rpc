package org.example.bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class BIOClient {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("localhost", 8088));
        OutputStream outputStream = socket.getOutputStream();
        for(int i = 0; i < 10; ++i){
            String msgContent = "hello_" + i;
            outputStream.write(msgContent.getBytes());
            outputStream.flush();
        }
        socket.close();
        System.out.println("发送完成");
    }
}
