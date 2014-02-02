package wyvern.tools.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import wyvern.tools.lex.ILexStream;
import wyvern.tools.lex.LexStream;
import wyvern.tools.lex.Token;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.simpleParser.Phase1Parser;

public class LexTest {
	@Rule
    public ExpectedException thrown= ExpectedException.none();

	@Test
	public void testSimpleLine() {
		Reader reader = new StringReader("hel_lo \t  \t\t w0rld  ");
		ILexStream lexer = new LexStream("Test", reader);
		
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		Assert.assertEquals(token("hel_lo"), lexer.next());
		Assert.assertEquals(token("w0rld"), lexer.next());
		Assert.assertEquals(Token.getEOF(), lexer.next());
	}

	@Test
	public void testNumber() {
		Reader reader = new StringReader("one 2 3 four");
		ILexStream lexer = new LexStream("Test", reader);
		
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		Assert.assertEquals(token("one"), lexer.next());
		Assert.assertEquals(Token.getNumber("2"), lexer.next());
		Assert.assertEquals(Token.getNumber("3"), lexer.next());
		Assert.assertEquals(token("four"), lexer.next());
		Assert.assertEquals(Token.getEOF(), lexer.next());
	}

	@Test
	public void testLines() {
		Reader reader = new StringReader("hel_lo \t \n \t  \nw0rld");
		ILexStream lexer = new LexStream("Test", reader);
		
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		Assert.assertEquals(token("hel_lo"), lexer.next());
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		Assert.assertEquals(token("w0rld"), lexer.next());
		Assert.assertEquals(Token.getEOF(), lexer.next());
	}

	@Test
	public void testIndents() {
		Reader reader = new StringReader("hel_lo \t \n " +
										 "\t  \n" +
										 "w0rld\n" +
										 "\tone\n\t two \t\n\tone\nback\n \t" +
										 "\n");
		ILexStream lexer = new LexStream("Test", reader);
		
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		Assert.assertEquals(token("hel_lo"), lexer.next());
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		Assert.assertEquals(token("w0rld"), lexer.next());
		Assert.assertEquals(Token.getINDENT(), lexer.next());
		Assert.assertEquals(token("one"), lexer.next());
		Assert.assertEquals(Token.getINDENT(), lexer.next());
		Assert.assertEquals(token("two"), lexer.next());
		Assert.assertEquals(Token.getDEDENT(), lexer.next());
		Assert.assertEquals(token("one"), lexer.next());
		Assert.assertEquals(Token.getDEDENT(), lexer.next());
		Assert.assertEquals(token("back"), lexer.next());
		Assert.assertEquals(Token.getEOF(), lexer.next());
	}
	
	@Test
	public void testIndents2() {
		Reader reader = new StringReader("hel_lo\n\tw0rld\n\t\tw1rld\nback");
		ILexStream lexer = new LexStream("Test", reader);
		
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		Assert.assertEquals(token("hel_lo"), lexer.next());
		Assert.assertEquals(Token.getINDENT(), lexer.next());
		Assert.assertEquals(token("w0rld"), lexer.next());
		Assert.assertEquals(Token.getINDENT(), lexer.next());
		Assert.assertEquals(token("w1rld"), lexer.next());
		Assert.assertEquals(Token.getDEDENT(), lexer.next());
		Assert.assertEquals(Token.getDEDENT(), lexer.next());
		Assert.assertEquals(token("back"), lexer.next());
		Assert.assertEquals(Token.getEOF(), lexer.next());
	}

	// caught a bug in an earlier version of the lexer
	@Test
	public void testSpacedIndent() {
		Reader reader = new StringReader("class\n"
										+"    field\n"
										+"3");
		ILexStream lexer = new LexStream("Test", reader);
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		Assert.assertEquals(token("class"), lexer.next());
		Assert.assertEquals(Token.getINDENT(), lexer.next());
		Assert.assertEquals(token("field"), lexer.next());
		Assert.assertEquals(Token.getDEDENT(), lexer.next());
		Assert.assertEquals(Token.getNumber("3"), lexer.next());
		Assert.assertEquals(Token.getEOF(), lexer.next());
	}

	@Test
	public void testSymbols() {
		Reader reader = new StringReader("hello+1++3/[{}](-x)");
		ILexStream lexer = new LexStream("Test", reader);
		
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		Assert.assertEquals(token("hello"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("+"), lexer.next());
		Assert.assertEquals(Token.getNumber("1"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("++"), lexer.next());
		Assert.assertEquals(Token.getNumber("3"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("/"), lexer.next());
		Assert.assertEquals(Token.getGroup('['), lexer.next());
		Assert.assertEquals(Token.getGroup('{'), lexer.next());
		Assert.assertEquals(Token.getGroup('}'), lexer.next());
		Assert.assertEquals(Token.getGroup(']'), lexer.next());
		Assert.assertEquals(Token.getGroup('('), lexer.next());
		Assert.assertEquals(Token.getIdentifier("-"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("x"), lexer.next());
		Assert.assertEquals(Token.getGroup(')'), lexer.next());
		Assert.assertEquals(Token.getEOF(), lexer.next());
	}
	
	@Test 
	public void testComments() {
		Reader reader = new StringReader("hel_lo" +
										 "//world\n" +
										 "/*Hello\n world!*/" +
										 "w0rld");
		ILexStream lexer = new LexStream("Test", reader);
		
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		Assert.assertEquals(token("hel_lo"), lexer.next());
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		Assert.assertEquals(token("w0rld"), lexer.next());
		Assert.assertEquals(Token.getEOF(), lexer.next());
	}
	
	@Test 
	public void testComments2() {
		Reader reader = new StringReader("hel_lo//world\n\tw0rld\n\tw0rld2");
		ILexStream lexer = new LexStream("Test", reader);
		
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		Assert.assertEquals(token("hel_lo"), lexer.next());
		Assert.assertEquals(Token.getINDENT(), lexer.next());
		Assert.assertEquals(token("w0rld"), lexer.next());
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		Assert.assertEquals(token("w0rld2"), lexer.next());
		Assert.assertEquals(Token.getEOF(), lexer.next());
	}
	
	@Test
	public void testStrings() {
		Reader reader = new StringReader("hel_lo\n\"world\"\nw0rld");
		ILexStream lexer = new LexStream("Test", reader);
		
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		Assert.assertEquals(token("hel_lo"), lexer.next());
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		Assert.assertEquals(Token.getString("world"), lexer.next());
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		Assert.assertEquals(token("w0rld"), lexer.next());
		Assert.assertEquals(Token.getEOF(), lexer.next());
	}
	
	@Test
	public void testStrings2() {
		Reader reader = new StringReader("hel_lo\n\"world\"+\"hello\"\nw0rld");
		ILexStream lexer = new LexStream("Test", reader);
		
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		Assert.assertEquals(token("hel_lo"), lexer.next());
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		Assert.assertEquals(Token.getString("world"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("+"), lexer.next());
		Assert.assertEquals(Token.getString("hello"), lexer.next());
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		Assert.assertEquals(token("w0rld"), lexer.next());
		Assert.assertEquals(Token.getEOF(), lexer.next());
	}
	
	@Test
	public void testSimpleIndent2() {
		ArrayList<ArrayList<Token>> tokensTokens = new ArrayList<ArrayList<Token>>();
		
		Reader reader = new StringReader(
				"hi\n" +
				"	hello\n" +
				"		world\n" +
				"			world\n" +
				"	goodbye\n" +
				"		hello\n");
		ILexStream lexer = new LexStream("Test", reader);
		
		ArrayList<Token> tokens = new ArrayList<Token>();
		int n = 0;
		Token token = lexer.next();
		while (token.kind != Token.Kind.EOF) {
			tokens.add(token);
			token = lexer.next();
			n++;
		}
		tokensTokens.add(tokens);
		int x = 2;
	}
	
	@Test
	public void testCodeAldrich() throws IOException {
		InputStream is = LexTest.class.getClassLoader().getResource("wyvern/tools/tests/samples/Stack.wyv").openStream();
		Reader reader = new InputStreamReader(is);
		ILexStream lexer = new LexStream("Test", reader);
		
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		Assert.assertEquals(Token.getIdentifier("interface"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("Stack"), lexer.next());
		Assert.assertEquals(Token.getINDENT(), lexer.next());
		Assert.assertEquals(Token.getIdentifier("type"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("T"), lexer.next());
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		Assert.assertEquals(Token.getIdentifier("prop"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("top"), lexer.next());
		Assert.assertEquals(Token.getIdentifier(":"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("T"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("?"), lexer.next());
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		Assert.assertEquals(Token.getIdentifier("def"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("push"), lexer.next());
		Assert.assertEquals(Token.getGroup('('), lexer.next());
		Assert.assertEquals(Token.getIdentifier("T"), lexer.next());
		Assert.assertEquals(Token.getGroup(')'), lexer.next());
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		Assert.assertEquals(Token.getIdentifier("def"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("pop"), lexer.next());
		Assert.assertEquals(Token.getGroup('('), lexer.next());
		Assert.assertEquals(Token.getGroup(')'), lexer.next());
		Assert.assertEquals(Token.getIdentifier(":"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("T"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("?"), lexer.next());
		Assert.assertEquals(Token.getDEDENT(), lexer.next());
		

		Assert.assertEquals(Token.getIdentifier("class"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("StackImpl"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("implements"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("Stack"), lexer.next());
		Assert.assertEquals(Token.getINDENT(), lexer.next());
		Assert.assertEquals(Token.getIdentifier("var"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("list"), lexer.next());
		Assert.assertEquals(Token.getIdentifier(":"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("T"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("Link"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("?"), lexer.next());
		

		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		Assert.assertEquals(Token.getIdentifier("def"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("top"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("="), lexer.next());
		Assert.assertEquals(Token.getIdentifier("list"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("."), lexer.next());
		Assert.assertEquals(Token.getIdentifier("data"), lexer.next());
		
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		Assert.assertEquals(Token.getIdentifier("def"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("push"), lexer.next());
		Assert.assertEquals(Token.getGroup('('), lexer.next());
		Assert.assertEquals(Token.getIdentifier("x"), lexer.next());
		Assert.assertEquals(Token.getGroup(')'), lexer.next());
		Assert.assertEquals(Token.getINDENT(), lexer.next());
		Assert.assertEquals(Token.getIdentifier("list"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("="), lexer.next());
		Assert.assertEquals(Token.getIdentifier("Link"), lexer.next());
		Assert.assertEquals(Token.getGroup('('), lexer.next());
		Assert.assertEquals(Token.getIdentifier("x"), lexer.next());
		Assert.assertEquals(Token.getIdentifier(","), lexer.next());
		Assert.assertEquals(Token.getIdentifier("link"), lexer.next());
		Assert.assertEquals(Token.getGroup(')'), lexer.next());
		Assert.assertEquals(Token.getDEDENT(), lexer.next());
		

		Assert.assertEquals(Token.getIdentifier("def"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("pop"), lexer.next());
		Assert.assertEquals(Token.getGroup('('), lexer.next());
		Assert.assertEquals(Token.getGroup(')'), lexer.next());
		Assert.assertEquals(Token.getINDENT(), lexer.next());
		Assert.assertEquals(Token.getIdentifier("val"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("result"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("="), lexer.next());
		Assert.assertEquals(Token.getIdentifier("list"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("."), lexer.next());
		Assert.assertEquals(Token.getIdentifier("data"), lexer.next());
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		Assert.assertEquals(Token.getIdentifier("list"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("="), lexer.next());
		Assert.assertEquals(Token.getIdentifier("list"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("."), lexer.next());
		Assert.assertEquals(Token.getIdentifier("next"), lexer.next());
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		Assert.assertEquals(Token.getIdentifier("result"), lexer.next());
		

		Assert.assertEquals(Token.getDEDENT(), lexer.next());
		Assert.assertEquals(Token.getDEDENT(), lexer.next());
		

		Assert.assertEquals(Token.getIdentifier("class"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("Link"), lexer.next());
		Assert.assertEquals(Token.getINDENT(), lexer.next());
		
		Assert.assertEquals(Token.getIdentifier("type"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("T"), lexer.next());
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());

		Assert.assertEquals(Token.getIdentifier("val"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("data"), lexer.next());
		Assert.assertEquals(Token.getIdentifier(":"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("T"), lexer.next());
		Assert.assertEquals(Token.getNEWLINE(), lexer.next());
		

		Assert.assertEquals(Token.getIdentifier("val"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("next"), lexer.next());
		Assert.assertEquals(Token.getIdentifier(":"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("T"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("Link"), lexer.next());
		Assert.assertEquals(Token.getIdentifier("?"), lexer.next());
		Assert.assertEquals(Token.getEOF(), lexer.next());
		
		is.close();
	}

	private Token token(String string) {
		return Token.getIdentifier(string);
	}	
}
