package wyvern.stdlib;

import wyvern.DSL.DSL;
import wyvern.tools.parsing.ContParser;
import wyvern.tools.parsing.DeclarationParser;
import wyvern.tools.parsing.RecordTypeParser;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.simpleParser.Phase1Parser;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.util.Pair;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

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

    private static Environment getParseEnv(List<DSL> dsls) {
        Environment parseEnv = Globals.getStandardEnv();
        for (DSL dsl : dsls)
            parseEnv = dsl.addToEnv(parseEnv);
        return parseEnv;
    }

    public static Pair<Environment, ContParser> compileSourcePartial(String name, String source, List<DSL> dsls) {
        String md5 = getMD5(source);
        if (parseCache.containsKey(md5))
            return parseCache.get(md5);

        Environment parseEnv = getParseEnv(dsls);

        RawAST parsedResult = Phase1Parser.parse(name, new StringReader(source));

        Pair<Environment, ContParser> pair = parsedResult.accept(DeclarationParser.getInstance(), parseEnv);
        parseCache.put(md5, pair);
        return pair;
    }

    public static Pair<Environment, ContParser> compilePartial(URI url, List<DSL> dsls) throws IOException {
        String name = url.getPath();
        String source = ImportCompileResolver.getInstance().lookupReader(url);
		final Pair<Environment, ContParser> parserPair = compileSourcePartial(name, source, dsls);
		return new Pair<Environment, ContParser>(parserPair.first, new ContParser() {
			private TypedAST cached = null;
			@Override
			public TypedAST parse(EnvironmentResolver r) {
				if (cached == null)
					cached = parserPair.second.parse(r);
				return cached;
			}
		});
    }

    public static Pair<Environment, ContParser> compileSourcePartial(String startupname, List<String> files, List<DSL> dsls) {
        ImportCompileResolver.getInstance().setCommandRefs(files);
        Pair<Environment, ContParser> pair = compileSourcePartial(startupname, files.get(0), dsls);
        return pair;
    }

    private static TypedAST resolvePair(Environment parseEnv, Pair<Environment, ContParser> pair) {
        ContParser.EnvironmentResolver r = new ContParser.SimpleResolver(pair.first.extend(parseEnv));
        if (pair.second instanceof RecordTypeParser) {
            ((RecordTypeParser) pair.second).parseTypes(r);
            ((RecordTypeParser)pair.second).parseInner(r);
        }
        return pair.second.parse(r);
    }

    public static TypedAST compileSource(String name, String source, List<DSL> dsls) {
        String md5 = getMD5(source);
        if (parseCache.containsKey(md5))
            return resolvePair(getParseEnv(dsls), parseCache.get(md5));

        Environment parseEnv = getParseEnv(dsls);

        RawAST parsedResult = Phase1Parser.parse(name, new StringReader(source));

        Pair<Environment, ContParser> pair = parsedResult.accept(DeclarationParser.getInstance(), parseEnv);
        parseCache.put(md5, pair);
        TypedAST result = resolvePair(parseEnv, pair);
		if (!parsedASTs.containsKey(md5))
			parsedASTs.put(md5, result);
		return result;
    }

    public static TypedAST compileSources(String startupname, List<String> files, List<DSL> dsls) {
        ImportCompileResolver.getInstance().setCommandRefs(files);
        return compileSource(startupname, files.get(0), dsls);
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
}
