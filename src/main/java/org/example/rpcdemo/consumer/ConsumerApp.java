package org.example.rpcdemo.consumer;

import lombok.extern.slf4j.Slf4j;
import org.example.rpcdemo.properties.ConsumerProperties;

import java.util.concurrent.ExecutionException;

@Slf4j
public class ConsumerApp {


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Consumer consumer = new Consumer(new ConsumerProperties());
        while(true){
            System.out.println(consumer.add(7, 2));
            Thread.sleep(1000);
        }


    }
}
