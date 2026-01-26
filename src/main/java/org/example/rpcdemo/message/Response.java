package org.example.rpcdemo.message;

import lombok.Data;

@Data
public class Response {
    private String requestId;

    private Object result;
}
