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
package net.oebs.jalos;

import java.io.StringBufferInputStream;
import net.oebs.jalos.errors.SettingsError;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SettingsTest {

    @Test
    public void simpleTest() throws SettingsError {

        String testConfig = ""
                + "db.location=/tmp/jtest-1\n"
                + "http.port = 8001\n"
                + "http.host = testhost.test\n"
                + "http.hostUrl = http://example.org/test/\n";

        StringBufferInputStream config = new StringBufferInputStream(testConfig);
        Settings settings = new Settings(config);

        assertEquals(settings.getDbLocation(), "/tmp/jtest-1");
        assertEquals(settings.getHttpHost(), "testhost.test");
        assertEquals(settings.getHttpPort(), 8001);
        assertEquals(settings.getHttpHostUrl().toString(), "http://example.org/test/");
    }

}
