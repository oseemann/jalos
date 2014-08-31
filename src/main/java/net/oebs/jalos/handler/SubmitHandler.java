package net.oebs.jalos.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.oebs.jalos.db.Backend;
import net.oebs.jalos.db.Url;

public class SubmitHandler implements Handler {

    SubmitResponseObject sro = null;
    static long c = 1000;

    public SubmitHandler(Backend db, List<InterfaceHttpData> params) {
        SubmitArgs args = readParams(params);
        Url url = new Url();
        url.setId(c++);
        url.setUrl(args.getUrl());
        db.store(url);
        sro = new SubmitResponseObject();
        sro.id = url.getId().toString();
        sro.target = url.getUrl();
        sro.url = "https://oebs.net/a/" + sro.id;
    }

    @Override
    public FullHttpResponse getResponse() {
        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(sro);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(SubmitHandler.class.getName()).log(Level.SEVERE, null, ex);
            json = "{\"status\": \"INTERNAL_ERROR\"}";
        }
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK,
                Unpooled.copiedBuffer(json, CharsetUtil.UTF_8));
        return response;
    }

    private SubmitArgs readParams(List<InterfaceHttpData> params) {
        String url = null;
        for (InterfaceHttpData param : params) {
            if (param.getName().compareTo("url") == 0) {
                url = param.toString();
            }
        }
        return new SubmitArgs(url);
    }

}
