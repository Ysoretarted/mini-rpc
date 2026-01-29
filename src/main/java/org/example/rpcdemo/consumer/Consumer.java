package org.example.rpcdemo.consumer;

import lombok.extern.slf4j.Slf4j;
import org.example.rpcdemo.service.OperationService;

@Slf4j
public class Consumer implements OperationService {

    public Consumer() {

    }

    public int add(int a, int b) {
        //自己的业务逻辑
        log.info("org.example.rpcdemo.consumer.Consumer.add 被调用了");
        return -9999;
    }

    @Override
    public int minus(int a, int b) {
        log.info("org.example.rpcdemo.consumer.Consumer.minus 被调用了");
        return 9999;
    }

}
