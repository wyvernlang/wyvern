package wyvern.tools.parsing.coreparser;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import edu.umn.cs.melt.copper.runtime.engines.single.SingleDFAEngine;
import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.lexing.LexerUtils;

public class WyvernTokenManager<Lexer extends SingleDFAEngine<List<Token>, CopperParserException>, ParserConstants> implements TokenManager {
    private Constructor<Lexer> lexerCtor;
    private Class<ParserConstants> parserConstantsClass;

    private Reader input;
    private String filename;
    private Iterator<Token> tokens;
    private Token specialToken;

    public WyvernTokenManager(Reader input, String filename, Class<Lexer> lexerClass, Class<ParserConstants> parserConstantsClass) {
        try {
            this.lexerCtor = lexerClass.getConstructor();
            this.parserConstantsClass = parserConstantsClass;

            this.input = input;
            this.filename = filename;
            this.tokens = null;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void readTokenList() throws CopperParserException, IOException, InstantiationException,
    IllegalAccessException, InvocationTargetException {
        List<Token> tokenList = this.lexerCtor.newInstance().parse(input, filename);
        tokens = tokenList.iterator();
    }

    @Override
    public Token getNextToken() {
        if (tokens == null) {
            try {
                readTokenList();
            } catch (CopperParserException e) {
                ToolError.reportError(ErrorMessage.PARSE_ERROR, (FileLocation) null, e.getMessage());
                throw new RuntimeException(e);
            } catch (IOException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        while (tokens.hasNext()) {
            Token t = tokens.next();
            t.specialToken = specialToken;
            if (!LexerUtils.<ParserConstants>isSpecial(t, parserConstantsClass)) {
                specialToken = null;
                return t;
            }
            specialToken = t;
        }

        try {
            int eof = parserConstantsClass.getField("EOF").getInt(null);
            return new Token(eof);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public String getFilename() {
        return filename;
    }

}
