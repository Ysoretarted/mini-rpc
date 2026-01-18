package org.example.rpcdemo.consumer;

import java.util.concurrent.ExecutionException;

public class MyBusinessApplication {



    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Consumer consumer = new Consumer();
        System.out.println(consumer.add(1, 2));
    }
}
