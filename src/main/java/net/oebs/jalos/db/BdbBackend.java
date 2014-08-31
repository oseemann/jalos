package net.oebs.jalos.db;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.Sequence;
import com.sleepycat.je.SequenceConfig;
import com.sleepycat.je.Transaction;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;
import java.io.File;
import net.oebs.jalos.db.errors.BackendError;
import net.oebs.jalos.db.errors.InitFailed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BdbBackend implements Backend {

    Environment env;
    EntityStore store;

    private static final String idSequenceName = "idSeq";
    private static final Logger logger = LogManager.getLogger("HelloWorld");

    public BdbBackend() throws BackendError {
        try {
            init();
        } catch (DatabaseException e) {
            logger.fatal("Failed to initialize BDB: {}", e.toString());
            throw new InitFailed();
        }
    }

    @Override
    public Url store(Url url) throws BackendError {
        try {
            put(url);
            return url;
        } catch (DatabaseException e) {
            logger.error("Failed to store url `{}`: {}", url.getUrl(), e);
            throw new InternalError();
        }
    }

    @Override
    public Url lookup(Long id) throws BackendError {
        Url url = null;
        try {
            url = retrieve(id);
        } catch (DatabaseException e) {
            logger.error("Failed to lookup id `{}`: {}", id, e);
        }
        return url;
    }

    @Override
    public void shutdown() {
        try {
            store.close();
            env.close();
        } catch (DatabaseException e) {
            logger.error("Failed to shutdown bdb: {}", e);
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

        SequenceConfig seqConfig = new SequenceConfig();
        seqConfig.setAllowCreate(true);
        seqConfig.setInitialValue(1);
        seqConfig.setWrap(false);
        store.setSequenceConfig(idSequenceName, seqConfig);
    }

    private void put(Url url) throws DatabaseException {
        PrimaryIndex<Long, Url> idx = store.getPrimaryIndex(Long.class, Url.class);
        Sequence sequence = store.getSequence(idSequenceName);
        Transaction txn = env.beginTransaction(null, null);
        long id = sequence.get(txn, 1);
        url.setId(id);
        Object result = idx.put(txn, url);
        // Currently we only insert, not update. Should always be null;
        assert (result == null);
        txn.commitSync();
    }

    private Url retrieve(Long id) throws DatabaseException {
        PrimaryIndex<Long, Url> idx = store.getPrimaryIndex(Long.class, Url.class);
        return idx.get(id);
    }
}
