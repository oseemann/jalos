package net.oebs.jalos.db;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.Transaction;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.oebs.jalos.handler.SubmitHandler;

public class BdbBackend implements Backend {

    Environment env;
    EntityStore store;

    public BdbBackend() {
        try {
            init();
        } catch (DatabaseException dbe) {
            System.err.println("Error opening environment and store: "
                    + dbe.toString());
            System.exit(-1);
        }
    }

    @Override
    public void store(Url url) {
        try {
            put(url);
        } catch (DatabaseException ex) {
            Logger.getLogger(SubmitHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Url lookup(Long id) {
        Url url = null;
        try {
            url = retrieve(id);
        } catch (DatabaseException ex) {
            Logger.getLogger(SubmitHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return url;
    }

    @Override
    public void shutdown() {
        try {
            store.close();
            env.close();
        } catch (DatabaseException ex) {
            Logger.getLogger(BdbBackend.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void init() throws DatabaseException {
        EnvironmentConfig envConfig = new EnvironmentConfig();
        StoreConfig storeConfig = new StoreConfig();

        envConfig.setAllowCreate(true);
        envConfig.setTxnNoSync(false);
        envConfig.setTransactional(true);
        storeConfig.setAllowCreate(true);
        storeConfig.setDeferredWrite(false);
        storeConfig.setTransactional(true);

        env = new Environment(new File("/tmp/jalos.db"), envConfig);
        store = new EntityStore(env, "EntityStore", storeConfig);
    }

    private void put(Url url) throws DatabaseException {
        PrimaryIndex<Long, Url> idx = store.getPrimaryIndex(Long.class, Url.class);
        Transaction txn = env.beginTransaction(null, null);
        idx.put(txn, url);
        txn.commitSync();
    }

    private Url retrieve(Long id) throws DatabaseException {
        PrimaryIndex<Long, Url> idx = store.getPrimaryIndex(Long.class, Url.class);
        return idx.get(id);
    }
}
