package wyvern2.parsing.tests;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import org.junit.Assert;
import org.junit.Test;
import wyvern.DSL.DSL;
import wyvern.stdlib.Globals;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.ValDeclaration;
import wyvern.tools.typedAST.extensions.DSLLit;
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
	public void testClass3() throws IOException, CopperParserException {
		String input =
				"class Hello\n" +
						"	class def create():Hello = new\n" +
						"	def foo():Int = 7\n" +
						"	val bar:Int = 19\n" +
						"Hello.create().foo()";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv()), Int.getInstance());
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "IntegerConstant(7)");
	}

	@Test
	public void testClassMutual() throws IOException, CopperParserException {
		String input =
				"class Foo\n" +
				"	class def create():Foo = new\n" +
				"	def hello():Hello = Hello.create()\n" +
				"class Hello\n" +
				"	class def create():Hello = new\n" +
				"	def foo():Foo = Foo.create()\n" +
				"	val bar:Int = 19\n" +
				"Hello.create().bar";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv()), Int.getInstance());
		Assert.assertEquals("IntegerConstant(19)", res.evaluate(Globals.getStandardEnv()).toString());
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
				"  class def create():C\n" +
				"    new\n" +
				"  def bar():Int\n" +
				"    9\n" +
				"5";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv()), Int.getInstance());
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "IntegerConstant(5)");
	}
	
	@Test
	public void testSimpleClass() throws IOException, CopperParserException {
		String input =
				"class C\n" +
				"  class def create():C\n" +
				"    new\n" +
				"  def bar():Int\n" +
				"    9\n" +
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
				"  class def create():T\n" +
				"    new\n" +
				"  def bar():Int\n" +
				"    9\n" +
				"C.create().bar()";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv()), Int.getInstance());
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "IntegerConstant(9)");
	}
	
	@Test
	public void testSimpleMetadata() throws IOException, CopperParserException {
		String input =
				"class C\n" +
				"  class def create():T\n" +
				"    new\n" +
				"  def foo():Int\n" +
				"    9\n" +
				"  def bar():Int\n" +
				"    12\n" +
				"type T\n" +
				"  def foo():Int\n" +
				"  metadata:C = C.create()\n" +
				"T.bar()";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv()), Int.getInstance());
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "IntegerConstant(12)");
	}

	@Test
	public void testDSL1() throws IOException, CopperParserException {
		String input =
				"{ 1 { 2 } {3} 4 {5} {5 {6{{3}}}} }";
		DSLLit res = (DSLLit)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(" 1 { 2 } {3} 4 {5} {5 {6{{3}}}} ", res.getText());
	}

	@Test
	public void testDSL2() throws IOException, CopperParserException {
		String input =
				"val test = ~\n" +
						"	hello\n" +
						"7\n";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		String parsed = ((DSLLit)((ValDeclaration) ((Sequence) res).getDeclIterator().iterator().next()).getDefinition()).getText();
		Assert.assertEquals("hello", parsed);
	}
	@Test
	public void testDSL3() throws IOException, CopperParserException {
		String input =
				"val test = ~\n" +
						"	hello\n" +
						"	world\n" +
						"		today\n" +
						"7\n";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		String parsed = ((DSLLit)((ValDeclaration) ((Sequence) res).getDeclIterator().iterator().next()).getDefinition()).getText();
		Assert.assertEquals("hello\nworld\n\ttoday", parsed);

	}

	@Test(expected = CopperParserException.class)
	public void testDSL4() throws IOException, CopperParserException {
		String input =
				"val test = 7\n" +
						"	hello\n" +
						"	world\n" +
						"7\n";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
	}
	@Test
	public void testNew1() throws IOException, CopperParserException {
		String input =
				"val test = new\n" +
						"	val d = 4\n" +
						"	def x():Int = 7\n" +
						"7\n";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");

	}
	@Test
	public void testNew2() throws IOException, CopperParserException {
		String input =
				"val test = (new.x())+9/3-3\n" +
						"	val d = 4\n" +
						"	def x():Int = 7\n" +
						"test\n";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		res.typecheck(Globals.getStandardEnv());
		Assert.assertEquals("IntegerConstant(7)",res.evaluate(Globals.getStandardEnv()).toString());
	}
	
	// admittedly this is only a starting point....
	@Test
	public void testTrivialDSL() throws IOException, CopperParserException {
		String input =
				"val myNumMetadata = /* write me somehow or import from Java */\n" +
				"type MyNum\n" +
				"  def getValue():Int\n" +
				"  metadata = myNumMetadata\n" +
				"val n:MyNum = { 5 }" +
				"n.getValue()";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv()), Int.getInstance());
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "IntegerConstant(5)");
	}
}

