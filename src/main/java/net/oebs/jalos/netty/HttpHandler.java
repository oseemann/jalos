
package net.oebs.jalos.netty;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.Values;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

public class HttpHandler extends ChannelInboundHandlerAdapter {

    private FullHttpResponse seeOther(String destinationUrl) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, SEE_OTHER);
        response.headers().set(LOCATION, destinationUrl);
        return response;
    }

    private FullHttpResponse handleSubmit(HttpRequest request) {
        FullHttpResponse response = null;
        return response;
    }

    private FullHttpResponse handleRequest(HttpRequest request) {

        FullHttpResponse response;
        String uri = request.getUri();

        if (uri.compareTo("/a/submit") == 0) {
            response = handleSubmit(request);
//        } else if (uri.compareTo("/status") == 0) {
        } else {
            response = seeOther("http://google.com/");
        }
        return response;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (!(msg instanceof HttpRequest)) {
            return;
        }

        HttpRequest req = (HttpRequest) msg;
        if (HttpHeaders.is100ContinueExpected(req)) {
            ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
        }

        FullHttpResponse response = handleRequest(req);
        response.headers().set(CONNECTION, Values.CLOSE);
        ctx.write(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
