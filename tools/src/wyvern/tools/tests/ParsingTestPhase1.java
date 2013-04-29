package wyvern.tools.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import wyvern.tools.rawAST.RawAST;
import wyvern.tools.simpleParser.Phase1Parser;

public class ParsingTestPhase1 {
	@Rule
    public ExpectedException thrown= ExpectedException.none();

	@Test
	public void testSimpleLine() {
		Reader reader = new StringReader("hel_lo \t  \t\t w0rld  ");
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		
		Assert.assertEquals("{$I {$L hel_lo w0rld $L} $I}", parsedResult.toString());
	}

	@Test
	public void testNumber() {
		Reader reader = new StringReader("one 2 3 four");
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		
		Assert.assertEquals("{$I {$L one 2 3 four $L} $I}", parsedResult.toString());
	}

	@Test
	public void testLines() {
		Reader reader = new StringReader("hel_lo \t \n \t  \nw0rld");
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		
		Assert.assertEquals("{$I {$L hel_lo $L} {$L w0rld $L} $I}", parsedResult.toString());
	}

	@Test
	public void testIndents() {
		Reader reader = new StringReader("\nhel_lo \t " +
				"\n \t  " +
				"\nw0rld" +
				"\n\tone" +
				"\n\t two \t" +
				"\n\tone" +
				"\nback\n \t");
		
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		
		String expected ="{$I {$L hel_lo $L}"
				+" {$L w0rld"
				+" {$I {$L one {$I {$L two $L} $I} $L}"
				+" {$L one $L} $I} $L}"
				+" {$L back $L} $I}";
		Assert.assertEquals(expected, parsedResult.toString());
	}
	
	@Test
	public void testSimpleIndent() {
		Reader reader = new StringReader("\nhel_lo \t " +
				"\n\tone" +
				"\n\ttwo" +
				"\nback\n \t");
		
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		
		String expected ="{$I {$L hel_lo"
				+" {$I {$L one $L}"
				+" {$L two $L} $I} $L}"
				+" {$L back $L} $I}";
		Assert.assertEquals(expected, parsedResult.toString());
	}
	
	@Test
	public void testSimpleIndent2() {
		Reader reader = new StringReader(
				"hi\n" +
				"	hello\n" +
				"		world\n" +
				"			today\n" +
				"	goodbye");
		
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		
		String expected ="{$I {$L hi {$I {$L hello {$I {$L world {$I {$L today $L} $I} $L} $I} $L} {$L goodbye $L} $I} $L} $I}";
		Assert.assertEquals(expected, parsedResult.toString());
	}
	
	@Test
	public void testCodeAldrich() throws IOException {
		InputStream is = ParsingTestPhase1.class.getClassLoader().getResource("wyvern/tools/tests/samples/Stack.wyv").openStream();
		Reader reader = new InputStreamReader(is);
		Phase1Parser.parse("Test", reader);
	}

	/*
	@Test
	public void testSymbols() {
		Reader reader = new StringReader("hello+1++3/[{+}b](-x)");
		Lexer lexer = new Lexer(reader);
		
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		Assert.assertEquals(token("hello"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("+"), lexer.getToken());
		Assert.assertEquals(Token.getNumber("1"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("++"), lexer.getToken());
		Assert.assertEquals(Token.getNumber("3"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("/"), lexer.getToken());
		Assert.assertEquals(Token.getGroup('['), lexer.getToken());
		Assert.assertEquals(Token.getGroup('{'), lexer.getToken());
		Assert.assertEquals(Token.getGroup('}'), lexer.getToken());
		Assert.assertEquals(Token.getGroup(']'), lexer.getToken());
		Assert.assertEquals(Token.getGroup('('), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("-"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("x"), lexer.getToken());
		Assert.assertEquals(Token.getGroup(')'), lexer.getToken());
		Assert.assertEquals(Token.getEOF(), lexer.getToken());
	}

	private Token token(String string) {
		return Token.getIdentifier(string);
	}
	*/
}