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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.FullHttpResponse;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import net.oebs.jalos.RuntimeContext;
import net.oebs.jalos.Settings;
import net.oebs.jalos.db.Backend;
import net.oebs.jalos.db.Url;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SubmitHandlerTest {

    @Test
    public void testGetResponse() throws Exception {
        String uri = "http://www.example.org/submittest1";
        Long id = new Long(123);
        Url u = new Url(uri, id);
        Map<String, String> params = new HashMap<>();
        params.put("url", uri);

        Backend backend = mock(Backend.class);
        when(backend.store(any(Url.class))).thenReturn(u);
        RuntimeContext.getInstance().setBackend(backend);

        Settings settings = new Settings();
        URL testUrl = new URL("http://w1.example.org/x1/");
        settings.setHttpHostUrl(testUrl);

        SubmitHandler handler = new SubmitHandler(settings, params);
        FullHttpResponse response = handler.getResponse();

        String json = new String(response.content().array());
        ObjectMapper mapper = new ObjectMapper();
        SubmitResponseObject sro = mapper.readValue(json, SubmitResponseObject.class);

        verify(backend).store(any(Url.class));
        assertEquals(response.getStatus(), OK);
        assertEquals(sro.status, "SUCCESS");
        assertEquals(sro.target, uri);
        assertEquals(sro.id, id);
        assertEquals(sro.url.toString(), testUrl.toString() + id.toString());
    }

}
