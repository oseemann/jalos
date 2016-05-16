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
package net.oebs.jalos.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.util.CharsetUtil;
import java.util.Map;
import net.oebs.jalos.RuntimeContext;
import net.oebs.jalos.Settings;
import net.oebs.jalos.db.Backend;
import net.oebs.jalos.db.Url;
import net.oebs.jalos.db.errors.BackendError;
import net.oebs.jalos.handler.errors.BadRequest;
import net.oebs.jalos.handler.errors.HandlerError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubmitHandler extends Handler {

    static final Logger log = LoggerFactory.getLogger(SubmitHandler.class);

    private final String jsonErrorResponse = "{\"status\": \"INTERNAL_ERROR\"}";

    @Override
    public FullHttpResponse handleGet(String uri, Map<String, String> params) throws HandlerError {
        throw new BadRequest();
    }

    @Override
    public FullHttpResponse handlePost(String uri, Map<String, String> params) {
        Url url = new Url(params.get("url"));
        Url result = null;
        Backend db = RuntimeContext.getInstance().getBackend();
        Settings settings = RuntimeContext.getInstance().getSettings();
        try {
            result = db.store(url);
        } catch (BackendError e) {
            // TODO
        }

        SubmitResponseObject sro = new SubmitResponseObject();
        if (result != null) {
            sro.status = "SUCCESS";
            sro.id = result.getId();
            sro.target = result.getUrl();
            sro.url = settings.getHttpHostUrl().toString() + sro.id;
        } else {
            sro.status = "ERROR";
        }

        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(sro);
        } catch (JsonProcessingException e) {
            log.error("JSON Error: {}", e);
            json = jsonErrorResponse;
        }
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK,
                Unpooled.copiedBuffer(json, CharsetUtil.UTF_8));
        response.headers().set("Content-Type", "application/json");
        return response;
    }

}
