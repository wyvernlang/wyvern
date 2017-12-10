package wyvern.tools.imports.extensions;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;


public class WyvernResolver {
	private static WyvernResolver instance;
	private LinkedList<String> paths = new LinkedList<String>();
	private boolean useNewParser = false;
	private static HashMap<String, String> savedResolutions = new HashMap<>();
	public static void addFile(String name, String source) {
		savedResolutions.put(name, source);
	}
	public void resetPaths() {
		paths = new LinkedList<String>();
	}
	public void addPath(String name) {
		paths.addLast(name);
	}
	/** Sets a flag to use the new parser.  Returns the old value of the flag. */
	public boolean setNewParser(boolean useNewParser) {
		boolean oldValue = this.useNewParser;
		this.useNewParser = useNewParser;
		return oldValue;
	}
	public static WyvernResolver getInstance() {
		if (instance == null)
			instance = new WyvernResolver();
		return instance;
	}
	private WyvernResolver() {}


}
