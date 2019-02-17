package wyvern.tools.parsing.coreparser;

import java.io.Reader;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.lexing.WyvernLexer;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Type;

public final class ParseUtils {
    /** Creates an instance of the new Wyvern parser
     *
     * @param filename
     * @param source
     * @return
     * @throws ParseException
     */
    private ParseUtils() { }

    public static WyvernParser<TypedAST, Type> makeParser(FileLocation startLocation, Reader source)
            throws ParseException {
        WyvernParser<TypedAST, Type> wp = new WyvernParser<TypedAST, Type>(
                new WyvernTokenManager<WyvernLexer, WyvernParserConstants>(source, startLocation, WyvernLexer.class,
                        WyvernParserConstants.class));
        wp.setBuilder(new WyvernASTBuilder());
        return wp;
    }
}
