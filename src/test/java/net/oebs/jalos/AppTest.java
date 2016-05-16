package net.oebs.jalos;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import net.oebs.jalos.handler.SubmitResponseObject;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class AppTest {

    private Path testDbPath;

    class AppThread extends Thread {

        public void run() {

            Settings settings = new Settings();
            settings.setHttpHost("localhost");
            settings.setHttpPort(16801);
            settings.setDbLocation(testDbPath.toString());

            try {
                settings.setHttpHostUrl(new URL("http://localhost:16801/a/"));
            } catch (MalformedURLException ex) {
            }

            try {
                JalosMain.runServer(settings);
            } catch (Exception e) {

            }
        }
    }

    @Before
    public void setUpDb() throws IOException {
        testDbPath = Files.createTempDirectory("jalostest");
        System.out.println(testDbPath.toString());
    }

    @After
    public void tearDownDb() throws IOException {
        String dirToDel = testDbPath.toString();
        assert (dirToDel.contains("jalostest"));
        FileUtils.deleteDirectory(new File(dirToDel));
    }

    @Test
    public void fullTest() throws Exception {
        AppThread app = new AppThread();
        app.start();

        // wait until app is ready to serve
        Thread.sleep(1000);

        Client client = new Client("http://localhost:16801");

        HttpResponse resp = client.xget("/");
        assertEquals("HTTP/1.1 303 See Other", resp.getStatusLine().toString());
        assertEquals("/a/", resp.getFirstHeader("Location").getValue());

        SubmitResponseObject sro = null;
        String testUrl;
        long lastId = 0;

        testUrl = "http://a.com/";
        sro = client.submit(testUrl);
        assertEquals(sro.status, "SUCCESS");
        assertEquals(sro.target, testUrl);
        assertTrue(sro.id > lastId);
        lastId = sro.id;

        testUrl = "http://b.com/";
        sro = client.submit(testUrl);
        assertEquals(sro.status, "SUCCESS");
        assertEquals(sro.target, testUrl);
        assertTrue(sro.id > lastId);
        lastId = sro.id;

        long testCount = 23;
        for (long i = 0; i < testCount; i++) {
            testUrl = String.format("http://testwithid.com/%d", i);
            sro = client.submit(testUrl);
            assertEquals(sro.status, "SUCCESS");
            assertEquals(sro.target, testUrl);
            assertTrue(sro.id > lastId);
            lastId = sro.id;
        }

        for (long i = lastId; i > lastId - testCount; i--) {
            long n = testCount - (lastId - i) - 1;
            assertEquals(client.get(i), "Location: http://testwithid.com/" + n);
        }

        app.interrupt();
    }
}
