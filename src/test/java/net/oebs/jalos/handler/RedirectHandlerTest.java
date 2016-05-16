/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.oebs.jalos.handler;

import io.netty.handler.codec.http.FullHttpResponse;
import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class RedirectHandlerTest {

    @Test
    public void testGet() throws Exception {
        RedirectHandler handler = new RedirectHandler() {
            {
                location = "abc";
            }
        };
        Map<String, String> params = new HashMap<>();
        FullHttpResponse response = handler.handleGet("/", params);

        assertEquals(response.getStatus(), FOUND);
        assertEquals(response.headers().get("Location"), "abc");
    }
}
