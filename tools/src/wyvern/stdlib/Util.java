package wyvern.stdlib;

import wyvern.DSL.DSL;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.parsing.ContParser;
import wyvern.tools.parsing.DeclarationParser;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.simpleParser.Phase1Parser;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.Pair;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

public class Util {
	public static TypedAST doCompile(String input, List<DSL> dsls) {
		return doCompile(input, "Test", dsls);
	}

	public static TypedAST doCompile(String input, String name, List<DSL> dsls) {
		return doCompile(new StringReader(input), name, dsls);
	}

	public static TypedAST doCompile(Reader reader, String name, List<DSL> dsls) {
		RawAST parsedResult = Phase1Parser.parse(name, reader);
		Environment env = Globals.getStandardEnv();
		for (DSL dsl : dsls)
			env = dsl.addToEnv(env);
		TypedAST typedAST = parsedResult.accept(new BodyParser(), env);
		Type resultType = typedAST.typecheck(env);
		return typedAST;
	}

	public static Pair<Environment, ContParser> doPartialCompile(Reader reader, String name, List<DSL> dsls) {
		RawAST parsedResult = Phase1Parser.parse(name, reader);
		Environment env = Globals.getStandardEnv();
		for (DSL dsl : dsls)
			env = dsl.addToEnv(env);
		return parsedResult.accept(new DeclarationParser(null), env);
	}

	public static Pair<Environment, ContParser> doPartialCompile(String input, String name, List<DSL> dsls) {
		return doPartialCompile(new StringReader(input), name, dsls);
	}
}