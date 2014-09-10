package net.oebs.jalos;

import errors.SettingsError;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Settings {

    static final Logger log = LogManager.getLogger();

    private final String filename = "jalos.properties";
    private final Properties properties = new Properties();

    private String dbLocation;
    private String httpHost;
    private int httpPort;

    public Settings() throws SettingsError {
        load();
    }

    private void load() throws SettingsError {
        InputStream in = null;
        try {
            log.info("Loading settings from %s", filename);
            in = new FileInputStream(filename);
            properties.load(in);

            dbLocation = properties.getProperty("db.location");
            log.debug("db.location = %s", dbLocation);

            httpPort = Integer.parseInt(properties.getProperty("http.port"));
            log.debug("http.port = %s", httpPort);

            httpHost = properties.getProperty("http.host");
            log.debug("http.host = %s", httpHost);

        } catch (IOException ex) {
            throw new SettingsError(filename, ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                }
            }
        }

    }

    public int getHttpPort() {
        return httpPort;
    }

    public String getHttpHost() {
        return httpHost;
    }

    public String getDbLocation() {
        return dbLocation;
    }
}
