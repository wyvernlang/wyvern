package wyvern.stdlib;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.tools.parsing.Wyvern;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Type;

public class Compiler {
	public static TypedAST compileSources(String filename, List<String> sources) {
		try {
			TypedAST ast = (TypedAST) new Wyvern().parse(new StringReader(sources.get(0)), "test input");
			ast.typecheck(Globals.getStandardEnv(), Optional.<Type>empty());
			return ast;
		} catch (IOException | CopperParserException e) {
			throw new RuntimeException(e);
		}
	}
}
