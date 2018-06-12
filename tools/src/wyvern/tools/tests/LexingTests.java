package wyvern.tools.tests;

import static wyvern.tools.parsing.coreparser.WyvernParserConstants.CHARACTER_LITERAL;
import static wyvern.tools.parsing.coreparser.WyvernParserConstants.DASH;
import static wyvern.tools.parsing.coreparser.WyvernParserConstants.DATATYPE;
import static wyvern.tools.parsing.coreparser.WyvernParserConstants.DEDENT;
import static wyvern.tools.parsing.coreparser.WyvernParserConstants.DEF;
import static wyvern.tools.parsing.coreparser.WyvernParserConstants.DIVIDE;
import static wyvern.tools.parsing.coreparser.WyvernParserConstants.DSLLINE;
import static wyvern.tools.parsing.coreparser.WyvernParserConstants.IDENTIFIER;
import static wyvern.tools.parsing.coreparser.WyvernParserConstants.INDENT;
import static wyvern.tools.parsing.coreparser.WyvernParserConstants.LPAREN;
import static wyvern.tools.parsing.coreparser.WyvernParserConstants.MULT;
import static wyvern.tools.parsing.coreparser.WyvernParserConstants.MULTI_LINE_COMMENT;
import static wyvern.tools.parsing.coreparser.WyvernParserConstants.NEWLINE;
import static wyvern.tools.parsing.coreparser.WyvernParserConstants.PLUS;
import static wyvern.tools.parsing.coreparser.WyvernParserConstants.RPAREN;
import static wyvern.tools.parsing.coreparser.WyvernParserConstants.SINGLE_LINE_COMMENT;
import static wyvern.tools.parsing.coreparser.WyvernParserConstants.STRING_LITERAL;
import static wyvern.tools.parsing.coreparser.WyvernParserConstants.TILDE;
import static wyvern.tools.parsing.coreparser.WyvernParserConstants.TYPE;
import static wyvern.tools.parsing.coreparser.WyvernParserConstants.WHITESPACE;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.tools.errors.ToolError;
import wyvern.tools.lexing.WyvernLexer;
import wyvern.tools.parsing.coreparser.Token;
import wyvern.tools.tests.suites.RegressionTests;

@Category(RegressionTests.class)
public class LexingTests {
    public static String kindToName(int kind) {
        switch (kind) {
        case IDENTIFIER: return "IDENTIFIER";
        case WHITESPACE: return "WHITESPACE";
        case DSLLINE: return "DSLLINE";
        case DEDENT: return "DEDENT";
        case INDENT: return "INDENT";
        case PLUS: return "PLUS";
        case DASH: return "DASH";
        case MULT: return "MULT";
        case DIVIDE: return "DIVIDE";
        case SINGLE_LINE_COMMENT: return "SINGLE_LINE_COMMENT";
        case MULTI_LINE_COMMENT: return "MULTI_LINE_COMMENT";
        case NEWLINE: return "NEWLINE";
        default: return "UNKNOWN(" + kind + ")";
        }
    }

    public String concat(List<Token> tokens) {
        StringBuffer buf = new StringBuffer();
        for (Token t:tokens) {
            buf.append(t.image);
        }
        return buf.toString();
    }

    public void checkKinds(int[] kinds, List<Token> tokens) {
        int index = 0;
        for (Token t: tokens) {
            if (index >= kinds.length) {
                Assert.fail("more tokens than expected");
            }
            int k = kinds[index++];
            if (k != t.kind) {
                Assert.fail("expected " + kindToName(k) + " but was " + kindToName(t.kind) + " at " + (index - 1));
            }
        }
        Assert.assertEquals("Not enough tokens: " + (kinds.length - tokens.size()) + " missing", kinds.length, tokens.size());
    }

    public List<Token> tryLex(String input) throws IOException, CopperParserException {
        return new WyvernLexer().parse(new StringReader(input), "test input");
    }

    public List<Token> checkKindsFromLex(String input, int[] kinds) throws IOException, CopperParserException {
        List<Token> tokens = tryLex(input);
        checkKinds(kinds, tokens);
        return tokens;
    }

    public List<Token> checkLex(String input, int[] kinds) throws IOException, CopperParserException {
        List<Token> tokens = checkKindsFromLex(input, kinds);
        Assert.assertEquals(input, concat(tokens));
        return tokens;
    }


    /************************ TESTS HERE *****************************/

    @Test
    public void testComments1() throws IOException, CopperParserException {
        String input =
                  "exn1\n"
                + "\n"
                + "// foo\n"
                + "\n"
                + "exn2\n";
        int[] expected = new int[] {
                IDENTIFIER, WHITESPACE, NEWLINE,
                WHITESPACE,
                SINGLE_LINE_COMMENT, WHITESPACE,
                WHITESPACE,
                IDENTIFIER, WHITESPACE, NEWLINE,
        };
        checkLex(input, expected);
    }
    @Test
    public void testDSLBlock1() throws IOException, CopperParserException {
        String input =
                "foo(~)\n"
                      + "  DSL here!\n"
                      + "bar()\n";
        int[] expected = new int[] {
                IDENTIFIER, LPAREN, TILDE, RPAREN, WHITESPACE, NEWLINE,
                WHITESPACE, DSLLINE,
                IDENTIFIER, LPAREN, RPAREN, WHITESPACE, NEWLINE,
        };
        checkLex(input, expected);
    }
    @Test
    public void testDSLBlock2() throws IOException, CopperParserException {
        String input =
                "foo(~)\n"
                      + "  DSL here!\n"
                      + "  and here!\n"
                      + "bar()\n";
        int[] expected = new int[] {
                IDENTIFIER, LPAREN, TILDE, RPAREN, WHITESPACE, NEWLINE,
                WHITESPACE, DSLLINE,
                WHITESPACE, DSLLINE,
                IDENTIFIER, LPAREN, RPAREN, WHITESPACE, NEWLINE,
        };
        checkLex(input, expected);
    }
    @Test
    public void testDSLEOF() throws IOException, CopperParserException {
        String input =
                "foo(~)\n"
                      + "  DSL here!\n"
                      + "  and here!\n";
        int[] expected = new int[] {
                IDENTIFIER, LPAREN, TILDE, RPAREN, WHITESPACE, NEWLINE,
                WHITESPACE, DSLLINE,
                WHITESPACE, DSLLINE,
        };
        checkLex(input, expected);
    }
    @Test
    public void testIndentBlock() throws IOException, CopperParserException {
        String input =
                "def()\n"
                      + "  baz()\n"
                      + "  bam()\n"
                      + "bar()\n";
        int[] expected = new int[] {
                DEF, LPAREN, RPAREN, WHITESPACE, NEWLINE,
                INDENT, WHITESPACE, IDENTIFIER, LPAREN, RPAREN, WHITESPACE, NEWLINE,
                WHITESPACE, IDENTIFIER, LPAREN, RPAREN, WHITESPACE, NEWLINE,
                DEDENT, IDENTIFIER, LPAREN, RPAREN, WHITESPACE, NEWLINE,
        };
        List<Token> tokens = checkLex(input, expected);
        // check location information
        Token bamToken = tokens.get(13);
        Assert.assertEquals("bam", bamToken.image);
        Assert.assertEquals(3, bamToken.beginLine);
        Assert.assertEquals(3, bamToken.beginColumn);
    }
    @Test
    public void testSimpleMultiComment() throws IOException, CopperParserException {
        String input =
                "foo /*ha*/ bar\n";
        int[] expected = new int[] {
                IDENTIFIER, WHITESPACE, MULTI_LINE_COMMENT,
                WHITESPACE, IDENTIFIER, WHITESPACE, NEWLINE,
        };
        checkLex(input, expected);
    }
    @Test
    public void testIndentComment() throws IOException, CopperParserException {
        String input =
                "def\n"
                      + "  baz  /* \n"
                      + " */ bam\n"
                      + "  bag\n";
        int[] expected = new int[] {
                DEF, WHITESPACE, NEWLINE,
                INDENT, WHITESPACE, IDENTIFIER, WHITESPACE, MULTI_LINE_COMMENT,
                WHITESPACE, IDENTIFIER, WHITESPACE, NEWLINE,
                WHITESPACE, IDENTIFIER, WHITESPACE, NEWLINE, DEDENT,
        };
        checkLex(input, expected);
    }
    @Test
    public void testIndentParen() throws IOException, CopperParserException {
        String input =
                "def\n"
                      + "  baz  ( \n"
                      + " knarl) bam\n"
                      + "  bag\n";
        int[] expected = new int[] {
                DEF, WHITESPACE, NEWLINE,
                INDENT, WHITESPACE, IDENTIFIER, WHITESPACE, LPAREN, WHITESPACE, WHITESPACE,
                WHITESPACE, IDENTIFIER, RPAREN, WHITESPACE, IDENTIFIER, WHITESPACE, NEWLINE,
                WHITESPACE, IDENTIFIER, WHITESPACE, NEWLINE, DEDENT,
        };
        checkLex(input, expected);
    }
    @Test
    public void testContinuationAndEOFDedent() throws IOException, CopperParserException {
        String input =
                "def\\\n"
                      + "  bar\n"
                      + "  bam\n";
        int[] expected = new int[] {
                DEF, WHITESPACE,
                WHITESPACE, IDENTIFIER, WHITESPACE, NEWLINE,
                INDENT, WHITESPACE, IDENTIFIER, WHITESPACE, NEWLINE, DEDENT,
        };
        checkLex(input, expected);
    }
    @Test
    public void testContinuationAndEOFDedent2() throws IOException, CopperParserException {
        String input =
                "def\\\n"
                      + "  bar\n"
                      + "  bam";
        int[] expected = new int[] {
                DEF, WHITESPACE,
                WHITESPACE, IDENTIFIER, WHITESPACE, NEWLINE,
                INDENT, WHITESPACE, IDENTIFIER, NEWLINE, DEDENT,
        };
        checkLex(input, expected);
    }
    @Test
    public void testEmpty() throws IOException, CopperParserException {
        String input = "";
        int[] expected = new int[] {};
        checkLex(input, expected);
    }
    @Test
    public void testWS() throws IOException, CopperParserException {
        String input = " ";
        int[] expected = new int[] {WHITESPACE};
        checkLex(input, expected);
    }
    @Test
    public void testNL() throws IOException, CopperParserException {
        String input = "\n";
        int[] expected = new int[] {WHITESPACE};
        checkLex(input, expected);
    }
    @Test(expected = CopperParserException.class)
    public void testMissingDSL() throws IOException, CopperParserException {
        String input =
                " foo(~)\n"
                      + " nodsl\n";
        tryLex(input);
    }
    @Test(expected = CopperParserException.class)
    public void badIndent() throws IOException, CopperParserException {
        String input =
                "def\n"
                      + " type\n"
                      + "\t baz\n";
        tryLex(input);
    }
    @Test(expected = ToolError.class)
    public void badDedent() throws IOException, CopperParserException {
        String input =
                "def\n"
                      + "  bar\n"
                      + " baz\n";
        tryLex(input);
    }
    @Test
    public void twoDedents() throws IOException, CopperParserException {
        String input =
                "def\n"
                      + " type\n"
                      + "  bam\n"
                      + "baz\n";
        int[] expected = new int[] {
                DEF, WHITESPACE, NEWLINE,
                INDENT, WHITESPACE, TYPE, WHITESPACE, NEWLINE,
                INDENT, WHITESPACE, IDENTIFIER, WHITESPACE, NEWLINE,
                DEDENT, DEDENT, IDENTIFIER, WHITESPACE, NEWLINE,
        };
        checkLex(input, expected);
    }

    @Test
    public void datatypeDedents() throws IOException, CopperParserException {
        String input =
                "def\n"
                        + " datatype\n"
                        + "  bam\n"
                        + "baz\n";
        int[] expected = new int[] {
                DEF, WHITESPACE, NEWLINE,
                INDENT, WHITESPACE, DATATYPE, WHITESPACE, NEWLINE,
                INDENT, WHITESPACE, IDENTIFIER, WHITESPACE, NEWLINE,
                DEDENT, DEDENT, IDENTIFIER, WHITESPACE, NEWLINE,
        };
        checkLex(input, expected);
    }

    @Test(expected = CopperParserException.class)
    public void testMissingDSL2() throws IOException, CopperParserException {
        String input =
                "foo(~)\n"
                      + "nodsl\n";
        tryLex(input);
    }
    @Test
    public void testIndentedProgram() throws IOException, CopperParserException {
        String input =
                "  bar\n"
                      + "  bam";
        int[] expected = new int[] {
                INDENT, WHITESPACE, IDENTIFIER, WHITESPACE, NEWLINE,
                WHITESPACE, IDENTIFIER, NEWLINE, DEDENT,
        };
        checkLex(input, expected);
    }

    @Test
    public void testCharacterLiterals() throws IOException, CopperParserException {
        String input = "#'a'#\"b\"\"c\"'d'";
        int[] expected = new int[] {CHARACTER_LITERAL, CHARACTER_LITERAL, STRING_LITERAL, STRING_LITERAL};
        checkKindsFromLex(input, expected);
    }
}
