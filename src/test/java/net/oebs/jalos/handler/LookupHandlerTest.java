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
import net.oebs.jalos.db.Backend;
import net.oebs.jalos.handler.errors.HandlerError;
import net.oebs.jalos.handler.errors.NotFound;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class LookupHandlerTest {

    public LookupHandlerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetResponseSuccess() throws Exception {
        Backend backend = mock(Backend.class);
        String uri = "/a/12345";
        LookupHandler instance = new LookupHandler(backend, uri);
        FullHttpResponse result = instance.getResponse();
        verify(backend).lookup(new Long(12345));
    }

    @Test(expected = NotFound.class)
    public void testGetResponseWithInvalidUrl() throws HandlerError {
        Backend backend = mock(Backend.class);
        String uri = "/a/invalid";
        LookupHandler instance = new LookupHandler(backend, uri);
    }

}
