package wyvern.tools.interop;

/*
 * A singleton for returning a standard Java importer.
 */
public final class Default {
    private Default() { }
    private static final Importer theImporter = new JavaImporter();
    public static Importer importer() {
        return theImporter;
    }
}
