package org.example.rpcdemo.provider;

import org.example.rpcdemo.properties.ServerProperties;

public class ServerApp {

    public static void main(String[] args) throws InterruptedException {
        ServiceServer serviceServer = new ServiceServer(new ServerProperties());
        serviceServer.start();
    }
}
