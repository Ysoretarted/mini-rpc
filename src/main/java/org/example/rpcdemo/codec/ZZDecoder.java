package org.example.rpcdemo.codec;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import org.example.rpcdemo.message.Message;
import org.example.rpcdemo.message.Request;
import org.example.rpcdemo.message.Response;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
public class ZZDecoder extends LengthFieldBasedFrameDecoder {

    //这个构造函数很重要
    public ZZDecoder() {
        super(1024 * 1024, 0, Integer.BYTES, 0, Integer.BYTES);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        log.info("ZZDecoder start decode");
        System.out.println("ZZDecoder start decode");
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);


        byte[] magic = new byte[Message.MAGIC.length];
        frame.readBytes(magic);

        //这个为啥不行
//        if(Objects.equals(magic, Message.MAGIC)){
        if (!Arrays.equals(magic, Message.MAGIC)) {
            throw new IllegalArgumentException("魔术不对,协议不支持");
        }
        byte messageType = frame.readByte();

        byte[] body = new byte[frame.readableBytes()];
        frame.readBytes(body);

        if (Objects.equals(Message.MessageType.REQUEST.getCode(), messageType)) {
            return deserializeRequest(body);
        } else if (Objects.equals(Message.MessageType.RESPONSE.getCode(), messageType)) {
            return deserializeResponse(body);
        }
        throw new IllegalArgumentException("不支持的消息类型");
    }

    private Response deserializeResponse(byte[] body) {
        return JSONObject.parseObject(new String(body), Response.class);

    }

    private Request deserializeRequest(byte[] body) {
        return JSONObject.parseObject(new String(body), Request.class);
    }
}
