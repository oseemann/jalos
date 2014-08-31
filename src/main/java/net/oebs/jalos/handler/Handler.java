package net.oebs.jalos.handler;

import io.netty.handler.codec.http.FullHttpResponse;

public interface Handler {

    public FullHttpResponse getResponse();
}
