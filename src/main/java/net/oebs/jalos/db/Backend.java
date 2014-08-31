package net.oebs.jalos.db;

public interface Backend {

    public void shutdown();

    public void store(Url url);

    public Url lookup(Long id);
}
