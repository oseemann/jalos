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

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import static io.netty.handler.codec.http.HttpHeaders.Names.LOCATION;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.SEE_OTHER;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import net.oebs.jalos.db.Backend;
import net.oebs.jalos.db.Url;
import net.oebs.jalos.handler.errors.HandlerError;
import net.oebs.jalos.handler.errors.NotFound;

public class LookupHandler implements Handler {

    Backend db;
    Url result = null;

    public LookupHandler(Backend db, String uri) throws HandlerError {
        this.db = db;
        assert (uri.startsWith("/a/"));
        String id_param = uri.substring(3);
        Long id;
        try {
            id = Long.parseLong(id_param);
        } catch (NumberFormatException e) {
            throw new NotFound();
        }

        try {
            result = db.lookup(id);
        } catch (Exception e) {
            throw new NotFound();
        }
    }

    @Override
    public FullHttpResponse getResponse() {
        FullHttpResponse response;
        if (result != null) {
            response = new DefaultFullHttpResponse(HTTP_1_1, SEE_OTHER);
            response.headers().set(LOCATION, result.getUrl());
        } else {
            response = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND);
        }
        return response;
    }
}
