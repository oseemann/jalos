package net.oebs.jalos.db;

import net.oebs.jalos.db.errors.BackendError;

public interface Backend {

    public void shutdown();

    public Url store(Url url) throws BackendError;

    public Url lookup(Long id) throws BackendError;
}
