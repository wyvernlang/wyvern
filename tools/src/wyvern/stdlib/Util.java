package wyvern.stdlib;

import wyvern.DSL.DSL;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.simpleParser.Phase1Parser;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

public class Util {
	public static TypedAST doCompile(String input, List<DSL> dsls) {
		Reader reader = new StringReader(input);
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Environment env = Globals.getStandardEnv();
		for (DSL dsl : dsls)
			env = dsl.addToEnv(env);
		TypedAST typedAST = parsedResult.accept(BodyParser.getInstance(), env);
		Type resultType = typedAST.typecheck(env);
		return typedAST;
	}
}
