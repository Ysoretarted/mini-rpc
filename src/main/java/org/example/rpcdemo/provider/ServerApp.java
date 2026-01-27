package org.example.rpcdemo.provider;

import org.example.rpcdemo.properties.ServerProperties;
import org.example.rpcdemo.service.OperationService;
import org.example.rpcdemo.service.OperationServiceImpl;

public class ServerApp {

    public static void main(String[] args) throws InterruptedException {
        ServiceServer serviceServer = new ServiceServer(new ServerProperties());
        //服务端的接口注册到注册中心
        serviceServer.registry(OperationService.class, new OperationServiceImpl());
        serviceServer.start();
    }
}
