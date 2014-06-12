package wyvern.tools.parsing;

import wyvern.tools.parsing.parselang.CopperTSL;
import wyvern.tools.typedAST.interfaces.TypedAST;

public interface ExtParser {
	public TypedAST parse(ParseBuffer input) throws Exception;
	public static HasParser meta$get() {
		return CopperTSL::new;
	}
}
