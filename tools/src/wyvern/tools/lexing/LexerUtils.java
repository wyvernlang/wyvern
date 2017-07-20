package wyvern.tools.lexing;

import static wyvern.tools.parsing.coreparser.WyvernParserConstants.MULTI_LINE_COMMENT;
import static wyvern.tools.parsing.coreparser.WyvernParserConstants.SINGLE_LINE_COMMENT;
import static wyvern.tools.parsing.coreparser.WyvernParserConstants.WHITESPACE;

import wyvern.tools.parsing.coreparser.Token;

public class LexerUtils {
	public static boolean isSpecial(Token t) {
		switch (t.kind) {
			case SINGLE_LINE_COMMENT:
			case MULTI_LINE_COMMENT:
			case WHITESPACE:
					return true;
			default:
					return false;
		}
	}
	

}
