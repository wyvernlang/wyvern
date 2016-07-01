package wyvern.tools.parsing.coreparser;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.lexing.LexerUtils;
import wyvern.tools.lexing.WyvernLexer;

public class WyvernTokenManager implements TokenManager {
	private Reader input;
	private String filename;
	private Iterator<Token> tokens;
	private Token specialToken;
	
	
	public WyvernTokenManager(Reader input, String filename) {
		this.input = input;
		this.filename = filename;
		this.tokens = null;
	}
	
	private void readTokenList() throws CopperParserException, IOException {
		tokens = new WyvernLexer().parse(input, filename).iterator();
	}

	@Override
	public Token getNextToken() {
		if (tokens == null)
			try {
				readTokenList();
			} catch (CopperParserException e) {
				ToolError.reportError(ErrorMessage.PARSE_ERROR, (FileLocation)null, e.getMessage());
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		while (tokens.hasNext()) {
			Token t = tokens.next();
			t.specialToken = specialToken;
			if (!LexerUtils.isSpecial(t)) {
				specialToken = null;
				return t;
			}
			specialToken = t;
		}
		
		return new Token(WyvernParserConstants.EOF);
	}
	
	public String getFilename() {
		return filename;
	}

}
