/*
 * Copyright (c) 2014 Oliver Seemann
 *
 * This file is part of Jalos.
 *
 * Jalos is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Jalos is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Jalos.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.oebs.jalos.netty;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.oebs.jalos.handler.Handler;
import net.oebs.jalos.handler.IndexHandler;
import net.oebs.jalos.handler.LookupHandler;
import net.oebs.jalos.handler.RedirectHandler;
import net.oebs.jalos.handler.SubmitHandler;
import net.oebs.jalos.handler.errors.BadRequest;
import net.oebs.jalos.handler.errors.HandlerError;
import net.oebs.jalos.handler.errors.NotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpHandler extends SimpleChannelInboundHandler {

    static final Logger log = LoggerFactory.getLogger(HttpHandler.class);

    private static class Route {

        public final String path;
        public final Class handler;

        public Route(String path, Class handler) {
            this.path = path;
            this.handler = handler;
        }
    }

    private final static List<Route> routes = new ArrayList<>(Arrays.asList(
            new Route("/a/submit", SubmitHandler.class),
            new Route("/a/", IndexHandler.class),
            new Route("/a/static/.*", IndexHandler.class),
            new Route("/a/\\d+", LookupHandler.class),
            new Route("/", (new RedirectHandler() {
                {
                    location = "/a/";
                    status = SEE_OTHER;
                }
            }).getClass())
    ));

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

    private Map<String, String> httpDataToStringMap(List<InterfaceHttpData> params) {
        Map<String, String> result = new HashMap<>();
        for (InterfaceHttpData param : params) {
            String[] x = param.toString().split("=");
            result.put(x[0], x[1]);
        }
        return result;
    }

    private FullHttpResponse handleRequest(HttpRequest request) {

        FullHttpResponse response = null;
        String uri = request.getUri();

        // lookup Handler class by URL
        Class cls = null;
        for (Route route : routes) {
            if (uri.matches(route.path)) {
                cls = route.handler;
                break;
            }
        }

        // no matching handler == 404
        if (cls == null) {
            log.info("No handler match found for uri %s", uri);
            return notFound();
        }

        Handler h;
        try {
            h = (Handler) cls.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            return internalError();
        }

        Map<String, String> params = null;
        HttpMethod method = request.getMethod();

        // dispatch based on request method
        try {
            if (method.equals(HttpMethod.POST)) {
                HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);
                params = httpDataToStringMap(decoder.getBodyHttpDatas());
                response = h.handlePost(uri, params);
            } else if (method.equals(HttpMethod.GET)) {
                params = new HashMap<>();
                response = h.handleGet(uri, params);
            } else {
                response = badRequest();
            }

        } catch (BadRequest ex) {
            response = badRequest();
        } catch (NotFound ex) {
            response = notFound();
        } catch (HandlerError ex) {
            response = internalError();
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
