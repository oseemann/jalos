package net.oebs.jalos;

import errors.SettingsError;
import net.oebs.jalos.db.Backend;
import net.oebs.jalos.db.BdbBackend;
import net.oebs.jalos.netty.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class JalosMain {

    static final int PORT = 8080;
    static final Logger log = LogManager.getLogger();

    public static void main(String[] args) throws Exception {
        Settings settings = null;
        try {
            settings = new Settings();
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
