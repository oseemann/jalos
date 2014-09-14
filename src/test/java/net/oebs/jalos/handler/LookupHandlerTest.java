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

import io.netty.handler.codec.http.FullHttpResponse;
import static io.netty.handler.codec.http.HttpResponseStatus.SEE_OTHER;
import net.oebs.jalos.db.Backend;
import net.oebs.jalos.db.Url;
import net.oebs.jalos.handler.errors.HandlerError;
import net.oebs.jalos.handler.errors.NotFound;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LookupHandlerTest {

    @Test
    public void testGetResponseSuccess() throws Exception {
        Backend backend = mock(Backend.class);
        Url url = new Url("http://www.example.org/test1");
        when(backend.lookup(new Long(12345))).thenReturn(url);

        LookupHandler handler = new LookupHandler(backend, "/a/12345");
        FullHttpResponse response = handler.getResponse();

        verify(backend).lookup(new Long(12345));
        assertEquals(response.getStatus(), SEE_OTHER);
        assertEquals(response.headers().get("Location"), url.getUrl());
    }

    @Test(expected = NotFound.class)
    public void testGetResponseWithInvalidUrl() throws HandlerError {
        new LookupHandler(mock(Backend.class), "/a/invalid");
    }

}
