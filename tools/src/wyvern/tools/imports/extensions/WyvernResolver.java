package wyvern.tools.imports.extensions;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;


public class WyvernResolver {
	private static WyvernResolver instance;
	private LinkedList<String> paths = new LinkedList<String>();
	public void resetPaths() {
		paths = new LinkedList<String>();
	}
	public void addPath(String name) {
		paths.addLast(name);
	}
	public static WyvernResolver getInstance() {
		if (instance == null)
			instance = new WyvernResolver();
		return instance;
	}
	private WyvernResolver() {}


}
