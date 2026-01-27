package org.example.rpcdemo.message;

import lombok.Data;

@Data
public class Response {
    private String requestId;

    private Object result;

    private int code;

    private String errorMsg;


    public static Response FAIL(String errorMsg){
        Response response = new Response();

        response.setCode(400);
        response.setErrorMsg(errorMsg);
        return response;
    }

    public static Response SUCCESS(Object result){
        Response response = new Response();

        response.setCode(200);
        response.setResult(result);
        return response;
    }
}
