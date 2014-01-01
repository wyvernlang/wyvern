package wyvern.tools.parsing.resolvers;

import org.junit.Assert;
import wyvern.DSL.DSL;
import wyvern.tools.parsing.ContParser;
import wyvern.tools.parsing.ImportResolver;
import wyvern.tools.types.Environment;
import wyvern.tools.util.CompilationContext;
import wyvern.tools.util.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static wyvern.stdlib.Compiler.compileSourcePartial;

public class FileResolver implements ImportResolver {

	public FileResolver() {
	}

	@Override
	public boolean checkURI(URI path) {
		return path.getScheme().equals("file");
	}

	@Override
	public String getDefaultName(URI ref) {
		return null;
	}

	@Override
	public Pair<Environment, ContParser> resolveImport(URI uri, List<DSL> dsls, CompilationContext ctx) {
		String path = uri.getSchemeSpecificPart();

		URL url = FileResolver.class.getClassLoader().getResource(path);
		if (url == null) {
			Assert.fail("Unable to open " + path + " file.");
			throw new RuntimeException();
		}

		InputStream is;
		try {
			is = url.openStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Scanner reader = new Scanner(new InputStreamReader(is));
		String source = reader.useDelimiter("\\A").next();
		try {
			is.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return compileSourcePartial(uri.getPath(), source, dsls, ctx);
	}
}
