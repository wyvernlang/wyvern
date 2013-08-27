package wyvern.tools.parsing.resolvers;

import wyvern.DSL.DSL;
import wyvern.tools.parsing.ContParser;
import wyvern.tools.parsing.ImportResolver;
import wyvern.tools.types.Environment;
import wyvern.tools.util.CompilationContext;
import wyvern.tools.util.Pair;
import java.net.URI;
import java.util.List;

import static wyvern.stdlib.Compiler.compileSourcePartial;

public class CompilerInputResolver implements ImportResolver {
	private List<String> readers;

	public CompilerInputResolver(List<String> readers) {
		this.readers = readers;
	}

	@Override
	public boolean checkURI(URI path) {
		return path.getScheme().equals("input");
	}

	@Override
	public Pair<Environment, ContParser> resolveImport(URI uri, List<DSL> dsls, CompilationContext ctx) {
		String path = uri.getSchemeSpecificPart();
		String source = readers.get(Integer.parseInt(path));
		return compileSourcePartial(uri.getPath(), source, dsls, ctx);
	}
}
