package wyvern.tools.lex;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;

public class Token implements HasLocation {
	
	public enum Kind { EOF, NEWLINE, INDENT, DEDENT, LPAREN, RPAREN, LBRACK, RBRACK, LBRACE, RBRACE, Identifier, Number, String, Symbol }

	public final Kind kind;
	public final String text;
	public final int value;
	
	public final FileLocation location;
	public FileLocation getLocation() {
		return location;
	}
	
	private Token(Kind k, FileLocation location) { kind = k; text = ""; value = 0; this.location = location; }
	private Token(Kind k, String t, FileLocation location) { kind = k; text = t; value = 0; this.location = location; }
	private Token(Kind k, String t, int val, FileLocation location) { kind = k; text = t; value = val; this.location = location; }

	public static Token getEOF() {
		return new Token(Kind.EOF,FileLocation.UNKNOWN);
	}
	public static Token getNEWLINE() {
		return new Token(Kind.NEWLINE,FileLocation.UNKNOWN);
	}
	public static Token getINDENT() {
		return new Token(Kind.INDENT,FileLocation.UNKNOWN);
	}
	public static Token getDEDENT() {
		return new Token(Kind.DEDENT,FileLocation.UNKNOWN);
	}

	public static Token getEOF(FileLocation location) {
		return new Token(Kind.EOF,location);
	}
	public static Token getNEWLINE(FileLocation location) {
		return new Token(Kind.NEWLINE,location);
	}
	public static Token getINDENT(FileLocation location) {
		return new Token(Kind.INDENT,location);
	}
	public static Token getDEDENT(FileLocation location) {
		return new Token(Kind.DEDENT,location);
	}
	private static Token LPAREN = new Token(Kind.LPAREN, "(",FileLocation.UNKNOWN);
	private static Token RPAREN = new Token(Kind.RPAREN, ")",FileLocation.UNKNOWN);
	private static Token LBRACE = new Token(Kind.LBRACE, "{",FileLocation.UNKNOWN);
	private static Token RBRACE = new Token(Kind.RBRACE, "}",FileLocation.UNKNOWN);
	private static Token LBRACK = new Token(Kind.LBRACK, "[",FileLocation.UNKNOWN);
	private static Token RBRACK = new Token(Kind.RBRACK, "]",FileLocation.UNKNOWN);

	public static Token getGroup(char ch) {
		return getGroup(ch, FileLocation.UNKNOWN);
	}

	public static Token getGroup(char ch, FileLocation location) {
		switch(ch) {
			case '(': return new Token(Kind.LPAREN, "(",location);
			case ')': return new Token(Kind.RPAREN, ")",location);
			case '{': return new Token(Kind.LBRACE, "{",location);
			case '}': return new Token(Kind.RBRACE, "}",location);
			case '[': return new Token(Kind.LBRACK, "[",location);
			case ']': return new Token(Kind.RBRACK, "]",location);
		}
		ToolError.reportError(ErrorMessage.LEXER_ERROR, HasLocation.UNKNOWN);
		return null; // Unreachable.
	}
	
	public static Token getIdentifier(String string) {
		return new Token(Kind.Identifier, string, FileLocation.UNKNOWN);
	}

	public static Token getNumber(String string) {
		return new Token(Kind.Number, string, Integer.parseInt(string), FileLocation.UNKNOWN);
	}
	
	public static Token getString(String string) {
		return new Token(Kind.String, string, FileLocation.UNKNOWN);
	}
	
	public static Token getIdentifier(String string, FileLocation location) {
		return new Token(Kind.Identifier, string, location);
	}

	public static Token getNumber(String string, FileLocation location) {
		return new Token(Kind.Number, string, Integer.parseInt(string), location);
	}
	
	public static Token getString(String string, FileLocation location) {
		return new Token(Kind.String, string, location);
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
