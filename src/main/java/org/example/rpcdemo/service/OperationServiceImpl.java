package org.example.rpcdemo.service;

public class OperationServiceImpl implements OperationService{
    @Override
    public int add(int a, int b) {
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
