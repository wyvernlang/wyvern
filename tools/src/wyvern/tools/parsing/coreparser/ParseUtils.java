package wyvern.tools.parsing.coreparser;

import java.io.Reader;

import wyvern.tools.lexing.WyvernLexer;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Type;

public class ParseUtils {
	/** Creates an instance of the new Wyvern parser
	 * 
	 * @param filename
	 * @param source
	 * @return
	 * @throws ParseException
	 */
	public static WyvernParser<TypedAST,Type> makeParser(String filename, Reader source)
			throws ParseException {
		WyvernParser<TypedAST,Type> wp = new WyvernParser<TypedAST,Type>(new WyvernTokenManager<WyvernLexer>(source, filename, WyvernLexer.class));
		wp.setBuilder(new WyvernASTBuilder());
		return wp;
	}
}
