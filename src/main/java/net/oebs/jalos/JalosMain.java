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

import java.io.FileNotFoundException;
import net.oebs.jalos.db.Backend;
import net.oebs.jalos.db.BdbBackend;
import net.oebs.jalos.db.errors.BackendError;
import net.oebs.jalos.errors.SettingsError;
import net.oebs.jalos.netty.HttpServer;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JalosMain {

    static final Logger log = LoggerFactory.getLogger(JalosMain.class);

    private static Settings createSettings(String[] args) throws SettingsError {
        Options options = new Options();
        options.addOption("c", "config", true, "config file path");

        CommandLineParser parser = new BasicParser();
        try {
            CommandLine cmd = parser.parse(options, args);
        } catch (ParseException e) {
            // TODO
        }

        String configFile = options.getOption("c").getValue("jalos.properties");
        Settings settings = null;

        try {
            settings = new Settings(configFile);
        } catch (FileNotFoundException e) {
            // TODO
        }
        return settings;
    }

    public static void runServer(Settings settings) throws BackendError, Exception {
        Backend db = new BdbBackend(settings);
        HttpServer server = new HttpServer(settings);

        RuntimeContext ctx = RuntimeContext.getInstance();

        ctx.setSettings(settings);

        ctx.setBackend(db);

        server.run();
        db.shutdown();
    }

    public static void main(String[] args) throws Exception {

        Settings settings = null;
        try {
            settings = createSettings(args);
        } catch (SettingsError ex) {
            System.out.println(ex.toString());
            System.exit(1);
        }

        runServer(settings);
    }
}
