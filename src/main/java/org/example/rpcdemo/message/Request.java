package org.example.rpcdemo.message;

import lombok.Data;

@Data
public class Request {

    private String requestId;

    private String serviceName;

    private String methodName;

    private String[] paramClass;

    private String[] params;
}
