package net.oebs.jalos;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;
import java.io.File;
import net.oebs.jalos.netty.HttpServer;

public final class JalosMain {

    static final int PORT = 8080;

    private static EntityStore setupDatabase() {
        Environment myEnv;
        EntityStore store = null;

        try {
            EnvironmentConfig myEnvConfig = new EnvironmentConfig();
            StoreConfig storeConfig = new StoreConfig();

            myEnvConfig.setAllowCreate(true);
            storeConfig.setAllowCreate(true);

            // Open the environment and entity store
            myEnv = new Environment(new File("/tmp/jalos.db"), myEnvConfig);
            store = new EntityStore(myEnv, "EntityStore", storeConfig);
        } catch (DatabaseException dbe) {
            System.err.println("Error opening environment and store: "
                    + dbe.toString());
            System.exit(-1);
        }

        return store;
    }

    public static void main(String[] args) throws Exception {
        EntityStore db = setupDatabase();
        HttpServer server = new HttpServer(db, PORT);
        server.run();
        db.close();
        //close env
    }
}
