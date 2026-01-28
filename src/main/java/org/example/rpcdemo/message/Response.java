package org.example.rpcdemo.message;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public class Response {
    private Integer requestId;

    private Object result;

    private int code;

    private String errorMsg;


    public static Response FAIL(Integer requestId, String errorMsg){
        Response response = new Response();

        response.setRequestId(requestId);
        response.setCode(400);
        response.setErrorMsg(errorMsg);
        return response;
    }

    public static Response SUCCESS(Integer requestId, Object result){
        Response response = new Response();

        response.setRequestId(requestId);
        response.setCode(200);
        response.setResult(result);
        return response;
    }
}
