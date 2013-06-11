package wyvern.DSL.html.tests;

import java.io.Reader;
import java.io.StringReader;

import junit.framework.Assert;

import org.junit.Test;

import wyvern.DSL.html.Html;
import wyvern.stdlib.Globals;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.simpleParser.Phase1Parser;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;

public class ParsingTests {
	private TypedAST doCompile(String input) {
		Reader reader = new StringReader(input);
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Environment env = Globals.getStandardEnv();
		env = Html.extend(env);
		TypedAST typedAST = parsedResult.accept(BodyParser.getInstance(), env);
		Type resultType = typedAST.typecheck(env);
		return typedAST;
	}
	
	@Test
	public void testEmpty() {
		String testDat = "html\n";
		TypedAST ast = doCompile(testDat);
		Assert.assertEquals("StringConstant(\"<html>\n</html>\n\")",ast.toString());
	}
	
	@Test
	public void testBody() {
		String testStr = "html\n" +
						 "	body";
		TypedAST ast = doCompile(testStr);
		Value result = ast.evaluate(Globals.getStandardEnv());
		Assert.assertEquals("StringConstant(\"<html>\n<body>\n</body>\n</html>\n\")",result.toString());
		
	}
	
	@Test
	public void testDoc() {
		String testStr = "html\n" +
						 "	head\n" +
						 "	body";
		TypedAST ast = doCompile(testStr);
		Value result = ast.evaluate(Globals.getStandardEnv());
		Assert.assertEquals("StringConstant(\"<html>\n<head>\n</head>\n<body>\n</body>\n</html>\n\")",result.toString());
	}
	
	@Test
	public void testParameters() {
		String testStr = "html\n" +
						 "	head\n" +
						 "		title \"Hello World\"\n" +
						 "	body";
		TypedAST ast = doCompile(testStr);
		Value result = ast.evaluate(Globals.getStandardEnv());
		Assert.assertEquals("StringConstant(\"<html>\n<head>\n<title>Hello World</title>\n</head>\n<body>\n</body>\n</html>\n\")",result.toString());
	}
	
	@Test
	public void testVar() {
		String testStr = "val test : Str = \"Hello\"\n" +
						 "html\n" +
						 "	head\n" +
						 "		title test\n" +
						 "	body";
		TypedAST ast = doCompile(testStr);
		Value result = ast.evaluate(Globals.getStandardEnv());
		Assert.assertEquals("StringConstant(\"<html>\n<head>\n<title>Hello</title>\n</head>\n<body>\n</body>\n</html>\n\")",result.toString());
	}
	@Test
	public void testProps() {
		String testStr = 
				 "html\n" +
				 "	body\n" +
				 "		div\n" +
				 "			attrs\n" +
				 "				id=\"main\"\n" +
				 "				class=\"test\"\n" +
				 "			\"Hi\"";
		TypedAST ast = doCompile(testStr);
		Value result = ast.evaluate(Globals.getStandardEnv());
		Assert.assertEquals("StringConstant(\"<html>\n<body>\n<div id=\"main\" class=\"test\">\nHi\n</div>\n</body>\n</html>\n\")",result.toString());
	}
}
