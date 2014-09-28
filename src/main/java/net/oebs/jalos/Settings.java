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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import net.oebs.jalos.errors.SettingsError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Settings {

    static final Logger log = LogManager.getLogger();

    private String filename;
    private final Properties properties = new Properties();

    private String dbLocation;
    private String httpHost;
    private int httpPort;
    private URL httpHostUrl;

    public Settings(String configFile) throws SettingsError, FileNotFoundException {
        this.filename = configFile;
        log.info("Loading settings from %s", filename);
        InputStream config = null;
        try {
            config = new FileInputStream(filename);
        } catch (FileNotFoundException ex) {
            throw new SettingsError(filename, ex);
        }
        load(config);
    }

    public Settings(InputStream config) throws SettingsError {
        load(config);
    }

    public Settings() {
    }

    private void load(InputStream config) throws SettingsError {
        try {
            properties.load(config);

            dbLocation = properties.getProperty("db.location");
            log.debug("db.location = %s", dbLocation);

            httpPort = Integer.parseInt(properties.getProperty("http.port"));
            log.debug("http.port = %s", httpPort);

            httpHost = properties.getProperty("http.host");
            log.debug("http.host = %s", httpHost);

            httpHostUrl = new URL(properties.getProperty("http.hostUrl"));
            log.debug("http.hostUrl = %s", httpHostUrl);

        } catch (MalformedURLException ex) {
            throw new SettingsError(filename, ex);
        } catch (IOException ex) {
            throw new SettingsError(filename, ex);
        } finally {
            if (config != null) {
                try {
                    config.close();
                } catch (IOException ex) {
                }
            }
        }

    }

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int port) {
        this.httpPort = port;
    }

    public String getHttpHost() {
        return httpHost;
    }

    public void setHttpHost(String host) {
        this.httpHost = host;
    }

    public URL getHttpHostUrl() {
        return httpHostUrl;
    }

    public void setHttpHostUrl(URL hostUrl) {
        this.httpHostUrl = hostUrl;
    }

    public String getDbLocation() {
        return dbLocation;
    }

    public void setDbLocation(String path) {
        this.dbLocation = path;
    }

}
