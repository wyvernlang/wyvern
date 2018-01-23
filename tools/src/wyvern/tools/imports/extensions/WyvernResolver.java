package wyvern.tools.imports.extensions;

import java.util.LinkedList;

public final class WyvernResolver {
    private static WyvernResolver instance;
    private LinkedList<String> paths = new LinkedList<String>();
    public void resetPaths() {
        paths = new LinkedList<String>();
    }
    public void addPath(String name) {
        paths.addLast(name);
    }
    public static WyvernResolver getInstance() {
        if (instance == null) {
            instance = new WyvernResolver();
        }
        return instance;
    }
    private WyvernResolver() { }
}
