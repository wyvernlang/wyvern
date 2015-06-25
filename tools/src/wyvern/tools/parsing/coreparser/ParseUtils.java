package wyvern.tools.parsing.coreparser;

import java.io.Reader;

import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Type;

public class ParseUtils {
	public static WyvernParser<TypedAST,Type> makeParser(String filename, Reader source)
			throws ParseException {
		WyvernParser<TypedAST,Type> wp = new WyvernParser<TypedAST,Type>(new WyvernTokenManager(source, filename));
		wp.setBuilder(new WyvernASTBuilder());
		return wp;
	}
}
