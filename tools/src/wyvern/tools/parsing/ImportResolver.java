package wyvern.tools.parsing;

import wyvern.DSL.DSL;
import wyvern.tools.types.Environment;
import wyvern.tools.util.CompilationContext;
import wyvern.tools.util.Pair;

import java.net.URI;
import java.util.List;

public interface ImportResolver {
	boolean checkURI(URI path);
	String getDefaultName(URI ref);
	Pair<Environment,ContParser> resolveImport(URI uri, List<DSL> dsls, CompilationContext ctx) throws Exception;
}
