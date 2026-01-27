package org.example.rpcdemo.service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class OperationServiceImpl implements OperationService{
    @Override
    public int add(int a, int b) {
        //服务端响应超时测试
//        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(10));
        return a + b;
    }

    @Override
    public int minus(int a, int b) {
        return a - b;
    }

    private int privateAdd(int a, int b){
        return a * b;
    }
}
