package org.example.rpcdemo.consumer;

import lombok.extern.slf4j.Slf4j;
import org.example.rpcdemo.service.OperationService;

@Slf4j
public class ConsumerApp {


    public static void main(String[] args) {

        ConsumerFactory factory = new ConsumerFactory();
        OperationService consumer = factory.getProxy(OperationService.class);

        for (int i = 0; i < 1; i++) {
            buildThread(consumer).start();
        }
    }

    private static Thread buildThread(OperationService consumer) {
        return new Thread(() -> {
            for (int i = 0; i < 100; i++) {
//                    System.out.println(i + "+" + 2 * i + "=" + consumer.add(i, 2 * i));
                System.out.println(i + "-" + 2 * i + "=" + consumer.minus(i, 2 * i));
            }
        });
    }
}
