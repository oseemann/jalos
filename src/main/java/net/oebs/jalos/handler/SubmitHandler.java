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
import net.oebs.jalos.db.Backend;
import net.oebs.jalos.db.Url;
import net.oebs.jalos.db.errors.BackendError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SubmitHandler implements Handler {

    SubmitResponseObject sro = null;
    private static final Logger logger = LogManager.getLogger(SubmitHandler.class.getName());

    private final String jsonErrorResponse = "{\"status\": \"INTERNAL_ERROR\"}";

    public SubmitHandler(Backend db, List<InterfaceHttpData> params) {
        SubmitArgs args = readParams(params);
        Url url = new Url();
        url.setUrl(args.getUrl());
        Url result = null;
        try {
            result = db.store(url);
        } catch (BackendError e) {
        }

        sro = new SubmitResponseObject();
        if (result != null) {
            sro.status = "SUCCESS";
            sro.id = url.getId().toString();
            sro.target = url.getUrl();
            sro.url = "https://oebs.net/a/" + sro.id;
        } else {
            sro.status = "ERROR";
        }
    }

    @Override
    public FullHttpResponse getResponse() {
        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(sro);
        } catch (JsonProcessingException e) {
            logger.error("JSON Error: {}", e);
            json = jsonErrorResponse;
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
