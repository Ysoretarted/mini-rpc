package org.example.rpcdemo.codec;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.json.JsonObjectDecoder;
import lombok.extern.slf4j.Slf4j;
import org.example.rpcdemo.message.Message;
import org.example.rpcdemo.message.Request;

import java.util.List;

@Slf4j
public class ZZRequestEncoder extends MessageToByteEncoder<Request> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Request request, ByteBuf out) throws Exception {
        log.info("ZZRequestEncoder start encode");
        //协议：  len + magic + type + body

        byte[] magic = Message.MAGIC;
        byte type = Message.MessageType.REQUEST.getCode();
        byte[] body = serializeRequest(request);

        int len = magic.length + Byte.BYTES + body.length;
        out.writeInt(len);
        out.writeBytes(magic);
        out.writeByte(type);
        out.writeBytes(body);
    }

    private byte[] serializeRequest(Request request) {
        return JSONObject.toJSONString(request).getBytes();
    }
}
