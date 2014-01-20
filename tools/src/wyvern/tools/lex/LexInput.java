package wyvern.tools.lex;

import java.io.IOException;
import java.io.Reader;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;

class LexInput implements ILexInput {
	private Reader reader;
	private int currentChar = -1;
	private String filename;
	
	private int charNum = -1;
	private int lineNum = -1;
	
	public LexInput(String filename, Reader r) {
		reader = r;
		lineNum = 1; // First line is #1, every time we see '\n' when read() method is called, increment. Simple.
		charNum = 0; // Every line starts with a zeroth char. When read() is called, increment. If read sees a '\n', set to 0
		this.filename = filename;
	}


	@Override
	public boolean hasNext() {
		fill();
		return currentChar != -1;
	}
	
	/** Illegal to call without checking hasNext() first */
	@Override
	public char peek() {
		fill();
		return (char) currentChar;
	}

	/** Illegal to call without checking hasNext() first */
	@Override
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

	
	public int getLineNumber() {
		return lineNum;
	}
	
	@Override
	public FileLocation getLocation() {
		return new FileLocation(filename, lineNum, charNum);
	}
	
	private void fill() {
		try {
			if (currentChar == -1)
				currentChar = reader.read();
		} catch (IOException e) {
			ToolError.reportError(ErrorMessage.LEXER_ERROR, HasLocation.UNKNOWN);
		}
	}	
}
