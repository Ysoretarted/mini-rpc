package org.example.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * bio 一个线程处理一个链接。  本质：read是阻塞的
 */
public class BIOServerSocket {


    public static void main(String[] args) throws IOException {
        Socket socket;
        try (ServerSocket serverSocket = new ServerSocket(8088)) {
            while (true) {
                System.out.println("==========等待连接==============");
                socket = serverSocket.accept();
                InputStream inputStream = socket.getInputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    String message = new String(buffer, 0, length);
                    System.out.println(message);
                }

                System.out.println("客户端断开连接");
                System.out.println();
            }
        }


    }
}
