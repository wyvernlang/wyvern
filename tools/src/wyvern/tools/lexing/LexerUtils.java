package wyvern.tools.lexing;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.coreparser.Token;

public final class LexerUtils {
    private LexerUtils() { }
    public static <T> boolean isSpecial(Token t, Class<T> tClass) {
        try {
            int singleLineComment = tClass.getField("SINGLE_LINE_COMMENT").getInt(null);
            int multiLineComment = tClass.getField("MULTI_LINE_COMMENT").getInt(null);
            int whitespace = tClass.getField("WHITESPACE").getInt(null);

            if ((t.kind == singleLineComment) || (t.kind == multiLineComment) || (t.kind == whitespace)) {
                return true;
            }

            return false;
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /** convenient construction function for tokens.  The location of
     * the new token is taken from the tokenLoc argument
     */
    public static Token makeToken(int kind, String s, Token tokenLoc) {
        return makeToken(kind, s, tokenLoc.beginLine, tokenLoc.beginColumn);
    }

    /** convenient construction function for tokens.
     */
    public static Token makeToken(int kind, String s, int beginLine, int beginColumn) {
        Token t = new Token(kind, s);
        t.beginLine = beginLine;
        t.beginColumn = beginColumn;
        return t;
    }

    /** Constructor for an empty list of Tokens.
     */
    public static List<Token> emptyList() {
        return new LinkedList<Token>();
    }

    public static FileLocation loc(String fileName, Token t) {
        return new FileLocation(fileName, t.beginLine, t.beginColumn);
    }

    /** @return 1 for an indent, -n for n dedents, or 0 for the same indentation level
     */
    public static int adjustIndent(String newIndent, Token tokenLoc, String fileName, Stack<String> indents)
            throws CopperParserException {
        String currentIndent = indents.peek();
        if (newIndent.length() < currentIndent.length()) {
            // dedent(s)
            int dedentCount = 0;
            while (newIndent.length() < currentIndent.length()) {
                indents.pop();
                currentIndent = indents.peek();
                dedentCount--;
            }
            if (newIndent.equals(currentIndent)) {
                return dedentCount;
            } else {
                ToolError.reportError(ErrorMessage.INCONSISTENT_INDENT, loc(fileName, tokenLoc));
                // unreachable because reportError will throw. But the compiler doesn't know that.
                // So, we throw an exception to get it to stop complaining.
                throw new RuntimeException();
            }
        } else if (newIndent.length() > currentIndent.length()) {
            // indent
            if (newIndent.startsWith(currentIndent)) {
                indents.push(newIndent);
                return 1;
            } else {
                throw new CopperParserException("Illegal indent at line " + tokenLoc.beginLine
                        + ": not a superset of previous indent level");
            }
        } else {
            return 0;
        }
    }

    /** Adjusts the indentation level to the baseline (no indent)
     * @return the list of dedent tokens that must be added to the
     * stream to reach the baseline indent (empty if no dedents)
     */
    public static <T> List<Token> possibleDedentList(Token tokenLoc, String fileName, Stack<String> indents, Class<T> tClass)
            throws CopperParserException {
        int levelChange = adjustIndent("", tokenLoc, fileName, indents);
        List<Token> tokenList = emptyList();

        try {
            int dedent = tClass.getField("DEDENT").getInt(null);

            while (levelChange < 0) {
                Token t = makeToken(dedent, "", tokenLoc);
                tokenList.add(t);
                levelChange++;
            }

            return tokenList;
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * creates indents/dedents at the beginning if necessary
     * creates a NEWLINE at the end if necessary
     */
    public static <T> LinkedList<Token> adjustLogicalLine(LinkedList<Token> aLine, String fileName,
            Stack<String> indents, Class<T> tClass)
                    throws CopperParserException {
        try {
            int dedent = tClass.getField("DEDENT").getInt(null);
            int indent = tClass.getField("INDENT").getInt(null);
            int newline = tClass.getField("NEWLINE").getInt(null);
            int whitespace = tClass.getField("WHITESPACE").getInt(null);

            if (LexerUtils.<T>hasNonSpecialToken(aLine, tClass)) {
                // it's a logical line...let's adjust it!

                // find the indent for this line
                Token firstToken = aLine.getFirst();
                String lineIndent = "";
                if (firstToken.kind == whitespace) {
                    lineIndent = firstToken.image;
                }

                // add indents/dedents as needed
                int levelChange = adjustIndent(lineIndent, firstToken, fileName, indents);
                if (levelChange == 1) {
                    aLine.addFirst(makeToken(indent, "", firstToken));
                }
                while (levelChange < 0) {
                    aLine.addFirst(makeToken(dedent, "", firstToken));
                    levelChange++;
                }

                // add a NEWLINE at the end
                Token lastToken = aLine.getLast();
                Token nl = makeToken(newline, "", lastToken);
                aLine.addLast(nl);
            }
            // ELSE do nothing: this line has only comments/whitespace

            return aLine;
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /** Constructor for a singleton list of Tokens.
     */
    public static List<Token> makeList(Token t) {
        List<Token> l = emptyList();
        l.add(t);
        return l;
    }

    /** @return true if there are tokens other than comments and whitespace in this token list
     */
    public static <T> boolean hasNonSpecialToken(List<Token> l, Class<T> tClass) {
        for (Token t : l) {
            if (!LexerUtils.<T>isSpecial(t, tClass)
                    /*&& t.image.trim().length() > 0 /* takes out "DSLs" that are just whitespace*/) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCommentsAndWhitespace(String s) {
        String simpler = s.trim();
        if (simpler.startsWith("/*")) {
            int loc = simpler.indexOf("*/");
            if (loc != -1) {
                simpler = simpler.substring(loc + 2);
            }
        }
        if (simpler.startsWith("//")) {
            int loc = simpler.indexOf("\n");
            if (loc != -1) {
                simpler = simpler.substring(loc + 1);
            } else {
                simpler = "";
            }
        }
        if (simpler.length() == 0) {
            return true;
        } else if (simpler.length() < s.length()) {
            return isCommentsAndWhitespace(simpler);
        } else {
            return false;
        }
    }
}
