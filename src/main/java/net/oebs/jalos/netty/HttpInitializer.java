package net.oebs.jalos.netty;

import com.sleepycat.persist.EntityStore;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class HttpInitializer extends ChannelInitializer<SocketChannel> {

    EntityStore db;

    public HttpInitializer(EntityStore db) {
        this.db = db;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpRequestDecoder());
        p.addLast(new HttpResponseEncoder());
        p.addLast(new HttpObjectAggregator(1048576));
        p.addLast(new HttpHandler(db));
    }
}
