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
import net.oebs.jalos.db.Backend;
import net.oebs.jalos.db.Url;
import net.oebs.jalos.db.errors.BackendError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SubmitHandler implements Handler {

    private final SubmitResponseObject sro;
    private static final Logger logger = LogManager.getLogger(SubmitHandler.class.getName());

    private final String jsonErrorResponse = "{\"status\": \"INTERNAL_ERROR\"}";

    public SubmitHandler(Backend db, Map<String, String> params) {
        Url url = new Url(params.get("url"));
        Url result = null;
        try {
            result = db.store(url);
        } catch (BackendError e) {
        }

        sro = new SubmitResponseObject();
        if (result != null) {
            sro.status = "SUCCESS";
            sro.id = result.getId();
            sro.target = result.getUrl();
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

}
