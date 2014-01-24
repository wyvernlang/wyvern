package wyvern.tools.prsr.std;

import wyvern.tools.lex.ILexStream;
import wyvern.tools.lex.Token;
import wyvern.tools.prsr.Parser;
import wyvern.tools.prsr.ParserException;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.TypedAST;

/**
 * Created by Ben Chung on 1/24/14.
 */
public class Terminal implements Parser<Token> {
	private final Token.Kind kind;
	private final String token;

	public Terminal(Token.Kind kind, String token) {

		this.kind = kind;
		this.token = token;
	}

	@Override
	public Token parse(ILexStream stream) throws ParserException {
		Token next = stream.peek();
		if (kind != null && token != null) {
			if (next.kind == kind && next.text.equals(token)) {
				return stream.next();
			}
			throw new ParserException();
		}
		if (kind != null) {
			if (next.kind == kind) {
				return stream.next();
			}
			throw new ParserException();
		}
		if (token != null) {
			if (next.text.equals(token)) {
				return stream.next();
			}
			throw new ParserException();
		}
		throw new ParserException();
	}
}
