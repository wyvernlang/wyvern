package wyvern.tools.lexer;

import wyvern.tools.errors.HasLocation;

public class Token implements HasLocation {
	
	public enum Kind { EOF, NEWLINE, INDENT, DEDENT, LPAREN, RPAREN, LBRACK, RBRACK, LBRACE, RBRACE, Identifier, Number, String, Symbol }

	public final Kind kind;
	public final String text;
	public final int value;
	public final int line;
	
	private Token(Kind k, int line) { kind = k; text = ""; value = 0; this.line = line; }
	private Token(Kind k, String t, int line) { kind = k; text = t; value = 0; this.line = line; }
	private Token(Kind k, String t, int val, int line) { kind = k; text = t; value = val; this.line = line; }
	
	private static Token tokenEOF = new Token(Kind.EOF,-1);
	public static Token getEOF() {
		return tokenEOF;
	}
	private static Token tokenNEWLINE = new Token(Kind.NEWLINE,-1);
	public static Token getNEWLINE() {
		return tokenNEWLINE;
	}
	private static Token tokenINDENT = new Token(Kind.INDENT,-1);
	public static Token getINDENT() {
		return tokenINDENT;
	}
	private static Token tokenDEDENT = new Token(Kind.DEDENT,-1);
	public static Token getDEDENT() {
		return tokenDEDENT;
	}
	private static Token LPAREN = new Token(Kind.LPAREN, "(",-1);
	private static Token RPAREN = new Token(Kind.RPAREN, ")",-1);
	private static Token LBRACE = new Token(Kind.LBRACE, "{",-1);
	private static Token RBRACE = new Token(Kind.RBRACE, "}",-1);
	private static Token LBRACK = new Token(Kind.LBRACK, "[",-1);
	private static Token RBRACK = new Token(Kind.RBRACK, "]",-1);
	
	public static Token getGroup(char ch) {
		switch(ch) {
			case '(': return LPAREN;
			case ')': return RPAREN;
			case '[': return LBRACK;
			case ']': return RBRACK;
			case '{': return LBRACE;
			case '}': return RBRACE;
		}
		throw new LexerException();
	}
	
	public static Token getIdentifier(String string) {
		return new Token(Kind.Identifier, string, -1);
	}

	public static Token getNumber(String string) {
		return new Token(Kind.Number, string, Integer.parseInt(string), -1);
	}
	
	public static Token getString(String string) {
		return new Token(Kind.String, string, -1);
	}
	
	public static Token getIdentifier(String string, int line) {
		return new Token(Kind.Identifier, string, line);
	}

	public static Token getNumber(String string, int line) {
		return new Token(Kind.Number, string, Integer.parseInt(string), line);
	}
	
	public static Token getString(String string, int line) {
		return new Token(Kind.String, string, line);
	}

	@Override
	public boolean equals(Object otherT) {
		if (!(otherT instanceof Token))
			return false;
		Token otherTok = (Token) otherT; 
		return otherTok.kind == kind && otherTok.value == value && otherTok.text.equals(text);
	}
	
	@Override
	public int hashCode() {
		return 37*kind.hashCode() + text.hashCode();
	}
	
	@Override
	public String toString() {
		return kind.name() + '<' + text + '>';
	}
}
