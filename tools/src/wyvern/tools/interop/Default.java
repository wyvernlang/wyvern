package wyvern.tools.interop;

/*
 * A singleton for returning a standard Java importer.
 */
public class Default {
	private static Importer theImporter = new JavaImporter();
	public static Importer importer() {
		return theImporter;
	}
}
