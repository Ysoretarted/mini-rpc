package org.example.rpcdemo.exception;

public class RpcException extends RuntimeException {
    public RpcException(String message) {
        super(message);
    }
}
