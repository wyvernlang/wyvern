package wyvern.tools.lexer;

import java.io.IOException;
import java.io.Reader;
import java.util.Stack;

public class Lexer {
	private Stack<String> prefixStack = new Stack<String>();
	LexerState currentState = InitialState.getInstance();
	
	private Reader reader;
	private int currentChar = -1;
	private int lineNum = -1;
	
	public Lexer(Reader r) {
		reader = r;
		lineNum = 0;
		prefixStack.push("");
	}
	
	private void fill() {
		try {
			if (currentChar == -1)
				currentChar = reader.read();
		} catch (IOException e) {
			throw new LexerException();
		}
	}
	
	public boolean hasNext() {
		fill();
		return currentChar != -1;
	}
	
	/** Illegal to call without checking hasNext() first */
	public char peek() {
		fill();
		return (char) currentChar;
	}

	/** Illegal to call without checking hasNext() first */
	public char read() {
		fill();
		char ch = (char) currentChar;
		currentChar = -1;
		return ch;
	}

	private Token currentTok = null;
	private void fillToken() {
		if (currentTok == null)
			currentTok = currentState.getToken(this);
	}
	
	public Token getToken() {
		fillToken();
		Token token = currentTok;
		currentTok = null;
		//System.err.println(token);
		return token;
	}

	public Token peekToken() {
		fillToken();
		//System.err.println("peeked at " + currentTok);
		return currentTok;
	}

	public boolean isSymbol(char ch) {
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
			case '~':
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
	
	public boolean isGroupSymbol(char ch) {
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
	
	public int getLineNumber() {
		return lineNum;
	}
	
	void incrementLine() {
		lineNum++;
	}

	String getCurrentPrefix() {
		return prefixStack.peek();
	}

	void pushPrefix(String newPrefix) {
		prefixStack.push(newPrefix);
	}

	public void popPrefix() {
		prefixStack.pop();
	}
}
