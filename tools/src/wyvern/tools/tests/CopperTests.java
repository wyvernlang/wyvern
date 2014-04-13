package wyvern.tools.tests;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import org.junit.Assert;
import org.junit.Test;
import wyvern.stdlib.Globals;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.core.binding.ValueBinding;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.core.declarations.TypeDeclaration;
import wyvern.tools.typedAST.core.declarations.ValDeclaration;
import wyvern.tools.typedAST.core.expressions.New;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.extensions.DSLLit;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.parsing.transformers.DSLTransformer;
import wyvern.tools.parsing.ExtParser;
import wyvern.tools.parsing.Wyvern;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

public class CopperTests {
	@Test
	public void testVal() throws IOException, CopperParserException {
		String input = "val yx = 2\nyx";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		res.typecheck(Environment.getEmptyEnvironment(), Optional.empty());
		Value v = res.evaluate(Environment.getEmptyEnvironment());
		Assert.assertEquals(v.toString(), "IntegerConstant(2)");
	}
	@Test
	public void testAdd() throws IOException, CopperParserException {
		String input = "val yx = 2\nval ts = 9\nyx+ts";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		res.typecheck(Environment.getEmptyEnvironment(), Optional.empty());
		Value v = res.evaluate(Environment.getEmptyEnvironment());
		Assert.assertEquals(v.toString(), "IntegerConstant(11)");
	}
	@Test
	public void testMult() throws IOException, CopperParserException {
		String input = "val yx = 2\nval ts = 9+7*2\nyx+ts";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		res.typecheck(Environment.getEmptyEnvironment(), Optional.empty());
		Value v = res.evaluate(Environment.getEmptyEnvironment());
		Assert.assertEquals(v.toString(), "IntegerConstant(25)");
	}
	@Test
	public void testParens() throws IOException, CopperParserException {
		String input = "val yx = 2\nval ts = 9+(5+2)*2\nyx+ts";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		res.typecheck(Environment.getEmptyEnvironment(), Optional.empty());
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
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv(), Optional.empty()), Int.getInstance());
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "IntegerConstant(9)");
	}
	@Test
	public void testDeclParams() throws IOException, CopperParserException {
		String input =
				"def foo(x:Int):Int = 5+x\n" +
						"foo(7)";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv(), Optional.empty()), Int.getInstance());
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "IntegerConstant(12)");
	}
	@Test
	public void testDeclParams2() throws IOException, CopperParserException {
		String input =
				"def foo(x:Int,y:Int):Int = 5+x*y\n" +
						"foo(7,2)";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv(), Optional.empty()), Int.getInstance());
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
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv(), Optional.empty()), Int.getInstance());
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "IntegerConstant(29)");
	}
	@Test
	public void testClass() throws IOException, CopperParserException {
		String input =
				"class Hello\n" +
				"6";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv(), Optional.empty()), Int.getInstance());
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
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv(), Optional.empty()), Int.getInstance());
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
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv(), Optional.empty()), Int.getInstance());
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
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv(), Optional.empty()), Int.getInstance());
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
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv(), Optional.empty()), Int.getInstance());
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
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv(), Optional.empty()), Int.getInstance());
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
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv(), Optional.empty()), Int.getInstance());
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "IntegerConstant(9)");
	}
	
	@Test
	public void parseSimpleType() throws IOException, CopperParserException {
		String input =
				"type T\n" +
				"  def bar():Int\n" +
				"5";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv(), Optional.empty()), Int.getInstance());
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
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv(), Optional.empty()), Int.getInstance());
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "IntegerConstant(9)");
	}
	
	@Test
	public void testSimpleMetadata() throws IOException, CopperParserException {
		String input =
				"class C\n" +
				"  class def create():T\n" +
				"    new\n" +
				"  def foo():Int\n" +
				"    19\n" +
				"type T\n" +
				"  def foo():Int\n" +
				"  metadata:C = new\n" +
				"    def foo():Int\n" +
				"      12\n" +
				"T.foo()";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv(), Optional.empty()), Int.getInstance());
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "IntegerConstant(12)");
	}

	@Test
	public void testDSL1() throws IOException, CopperParserException {
		String input =
				"{ 1 { 2 } {3} 4 {5} {5 {6{{3}}}} }";
		DSLLit res = (DSLLit)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(" 1 { 2 } {3} 4 {5} {5 {6{{3}}}} ", res.getText().get());
	}

	@Test
	public void testDSL2() throws IOException, CopperParserException {
		String input =
				"val test = ~\n" +
						"	hello\n" +
						"7\n";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		String parsed = ((DSLLit)((ValDeclaration) ((Sequence) res).getDeclIterator().iterator().next()).getDefinition()).getText().get();
		Assert.assertEquals("hello", parsed);
	}
	@Test
	public void testDSL3() throws IOException, CopperParserException {
		String input =
				"val test:Int = ~\n" +
						"	hello\n" +
						"	world\n" +
						"		today\n" +
						"7\n";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		String parsed = ((DSLLit)((ValDeclaration) ((Sequence) res).getDeclIterator().iterator().next()).getDefinition()).getText().get();
		Assert.assertEquals("hello\nworld\n\ttoday", parsed);

	}

	@Test
	public void testDSL5() throws IOException, CopperParserException {
		String input =
				"val test:Int = ~\n" +
						"	hello\n" +
						"	world\n" +
						"		today\n" +
						"	today\n" +
						"7\n";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		String parsed = ((DSLLit)((ValDeclaration) ((Sequence) res).getDeclIterator().iterator().next()).getDefinition()).getText().get();
		Assert.assertEquals("hello\nworld\n\ttoday\ntoday", parsed);
	}
	@Test
	public void testDSL6()  throws IOException, CopperParserException {
		String input =
				"val test:Int = if (a):\n" +
						"	hello\n" +
						"	world\n" +
						"		today\n" +
						"	today\n" +
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
		res.typecheck(Globals.getStandardEnv(), Optional.empty());
		Assert.assertEquals("IntegerConstant(7)",res.evaluate(Globals.getStandardEnv()).toString());
	}
	@Test
	public void testNew3() throws IOException, CopperParserException {
		String input =
				"val test = (new.d.k)+9/3-3\n" +
				"	val d = new\n" +
				"		val k = 19\n" +
				"	def x():Int = 7\n" +
				"test\n";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		res.typecheck(Globals.getStandardEnv(), Optional.empty());
		Assert.assertEquals("IntegerConstant(19)",res.evaluate(Globals.getStandardEnv()).toString());
	}
	
	// admittedly this is only a starting point....
	@Test
	public void testTrivialDSL() throws IOException, CopperParserException {
		String input =
				"type MyNum\n" +
				"  def getValue():Int\n" +
				"  metadata:ExtParser = myNumMetadata\n" +
				"val n:MyNum = { 5 }\n" +
				"n.getValue()";

		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");

		Type metaType = Util.javaToWyvType(ExtParser.class);
		ExtParser parser = str -> {
			New newv = new New(new HashMap<>(), null);
			TypedAST dbody = new IntegerConstant(Integer.parseInt(str.trim()));
			newv.setBody(new DeclSequence(Arrays.asList(new DefDeclaration("getValue", new Arrow(Unit.getInstance(), Int.getInstance()), new ArrayList<>(), dbody, false))));
			return newv;
		};

		TypeDeclaration.attrEvalEnv = Environment.getEmptyEnvironment().extend(new ValueBinding("myNumMetadata", Util.toWyvObj(parser)));
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv().extend(new NameBindingImpl("myNumMetadata", metaType)).extend(new TypeBinding("ExtParser", metaType)), Optional.empty()), Int.getInstance());
		res = new DSLTransformer().transform(res);
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv().extend(new ValueBinding("myNumMetadata", Util.toWyvObj(parser)))).toString(), "IntegerConstant(5)");
	}
	@Test
	public void testImport1() throws IOException, CopperParserException {
		String input =
				"import java:java.lang.Long\n" +
					"Long.create(\"45\")";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		res.typecheck(Globals.getStandardEnv(), Optional.<Type>empty());
	}
	@Test
	public void testMultiExn() throws IOException, CopperParserException {
		String input =
				"5\n6";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
	}
}

