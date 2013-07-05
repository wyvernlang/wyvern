package wyvern.tools.lexer;

import java.io.IOException;
import java.io.Reader;
import java.util.Stack;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;

public class Lexer {
	private Stack<String> prefixStack = new Stack<String>();
	LexerState currentState = InitialState.getInstance();
	
	private Reader reader;
	private int currentChar = -1;
	private String filename;
	
	private int charNum = -1;
	private int lineNum = -1;
	
	public Lexer(String filename, Reader r) {
		reader = r;
		lineNum = 1; // First line is #1, every time we see '\n' when read() method is called, increment. Simple.
		charNum = 0; // Every line starts with a zeroth char. When read() is called, increment. If read sees a '\n', set to 0
		prefixStack.push("");
		this.filename = filename;
	}
	
	private void fill() {
		try {
			if (currentChar == -1)
				currentChar = reader.read();
		} catch (IOException e) {
			ToolError.reportError(ErrorMessage.LEXER_ERROR, HasLocation.UNKNOWN);
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
		charNum++;
		if (ch == '\n') {
			lineNum++;
			charNum = 0;
		}
		return ch;
	}

	private Token currentTok = null;
	
	private void fillToken() {
		if (currentTok == null) {
			currentTok = currentState.getToken(this);
		}
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
	
	public FileLocation getLocation() {
		return new FileLocation(filename, lineNum, charNum);
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
