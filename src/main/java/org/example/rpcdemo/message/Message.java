package org.example.rpcdemo.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Message{

    /**
     * 魔法数,校验作用
     */
    public static final byte[] MAGIC = "zhongzhong".getBytes(StandardCharsets.UTF_8);

    /**
     * 消息总长度
     */
    private int len;

    /**
     * 消息的魔法数
     */
    private byte[] magic;

    /**
     * 消息类型
     */
    private byte messageType;

    /**
     * 消息的数据本身
     */
    private byte[] body;


    public enum MessageType {
        REQUEST(1), RESPONSE(2);
        private final byte code;

        MessageType(int code) {
            this.code = (byte) code;
        }


        public byte getCode() {
            return code;
        }
    }
}
