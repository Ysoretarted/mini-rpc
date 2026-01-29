package org.example.rpcdemo.manager;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    private final Map<String, ChannelWrapper> channelMap = new ConcurrentHashMap<>();

    private final Bootstrap bootstrap;

    public ConnectionManager(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public Channel getChannel(String host, int port) {
        String key = host + ":" + port;
        ChannelWrapper channelWrapper = channelMap.computeIfAbsent(key, (hostPort) -> {
            try {
                Channel channel = bootstrap.connect(host, port).sync().channel();
                return new ChannelWrapper(channel);
            } catch (InterruptedException e) {
                return new ChannelWrapper(null);
            }
        });
        Channel channel = channelWrapper.channel;
        if(channel == null || !channel.isActive()){
            return null;
        }
        return channel;
    }

    @Getter
    public static class ChannelWrapper {
        private final Channel channel;

        public ChannelWrapper(Channel channel) {
            this.channel = channel;
        }

    }


}
