package net.oebs.jalos;

import net.oebs.jalos.db.Backend;
import net.oebs.jalos.db.BdbBackend;
import net.oebs.jalos.netty.HttpServer;

public final class JalosMain {

    static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        Backend db = new BdbBackend();
        HttpServer server = new HttpServer(db, PORT);
        server.run();
        db.shutdown();
    }
}
