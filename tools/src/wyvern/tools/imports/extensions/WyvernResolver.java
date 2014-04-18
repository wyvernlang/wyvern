package wyvern.tools.imports.extensions;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.stdlib.Globals;
import wyvern.tools.imports.ImportBinder;
import wyvern.tools.imports.ImportResolver;
import wyvern.tools.parsing.Wyvern;
import wyvern.tools.typedAST.interfaces.EnvironmentExtender;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.Reference;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Optional;

public class WyvernResolver implements ImportResolver {
	private static WyvernResolver instance;
	private static HashMap<String, String> savedResolutions = new HashMap<>();
	public static void addFile(String name, String source) {
		savedResolutions.put(name, source);
	}

	public static WyvernResolver getInstance() {
		if (instance == null)
			instance = new WyvernResolver();
		return instance;
	}
	private WyvernResolver() {}

	private static class WyvernBinder implements ImportBinder {


		private TypedAST res;

		public WyvernBinder(String filename, String source) {
			res = null;
			try {
				res = (TypedAST)new Wyvern().parse(new StringReader(source), filename);
			} catch (IOException | CopperParserException e) {
				throw new RuntimeException(e);
			}
			res.typecheck(Globals.getStandardEnv(), Optional.<Type>empty());
		}

		boolean etping = false;
		@Override
		public Environment extendTypes(Environment in) {
			if (etping) {
				throw new RuntimeException("Cyclic dependency");
			}
			etping = true;
			if (res instanceof EnvironmentExtender)
				in = ((EnvironmentExtender) res).extendType(in, Globals.getStandardEnv());
			etping = false;
			return in;
		}

		boolean enaming = false;
		@Override
		public Environment extendNames(Environment in) {
			if (enaming) {
				throw new RuntimeException("Cyclic dependency");
			}
			enaming = true;
			if (res instanceof EnvironmentExtender)
				in = ((EnvironmentExtender) res).extendName(in, Globals.getStandardEnv());
			enaming = false;
			return in;
		}

		boolean extending = false;
		@Override
		public Environment extend(Environment in) {
			if (extending) {
				throw new RuntimeException("Cyclic dependency");
			}
			extending = true;
			if (res instanceof EnvironmentExtender)
				in = ((EnvironmentExtender) res).extend(in);
			extending = false;
			return in;
		}

		boolean typechecking = false;
		@Override
		public Type typecheck(Environment env) {
			if (typechecking) {
				throw new RuntimeException("Cyclic dependency");
			}
			typechecking = true;
			Type resu = res.typecheck(env, Optional.<Type>empty());
			typechecking = false;
			return resu;
		}

		boolean evaling = false;
		@Override
		public Environment extendVal(Environment env) {
			if (extending) {
				throw new RuntimeException("Cyclic dependency");
			}
			extending = true;
			if (res instanceof EnvironmentExtender)
				env = ((EnvironmentExtender) res).evalDecl(env);
			extending = false;
			return env;
		}

		@Override
		public Environment bindVal(Environment env) {
			//Bound as part of eval
			return env;
		}
	}

	private HashMap<String, WyvernBinder> savedBinders = new HashMap<>();
	private WyvernBinder addAndBind(String name, WyvernBinder binder) {
		savedBinders.put(name, binder);
		return binder;
	}

	@Override
	public ImportBinder resolveImport(URI uri) {
		String filename = uri.getSchemeSpecificPart();
		if (savedBinders.containsKey(filename))
			return savedBinders.get(filename);
		if (savedResolutions.containsKey(filename))
			return addAndBind(filename, new WyvernBinder(filename,savedResolutions.get(filename)));
		throw new RuntimeException("Unknown file"); //TODO
	}
}
