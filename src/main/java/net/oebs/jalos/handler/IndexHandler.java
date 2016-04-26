/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.oebs.jalos.handler;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import net.oebs.jalos.handler.errors.BadRequest;
import net.oebs.jalos.handler.errors.HandlerError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexHandler extends Handler {

    static final Logger log = LoggerFactory.getLogger(SubmitHandler.class);

    static final Map<String, String> staticFiles = new HashMap();
    static final Map<String, String> contentTypes = new HashMap();

    static {
        staticFiles.put("/a/", "staticfiles/index.html");
        staticFiles.put("/a/static/jalos.css", "staticfiles/jalos.css");
        staticFiles.put("/a/static/jalos.js", "staticfiles/jalos.js");

        contentTypes.put("html", "text/html");
        contentTypes.put("css", "text/css");
        contentTypes.put("js", "application/javascript");
    }

    private File uriToFile(String uri) {
        String resource = staticFiles.get(uri);
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(classLoader.getResource(resource).getFile());
    }

    @Override
    public FullHttpResponse handleGet(String uri, Map<String, String> params) throws HandlerError {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);

        log.debug(uri);
        File file = uriToFile(uri);
        String fname = file.getName();
        String contentType = contentTypes.get(fname.substring(fname.lastIndexOf(".")));
        response.headers().set(CONTENT_TYPE, contentType + "; charset=UTF-8");

        try {
            response.content().writeBytes(Files.readAllBytes(file.toPath()));
        } catch (IOException ex) {
        }
        return response;
    }

    @Override
    public FullHttpResponse handlePost(String uri, Map<String, String> params) throws HandlerError {
        throw new BadRequest();
    }

}
