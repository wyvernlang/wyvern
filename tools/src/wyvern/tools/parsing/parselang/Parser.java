package wyvern.tools.parsing.parselang;

import wyvern.tools.parsing.ExtParser;
import wyvern.tools.parsing.HasParser;
import wyvern.tools.parsing.ParseBuffer;
import wyvern.tools.typedAST.interfaces.TypedAST;

public interface Parser {
	public TypedAST parse(ParseBuffer buf);

	public static HasParser meta$get() {
		return CopperTSL::new;
	}

}
