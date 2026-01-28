package org.example.rpcdemo.message;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public class Request {
    private static final AtomicInteger REQUEST_ID_COUNTER = new AtomicInteger(0);

    private Integer requestId = REQUEST_ID_COUNTER.getAndIncrement();

    private String serviceName;

    private String methodName;

    private Class<?>[] paramClass;

    private Object[] params;
}
