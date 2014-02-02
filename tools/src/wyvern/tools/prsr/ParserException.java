package wyvern.tools.prsr;

import wyvern.tools.lex.Token;

/**
 * Created by Ben Chung on 1/24/14.
 */
public class ParserException extends Exception {
	private final Token t;

	public ParserException(Token t) {
		this.t = t;
	}

	@Override
	public String toString() {
		return "Error at "+t.location.filename+":" +t.location.line+ ":"+t.location.character;
	}
}
