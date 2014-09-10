package net.oebs.jalos.errors;

public class SettingsError extends Exception {

    private final String filename;
    private final Exception error;

    public SettingsError(String filename, Exception ex) {
        this.error = ex;
        this.filename = filename;
    }

    @Override
    public String toString() {
        return String.format("Failed to load settings from '%s': %s",
                filename, error);
    }
}
