package wyvern.stdlib;

import com.sun.org.apache.xpath.internal.functions.FuncExtFunction;
import wyvern.DSL.DSL;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.parsing.ContParser;
import wyvern.tools.parsing.DeclarationParser;
import wyvern.tools.parsing.RecordTypeParser;
import wyvern.tools.parsing.transformers.TypedASTTransformer;
import wyvern.tools.parsing.transformers.stdlib.IdentityTranformer;
import wyvern.tools.parsing.transformers.stdlib.ImportChecker;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.simpleParser.Phase1Parser;
import wyvern.tools.typedAST.interfaces.EnvironmentExtender;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.util.CompilationContext;
import wyvern.tools.util.Pair;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Compiler {
    private static String getMD5(String input) {
        try {
            byte[] digested = MessageDigest.getInstance("MD5").digest(input.getBytes());
            StringBuilder sb = new StringBuilder(digested.length);
            for (byte b : digested)
                sb.append(Integer.toHexString(b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static HashMap<String, Pair<Environment, ContParser>> parseCache = new HashMap<>();
	private static HashMap<String, TypedAST> parsedASTs = new HashMap<>();

	public static void flush() {
		parseCache.clear();
		parsedASTs.clear();
	}

    private static Environment getParseEnv(List<DSL> dsls) {
        Environment parseEnv = Globals.getStandardEnv();
        for (DSL dsl : dsls)
            parseEnv = dsl.addToEnv(parseEnv);
        return parseEnv;
    }

    public static Pair<Environment, ContParser> compileSourcePartial(String name, String source, List<DSL> dsls, CompilationContext ctx) {
        String md5 = getMD5(source);
        if (parseCache.containsKey(md5))
            return parseCache.get(md5);

        Environment parseEnv = getParseEnv(dsls);

        RawAST parsedResult = Phase1Parser.parse(name, new StringReader(source));

        Pair<Environment, ContParser> pair = parsedResult.accept(new DeclarationParser(ctx), parseEnv);
		final Pair<Environment, ContParser> finalPair = pair;
		pair = wrapParser(finalPair);
        parseCache.put(md5, pair);
        return pair;
    }

	private static Pair<Environment, ContParser> wrapParser(final Pair<Environment, ContParser> finalPair) {
		return new Pair<Environment, ContParser>(finalPair.first, new CachingParser(finalPair));
	}

	public static Pair<Environment, ContParser> compilePartial(URI url, CompilationContext ctx, List<DSL> dsls) throws IOException {
        String name = url.getPath();
        String source = ctx.getResolver().lookupReader(url);
		final Pair<Environment, ContParser> parserPair = compileSourcePartial(name, source, dsls, ctx);
		return parserPair;
    }

    public static TypedAST resolvePair(Environment parseEnv, Pair<Environment, ContParser> pair) {
        ContParser.EnvironmentResolver r = new ContParser.SimpleResolver(pair.first.extend(parseEnv));
        if (pair.second instanceof RecordTypeParser) {
            ((RecordTypeParser) pair.second).parseTypes(r);
            ((RecordTypeParser)pair.second).parseInner(r);
        }
		TypedAST parsed = pair.second.parse(r);
		return parsed;
    }

    public static TypedAST compileSource(String name, String source, List<DSL> dsls, CompilationContext context) {
        String md5 = getMD5(source);
        if (parseCache.containsKey(md5))
            return resolvePair(getParseEnv(dsls), parseCache.get(md5));

        Environment parseEnv = getParseEnv(dsls);

        RawAST parsedResult = Phase1Parser.parse(name, new StringReader(source));

		final TypedAST result = parsedResult.accept(new BodyParser(context), parseEnv);

		Pair<Environment, ContParser> pair = wrapParser(
			new Pair<Environment, ContParser>(
				(result instanceof EnvironmentExtender) ?
						((EnvironmentExtender) result).extend(Environment.getEmptyEnvironment())
					  : Environment.getEmptyEnvironment(),
				new ContParser() {
					@Override
					public TypedAST parse(EnvironmentResolver r) {
						return result;
					}
				}
		));
		parseCache.put(md5, pair);
		if (!parsedASTs.containsKey(md5))
			parsedASTs.put(md5, result);
		return result;
    }

    public static <T extends TypedAST> T compileSources(String startupname, List<String> files, List<DSL> dsls, TypedASTTransformer<T> xformer) {
		CompilationContext newCtx = new CompilationContext(null, null, null);
		newCtx.getResolver().setCommandRefs(files);
        return xformer.transform(new ImportChecker(new IdentityTranformer()).transform(compileSource(startupname, files.get(0), dsls, newCtx)));
    }

	public static TypedAST compileSources(String startupname, List<String> files, List<DSL> dsls) {
		return compileSources(startupname, files, dsls, new IdentityTranformer());
	}







    public static class ImportCompileResolver {
        private static ImportCompileResolver instance;
        public static ImportCompileResolver getInstance() {
            if (instance == null)
                instance = new ImportCompileResolver();
            return instance;
        }
        private List<String> readers = new ArrayList<>(0);
        private void setCommandRefs(List<String> readers) {
            this.readers = readers;
        }

        public String lookupReader(URI uri) {
            switch (uri.getScheme()) {
                case "input":
                    return handleInputRef(uri);
            }
            throw new RuntimeException();
        }

        private String handleInputRef(URI uri) {
            String path = uri.getSchemeSpecificPart();
            return readers.get(Integer.parseInt(path));
        }
    }

	public static class CachingParser implements RecordTypeParser {
		private final Pair<Environment, ContParser> finalPair;
		private AtomicReference<TypedAST> cached = new AtomicReference<>();
		private boolean entered = false;

		public CachingParser(Pair<Environment, ContParser> finalPair) {
			this.finalPair = finalPair;
		}

		public AtomicReference<TypedAST> getRef() {
			return cached;
		}

		public boolean recursiveCall() {
			return entered;
		}

		@Override
		public TypedAST parse(EnvironmentResolver r) {
			if (cached.get() == null && !entered) {
				entered = true;
				cached.set(finalPair.second.parse(r));
			} else if (cached.get() == null && entered)
				return null; //In case of recursive call without cached value, use getRef to resolve recursively
			return cached.get();
		}

		@Override
		public void parseTypes(EnvironmentResolver r) {
			if (finalPair.second instanceof RecordTypeParser)
				((RecordTypeParser) finalPair.second).parseTypes(r);
		}

		@Override
		public void parseInner(EnvironmentResolver r) {
			if (finalPair.second instanceof RecordTypeParser)
				((RecordTypeParser) finalPair.second).parseInner(r);
		}
	}
}
