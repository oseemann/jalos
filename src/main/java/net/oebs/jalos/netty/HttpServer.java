package net.oebs.jalos.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import net.oebs.jalos.Settings;
import net.oebs.jalos.db.Backend;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpServer {

    private final Backend db;
    private final Integer port;
    private final String host;

    static final Logger log = LogManager.getLogger();

    public HttpServer(Backend db, Settings settings) {
        this.db = db;
        this.port = settings.getHttpPort();
        this.host = settings.getHttpHost();
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 8192);
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HttpInitializer(db));
            Channel ch = b.bind(host, port).sync().channel();
            log.info("Listening at http://%s:%d/", host, port);
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
