package org.example.rpcdemo.consumer;

import lombok.extern.slf4j.Slf4j;
import org.example.rpcdemo.properties.ConsumerProperties;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Slf4j
public class ConsumerApp {


    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        Consumer consumer = new Consumer(new ConsumerProperties());

        for(int i = 0; i < 2; i++){
            buildThread(consumer).start();
        }
    }

    private static Thread buildThread(Consumer consumer) {
        return new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    System.out.println(i + "+" + 2 * i + "=" + consumer.add(i, 2 * i));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (TimeoutException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
