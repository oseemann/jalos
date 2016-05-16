/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.oebs.jalos.handler;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import static io.netty.handler.codec.http.HttpHeaders.Names.LOCATION;
import io.netty.handler.codec.http.HttpResponseStatus;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import java.util.Map;
import net.oebs.jalos.handler.errors.BadRequest;
import net.oebs.jalos.handler.errors.HandlerError;

public class RedirectHandler extends Handler {

    protected String location = "";
    protected HttpResponseStatus status = HttpResponseStatus.FOUND;

    @Override
    public FullHttpResponse handleGet(String uri, Map<String, String> params) throws HandlerError {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status);
        response.headers().set(LOCATION, location);
        return response;
    }

    @Override
    public FullHttpResponse handlePost(String uri, Map<String, String> params) throws HandlerError {
        throw new BadRequest();
    }

}
