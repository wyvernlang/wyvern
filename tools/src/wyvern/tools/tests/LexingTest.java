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

import wyvern.tools.lexer.Lexer;
import wyvern.tools.lexer.Token;

public class LexingTest {
	@Rule
    public ExpectedException thrown= ExpectedException.none();

	@Test
	public void testSimpleLine() {
		Reader reader = new StringReader("hel_lo \t  \t\t w0rld  ");
		Lexer lexer = new Lexer(reader);
		
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		Assert.assertEquals(token("hel_lo"), lexer.getToken());
		Assert.assertEquals(token("w0rld"), lexer.getToken());
		Assert.assertEquals(Token.getEOF(), lexer.getToken());
	}

	@Test
	public void testNumber() {
		Reader reader = new StringReader("one 2 3 four");
		Lexer lexer = new Lexer(reader);
		
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		Assert.assertEquals(token("one"), lexer.getToken());
		Assert.assertEquals(Token.getNumber("2"), lexer.getToken());
		Assert.assertEquals(Token.getNumber("3"), lexer.getToken());
		Assert.assertEquals(token("four"), lexer.getToken());
		Assert.assertEquals(Token.getEOF(), lexer.getToken());
	}

	@Test
	public void testLines() {
		Reader reader = new StringReader("hel_lo \t \n \t  \nw0rld");
		Lexer lexer = new Lexer(reader);
		
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		Assert.assertEquals(token("hel_lo"), lexer.getToken());
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		Assert.assertEquals(token("w0rld"), lexer.getToken());
		Assert.assertEquals(Token.getEOF(), lexer.getToken());
	}

	@Test
	public void testIndents() {
		Reader reader = new StringReader("hel_lo \t \n " +
										 "\t  \n" +
										 "w0rld\n" +
										 "\tone\n\t two \t\n\tone\nback\n \t" +
										 "\n");
		Lexer lexer = new Lexer(reader);
		
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		Assert.assertEquals(token("hel_lo"), lexer.getToken());
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		Assert.assertEquals(token("w0rld"), lexer.getToken());
		Assert.assertEquals(Token.getINDENT(), lexer.getToken());
		Assert.assertEquals(token("one"), lexer.getToken());
		Assert.assertEquals(Token.getINDENT(), lexer.getToken());
		Assert.assertEquals(token("two"), lexer.getToken());
		Assert.assertEquals(Token.getDEDENT(), lexer.getToken());
		Assert.assertEquals(token("one"), lexer.getToken());
		Assert.assertEquals(Token.getDEDENT(), lexer.getToken());
		Assert.assertEquals(token("back"), lexer.getToken());
		Assert.assertEquals(Token.getEOF(), lexer.getToken());
	}
	
	@Test
	public void testIndents2() {
		Reader reader = new StringReader("hel_lo\n\tw0rld\n\t\tw1rld\nback");
		Lexer lexer = new Lexer(reader);
		
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		Assert.assertEquals(token("hel_lo"), lexer.getToken());
		Assert.assertEquals(Token.getINDENT(), lexer.getToken());
		Assert.assertEquals(token("w0rld"), lexer.getToken());
		Assert.assertEquals(Token.getINDENT(), lexer.getToken());
		Assert.assertEquals(token("w1rld"), lexer.getToken());
		Assert.assertEquals(Token.getDEDENT(), lexer.getToken());
		Assert.assertEquals(Token.getDEDENT(), lexer.getToken());
		Assert.assertEquals(token("back"), lexer.getToken());
		Assert.assertEquals(Token.getEOF(), lexer.getToken());
	}

	// caught a bug in an earlier version of the lexer
	@Test
	public void testSpacedIndent() {
		Reader reader = new StringReader("class\n"
										+"    field\n"
										+"3");
		Lexer lexer = new Lexer(reader);
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		Assert.assertEquals(token("class"), lexer.getToken());
		Assert.assertEquals(Token.getINDENT(), lexer.getToken());
		Assert.assertEquals(token("field"), lexer.getToken());
		Assert.assertEquals(Token.getDEDENT(), lexer.getToken());
		Assert.assertEquals(Token.getNumber("3"), lexer.getToken());
		Assert.assertEquals(Token.getEOF(), lexer.getToken());
	}

	@Test
	public void testSymbols() {
		Reader reader = new StringReader("hello+1++3/[{}](-x)");
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
	
	@Test 
	public void testComments() {
		Reader reader = new StringReader("hel_lo" +
										 "//world\n" +
										 "/*Hello\n world!*/" +
										 "w0rld");
		Lexer lexer = new Lexer(reader);
		
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		Assert.assertEquals(token("hel_lo"), lexer.getToken());
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		Assert.assertEquals(token("w0rld"), lexer.getToken());
		Assert.assertEquals(Token.getEOF(), lexer.getToken());
	}
	
	@Test 
	public void testComments2() {
		Reader reader = new StringReader("hel_lo//world\n\tw0rld\n\tw0rld2");
		Lexer lexer = new Lexer(reader);
		
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		Assert.assertEquals(token("hel_lo"), lexer.getToken());
		Assert.assertEquals(Token.getINDENT(), lexer.getToken());
		Assert.assertEquals(token("w0rld"), lexer.getToken());
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		Assert.assertEquals(token("w0rld2"), lexer.getToken());
		Assert.assertEquals(Token.getEOF(), lexer.getToken());
	}
	
	@Test
	public void testStrings() {
		Reader reader = new StringReader("hel_lo\n\"world\"\nw0rld");
		Lexer lexer = new Lexer(reader);
		
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		Assert.assertEquals(token("hel_lo"), lexer.getToken());
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		Assert.assertEquals(Token.getString("world"), lexer.getToken());
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		Assert.assertEquals(token("w0rld"), lexer.getToken());
		Assert.assertEquals(Token.getEOF(), lexer.getToken());
	}
	
	@Test
	public void testStrings2() {
		Reader reader = new StringReader("hel_lo\n\"world\"+\"hello\"\nw0rld");
		Lexer lexer = new Lexer(reader);
		
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		Assert.assertEquals(token("hel_lo"), lexer.getToken());
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		Assert.assertEquals(Token.getString("world"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("+"), lexer.getToken());
		Assert.assertEquals(Token.getString("hello"), lexer.getToken());
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		Assert.assertEquals(token("w0rld"), lexer.getToken());
		Assert.assertEquals(Token.getEOF(), lexer.getToken());
	}
	
	@Test
	public void testCodeAldrich() throws IOException {
		InputStream is = LexingTest.class.getClassLoader().getResource("wyvern/tools/tests/samples/Stack.wyv").openStream();
		Reader reader = new InputStreamReader(is);
		Lexer lexer = new Lexer(reader);
		
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("interface"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("Stack"), lexer.getToken());
		Assert.assertEquals(Token.getINDENT(), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("type"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("T"), lexer.getToken());
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("prop"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("top"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier(":"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("T"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("?"), lexer.getToken());
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("def"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("push"), lexer.getToken());
		Assert.assertEquals(Token.getGroup('('), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("T"), lexer.getToken());
		Assert.assertEquals(Token.getGroup(')'), lexer.getToken());
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("def"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("pop"), lexer.getToken());
		Assert.assertEquals(Token.getGroup('('), lexer.getToken());
		Assert.assertEquals(Token.getGroup(')'), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier(":"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("T"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("?"), lexer.getToken());
		Assert.assertEquals(Token.getDEDENT(), lexer.getToken());
		

		Assert.assertEquals(Token.getIdentifier("class"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("StackImpl"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("implements"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("Stack"), lexer.getToken());
		Assert.assertEquals(Token.getINDENT(), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("var"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("list"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier(":"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("T"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("Link"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("?"), lexer.getToken());
		

		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("def"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("top"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("="), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("list"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("."), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("data"), lexer.getToken());
		
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("def"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("push"), lexer.getToken());
		Assert.assertEquals(Token.getGroup('('), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("x"), lexer.getToken());
		Assert.assertEquals(Token.getGroup(')'), lexer.getToken());
		Assert.assertEquals(Token.getINDENT(), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("list"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("="), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("Link"), lexer.getToken());
		Assert.assertEquals(Token.getGroup('('), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("x"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier(","), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("link"), lexer.getToken());
		Assert.assertEquals(Token.getGroup(')'), lexer.getToken());
		Assert.assertEquals(Token.getDEDENT(), lexer.getToken());
		

		Assert.assertEquals(Token.getIdentifier("def"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("pop"), lexer.getToken());
		Assert.assertEquals(Token.getGroup('('), lexer.getToken());
		Assert.assertEquals(Token.getGroup(')'), lexer.getToken());
		Assert.assertEquals(Token.getINDENT(), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("val"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("result"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("="), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("list"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("."), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("data"), lexer.getToken());
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("list"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("="), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("list"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("."), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("next"), lexer.getToken());
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("result"), lexer.getToken());
		

		Assert.assertEquals(Token.getDEDENT(), lexer.getToken());
		Assert.assertEquals(Token.getDEDENT(), lexer.getToken());
		

		Assert.assertEquals(Token.getIdentifier("class"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("Link"), lexer.getToken());
		Assert.assertEquals(Token.getINDENT(), lexer.getToken());
		
		Assert.assertEquals(Token.getIdentifier("type"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("T"), lexer.getToken());
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());

		Assert.assertEquals(Token.getIdentifier("val"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("data"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier(":"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("T"), lexer.getToken());
		Assert.assertEquals(Token.getNEWLINE(), lexer.getToken());
		

		Assert.assertEquals(Token.getIdentifier("val"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("next"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier(":"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("T"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("Link"), lexer.getToken());
		Assert.assertEquals(Token.getIdentifier("?"), lexer.getToken());
		Assert.assertEquals(Token.getEOF(), lexer.getToken());
		
		is.close();
	}

	private Token token(String string) {
		return Token.getIdentifier(string);
	}	
}
