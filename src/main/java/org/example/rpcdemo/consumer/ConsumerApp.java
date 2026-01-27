package org.example.rpcdemo.consumer;

import org.example.rpcdemo.properties.ConsumerProperties;

import java.util.concurrent.ExecutionException;

public class ConsumerApp {


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Consumer consumer = new Consumer(new ConsumerProperties());
        while(true){
            System.out.println(consumer.add(1, 2));
            Thread.sleep(1000);
        }


    }
}
