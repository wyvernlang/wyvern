package wyvern.tools.imports.extensions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.stdlib.Globals;
import wyvern.tools.imports.ImportBinder;
import wyvern.tools.imports.ImportResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.parsing.coreparser.ParseUtils;
import wyvern.tools.typedAST.core.binding.compiler.MetadataInnerBinding;
import wyvern.tools.typedAST.interfaces.EnvironmentExtender;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.Reference;

public class WyvernResolver implements ImportResolver {
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


	@Override
	public ImportBinder resolveImport(URI uri) {
		throw new RuntimeException("not implemented anymore ");
	}
}
