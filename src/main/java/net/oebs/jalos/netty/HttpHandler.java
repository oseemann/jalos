package net.oebs.jalos.netty;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.LOCATION;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.SEE_OTHER;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import java.util.List;
import net.oebs.jalos.db.Backend;
import net.oebs.jalos.handler.LookupHandler;
import net.oebs.jalos.handler.SubmitHandler;
import net.oebs.jalos.handler.errors.HandlerError;
import net.oebs.jalos.handler.errors.NotFound;

public class HttpHandler extends SimpleChannelInboundHandler {

    Backend db;

    public HttpHandler(Backend db) {
        this.db = db;
    }

    private FullHttpResponse seeOther(String destinationUrl) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, SEE_OTHER);
        response.headers().set(LOCATION, destinationUrl);
        return response;
    }

    private FullHttpResponse notFound() {
        return new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND);
    }

    private FullHttpResponse badRequest() {
        return new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST);
    }

    private FullHttpResponse internalError() {
        return new DefaultFullHttpResponse(HTTP_1_1, INTERNAL_SERVER_ERROR);
    }

    private FullHttpResponse handleSubmit(FullHttpRequest request) {
        FullHttpResponse response;
        HttpMethod method = request.getMethod();
        if (method.equals(HttpMethod.POST)) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);
            List<InterfaceHttpData> params = decoder.getBodyHttpDatas();
            response = new SubmitHandler(db, params).getResponse();
        } else if (method.equals(HttpMethod.GET)) {
            response = badRequest();
        } else {
            response = badRequest();
        }

        return response;
    }

    private FullHttpResponse handleLookup(FullHttpRequest request) {
        FullHttpResponse response;
        try {
            response = new LookupHandler(db, request.getUri()).getResponse();
        } catch (NotFound e) {
            response = notFound();
        } catch (HandlerError e) {
            response = internalError();
        }
        return response;
    }

    private FullHttpResponse handleRequest(HttpRequest request) {

        FullHttpResponse response = null;
        String uri = request.getUri();

        if (uri.compareTo("/a/submit") == 0) {
            response = handleSubmit((FullHttpRequest) request);
        } else if (uri.startsWith("/a/")) {
            response = handleLookup((FullHttpRequest) request);
        } else if (uri.compareTo("/status") == 0) {
        } else {
            response = notFound();
        }
        return response;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
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
