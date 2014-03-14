package wyvern2.parsing.tests;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import org.junit.Assert;
import org.junit.Test;
import wyvern.stdlib.Globals;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.extensions.Int;
import wyvern2.parsing.Wyvern;

import java.io.IOException;
import java.io.StringReader;

public class CopperTests {
	@Test
	public void testVal() throws IOException, CopperParserException {
		String input = "val yx = 2\nyx";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		res.typecheck(Environment.getEmptyEnvironment());
		Value v = res.evaluate(Environment.getEmptyEnvironment());
		Assert.assertEquals(v.toString(), "IntegerConstant(2)");
	}
	@Test
	public void testAdd() throws IOException, CopperParserException {
		String input = "val yx = 2\nval ts = 9\nyx+ts";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		res.typecheck(Environment.getEmptyEnvironment());
		Value v = res.evaluate(Environment.getEmptyEnvironment());
		Assert.assertEquals(v.toString(), "IntegerConstant(11)");
	}
	@Test
	public void testMult() throws IOException, CopperParserException {
		String input = "val yx = 2\nval ts = 9+7*2\nyx+ts";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		res.typecheck(Environment.getEmptyEnvironment());
		Value v = res.evaluate(Environment.getEmptyEnvironment());
		Assert.assertEquals(v.toString(), "IntegerConstant(25)");
	}
	@Test
	public void testParens() throws IOException, CopperParserException {
		String input = "val yx = 2\nval ts = 9+(5+2)*2\nyx+ts";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		res.typecheck(Environment.getEmptyEnvironment());
		Value v = res.evaluate(Environment.getEmptyEnvironment());
		Assert.assertEquals(v.toString(), "IntegerConstant(25)");
	}
	@Test
	public void testDecls() throws IOException, CopperParserException {
		String input =
				"def foo():Int = 5\n" +
						"def bar():Int\n" +
						"	9\n" +
						"bar()";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv()), Int.getInstance());
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "IntegerConstant(9)");
	}
	@Test
	public void testDeclParams() throws IOException, CopperParserException {
		String input =
				"def foo(x:Int):Int = 5+x\n" +
						"foo(7)";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv()), Int.getInstance());
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "IntegerConstant(12)");
	}
	@Test
	public void testDeclParams2() throws IOException, CopperParserException {
		String input =
				"def foo(x:Int,y:Int):Int = 5+x*y\n" +
						"foo(7,2)";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv()), Int.getInstance());
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "IntegerConstant(19)");
	}
	@Test
	public void testFwdDecls() throws IOException, CopperParserException {
		String input =
				"def foo():Int = bar()+20\n" +
						"def bar():Int\n" +
						"	9\n" +
						"foo()";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv()), Int.getInstance());
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "IntegerConstant(29)");
	}
	@Test
	public void testClass() throws IOException, CopperParserException {
		String input =
				"class Hello\n" +
				"6";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv()), Int.getInstance());
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "IntegerConstant(6)");
	}

	@Test
	public void testClass2()  throws IOException, CopperParserException {
		String input =
				"class Hello\n" +
				"	def foo():Int = 7\n" +
				"	val bar:Int = 19\n" +
				"6";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv()), Int.getInstance());
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "IntegerConstant(6)");
	}

	@Test
	public void parseSimpleClass() throws IOException, CopperParserException {
		String input =
				"class C\n" +
				"  def bar():Int\n" +
				"    9\n" +
				"5";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv()), Int.getInstance());
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "IntegerConstant(5)");
	}
	
	@Test
	public void parseClassWithNew() throws IOException, CopperParserException {
		String input =
				"class C\n" +
				"  def bar():Int\n" +
				"    9\n" +
				"  class def create():C\n" +
				"    new\n" +
				"5";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv()), Int.getInstance());
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "IntegerConstant(5)");
	}
	
	@Test
	public void testSimpleClass() throws IOException, CopperParserException {
		String input =
				"class C\n" +
				"  def bar():Int\n" +
				"    9\n" +
				"  class def create():C\n" +
				"    new\n" +
				"C.create().bar()";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv()), Int.getInstance());
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "IntegerConstant(9)");
	}
	
	@Test
	public void parseSimpleType() throws IOException, CopperParserException {
		String input =
				"type T\n" +
				"  def bar():Int\n" +
				"5";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv()), Int.getInstance());
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "IntegerConstant(5)");
	}
	
	@Test
	public void testSimpleType() throws IOException, CopperParserException {
		String input =
				"type T\n" +
				"  def bar():Int\n" +
				"class C\n" +
				"  def bar():Int\n" +
				"    9\n" +
				"  class def create():T\n" +
				"    new\n" +
				"C.create().bar()";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv()), Int.getInstance());
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "IntegerConstant(9)");
	}
	
	@Test
	public void testSimpleMetadata() throws IOException, CopperParserException {
		String input =
				"class C\n" +
				"  def bar():Int\n" +
				"    9\n" +
				"  class def create():T\n" +
				"    new\n" +
				"type T\n" +
				"  def foo():Int\n" +
				"  metadata = C.create()\n" +
				"T.bar()";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv()), Int.getInstance());
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "IntegerConstant(9)");
	}
}
