package org.example.rpcdemo.codec;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.example.rpcdemo.message.Message;
import org.example.rpcdemo.message.Request;
import org.example.rpcdemo.message.Response;

public class ZZResponseEncoder extends MessageToByteEncoder<Response> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Response response, ByteBuf out) throws Exception {
        //协议：  len + magic + type + body

        byte[] magic = Message.MAGIC;
        byte type = Message.MessageType.RESPONSE.getCode();
        byte[] body = serializeRequest(response);

        int len = magic.length + Byte.BYTES + body.length;
        out.writeInt(len);
        out.writeBytes(magic);
        out.writeByte(type);
        out.writeBytes(body);
    }

    private byte[] serializeRequest(Response response) {
        return JSONObject.toJSONString(response).getBytes();
    }
}
