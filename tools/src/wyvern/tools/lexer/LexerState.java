package wyvern.tools.lexer;

public interface LexerState {
	Token getToken(Lexer lexer);
}
