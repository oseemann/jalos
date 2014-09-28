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

import net.oebs.jalos.db.Backend;
import net.oebs.jalos.db.BdbBackend;
import net.oebs.jalos.errors.SettingsError;
import net.oebs.jalos.netty.HttpServer;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class JalosMain {

    static final int PORT = 8080;
    static final Logger log = LogManager.getLogger();

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("c", "config", true, "config file path");

        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(options, args);

        String configFile = options.getOption("c").getValue("jalos.properties");
        Settings settings = null;

        try {
            settings = new Settings(configFile);
        } catch (SettingsError ex) {
            System.out.println(ex.toString());
            System.exit(1);
        }

        Backend db = new BdbBackend(settings);
        HttpServer server = new HttpServer(db, settings);
        server.run();
        db.shutdown();
    }
}
