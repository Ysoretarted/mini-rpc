package org.example.rpcdemo.properties;

import lombok.Data;

@Data
public class ConsumerProperties {

    private String host = "localhost";
    private int port = 8089;
}
