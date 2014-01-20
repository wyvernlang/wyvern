package wyvern.tools.lex;

public class LexUtils {
	public static boolean isSymbol(char ch) {
		switch(ch) {
			case '(':
			case ')':
			case '[':
			case ']':
			case '{':
			case '}':
			case '+':
			case '-':
			case '*':
			case '/':
			case '=':
			case '<':
			case '>':
			case ':':
			case '&':
			case '|':
			case '!':
			//case '~':
			case '^':
			case ',':
			case '.':
			case ';':
			case '@':
			case '#':
			case '$':
			case '%':
			case '?':
			case '`':
				return true;
			default: return false;
		}
	}
	
	public static boolean isGroupSymbol(char ch) {
		switch(ch) {
			case '(':
			case ')':
			case '[':
			case ']':
			case '{':
			case '}':
				return true;
			default: return false;
		}
	}
}
