package wyvern.tools.prsr.std;

import wyvern.tools.lex.ILexStream;
import wyvern.tools.lex.Token;
import wyvern.tools.prsr.Parser;
import wyvern.tools.prsr.ParserException;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.util.Pair;

import java.util.function.Function;

/**
 * Created by Ben Chung on 1/24/14.
 */
public class Terminal extends AbstractParser<Token> {
	private final Token.Kind kind;
	private final String token;

	public Terminal(Token.Kind kind, String token) {
		this.kind = kind;
		this.token = token;
	}

	public Terminal(Token.Kind kind) {
		this(kind, null);
	}

	public Terminal(String token) {
		this(null, token);
	}

	@Override
	public Token doParse(ILexStream stream) throws ParserException {
		preParse();
		Token next = stream.peek();
		if (kind != null && token != null) {
			if (next.kind == kind && next.text.equals(token)) {
				return stream.next();
			}
			throw new ParserException(next);
		}
		if (kind != null) {
			if (next.kind == kind) {
				return stream.next();
			}
			throw new ParserException(next);
		}
		if (token != null) {
			if (next.text.equals(token)) {
				return stream.next();
			}
			throw new ParserException(next);
		}
		throw new ParserException(next);
	}

	public String toString() {
		if (kind != null && token != null) {
			return token +":"+kind;
		}
		if (kind != null) {
			return ":"+kind;
		}
		if (token != null) {
			return token;
		}
		return "EMP";
	}
}
