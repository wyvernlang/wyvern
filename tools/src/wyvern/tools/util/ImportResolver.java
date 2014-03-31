package wyvern.tools.util;

import wyvern.tools.types.Environment;
import wyvern.tools.util.Pair;

import java.net.URI;
import java.util.List;

public interface ImportResolver {
	boolean checkURI(URI path);
	String getDefaultName(URI ref);
	Environment resolveImport(URI uri) throws Exception;
}
