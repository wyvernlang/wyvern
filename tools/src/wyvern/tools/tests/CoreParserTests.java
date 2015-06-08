package wyvern.tools.tests;

import java.io.Reader;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.parsing.coreparser.TokenManager;
import wyvern.tools.parsing.coreparser.WyvernParser;
import wyvern.tools.parsing.coreparser.WyvernTokenManager;

public class CoreParserTests {
	@Test
	public void testIdentity() throws ParseException {
		String input = "fn x : Int => x";
		//String input = "x.y( z)";
		Reader r = new StringReader(input);
		TokenManager tm = new WyvernTokenManager(r, "test input");
		WyvernParser wp = new WyvernParser(tm);
		WyvernParser.Expression();
	}
}
