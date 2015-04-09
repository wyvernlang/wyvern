package wyvern.tools.tests;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import wyvern.stdlib.Globals;
import wyvern.tools.errors.ToolError;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.HasParser;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.binding.evaluation.ValueBinding;
import wyvern.tools.typedAST.core.declarations.*;
import wyvern.tools.typedAST.core.expressions.*;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.parsing.DSLLit;
import wyvern.tools.typedAST.extensions.SpliceExn;
import wyvern.tools.typedAST.extensions.TSLBlock;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.typedAST.extensions.interop.java.objects.JavaObj;
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
import wyvern.tools.util.EvaluationEnvironment;

import java.io.*;
import java.util.*;

public class CopperTests {
	@Test(expected= ToolError.class)
	public void testVal2() throws IOException, CopperParserException {
		String input = "val yx:Int = false\nyx";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		res.typecheck(Globals.getStandardEnv(), Optional.empty());
	}
	@Test(expected= RuntimeException.class)
	public void testVal3() throws IOException, CopperParserException {
		String input = "val yx:Int = 3\nyx = 9\nyx";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		res.typecheck(Globals.getStandardEnv(), Optional.empty());
	}
	@Test
	public void testDeclParams2() throws IOException, CopperParserException {
		String input =
				"def foo(x:Int,y:Int):Int = 5+x*y\n" +
						"foo(7,2)";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv(), Optional.empty()), Int.getInstance());
		Assert.assertEquals(res.evaluate(Globals.getStandardEvalEnv()).toString(), "IntegerConstant(19)");
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
		Assert.assertEquals(res.evaluate(Globals.getStandardEvalEnv()).toString(), "IntegerConstant(29)");
	}
	@Test
	public void testClass() throws IOException, CopperParserException {
		String input =
				"class Hello\n" +
				"6";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv(), Optional.empty()), Int.getInstance());
		Assert.assertEquals(res.evaluate(Globals.getStandardEvalEnv()).toString(), "IntegerConstant(6)");
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
		Assert.assertEquals(res.evaluate(Globals.getStandardEvalEnv()).toString(), "IntegerConstant(6)");
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
		Assert.assertEquals(res.evaluate(Globals.getStandardEvalEnv()).toString(), "IntegerConstant(7)");
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
		Assert.assertEquals("IntegerConstant(19)", res.evaluate(Globals.getStandardEvalEnv()).toString());
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
		Assert.assertEquals(res.evaluate(Globals.getStandardEvalEnv()).toString(), "IntegerConstant(5)");
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
		Assert.assertEquals(res.evaluate(Globals.getStandardEvalEnv()).toString(), "IntegerConstant(5)");
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
		Assert.assertEquals(res.evaluate(Globals.getStandardEvalEnv()).toString(), "IntegerConstant(9)");
	}
	
	@Test
	public void parseSimpleType() throws IOException, CopperParserException {
		String input =
				"type T\n" +
				"  def bar():Int\n" +
				"5";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv(), Optional.empty()), Int.getInstance());
		Assert.assertEquals(res.evaluate(Globals.getStandardEvalEnv()).toString(), "IntegerConstant(5)");
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
		Assert.assertEquals(res.evaluate(Globals.getStandardEvalEnv()).toString(), "IntegerConstant(9)");
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
		Assert.assertEquals("IntegerConstant(7)",res.evaluate(Globals.getStandardEvalEnv()).toString());
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
		Assert.assertEquals("IntegerConstant(19)",res.evaluate(Globals.getStandardEvalEnv()).toString());
	}
	@Test
	public void testNew4() throws IOException, CopperParserException {
		String input =
				"val x = 3\n" +
				"val test = new\n" +
				"	val x = x\n" +
				"test.x\n";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		res.typecheck(Globals.getStandardEnv(), Optional.empty());
		Assert.assertEquals("IntegerConstant(3)",res.evaluate(Globals.getStandardEvalEnv()).toString());
	}
	
	// admittedly this is only a starting point....
	@Test
	public void testTrivialDSL() throws IOException, CopperParserException {
		String input =
				"import java:wyvern.tools.parsing.HasParser\n" +
				"type MyNum\n" +
						"  def getValue():Int\n" +
						"  metadata:HasParser = myNumMetadata\n" +
						"val n:MyNum = { 5 }\n" +
						"n.getValue()";

		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");

		Type parserType = Util.javaToWyvType(ExtParser.class);
		Type metaType = Util.javaToWyvType(HasParser.class);


		final ExtParser parseri = str -> {
			New newv = new New(new HashMap<>(), null);
			TypedAST dbody = new IntegerConstant(Integer.parseInt(str.getSrcString().trim()));
			newv.setBody(new DeclSequence(Arrays.asList(new DefDeclaration("getValue", new Arrow(Unit.getInstance(), Int.getInstance()), new ArrayList<>(), dbody, false))));
			return newv;
		};

		HasParser inner = new HasParser() {
			@Override
			public ExtParser getParser() {
				return parseri;
			}
		};

		TypeDeclaration.attrEvalEnv = EvaluationEnvironment.EMPTY.extend(new ValueBinding("myNumMetadata", Util.toWyvObj(inner)));
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv().extend(new NameBindingImpl("myNumMetadata", metaType)), Optional.empty()), Int.getInstance());
		res = new DSLTransformer().transform(res);
		Assert.assertEquals(res.evaluate(Globals.getStandardEvalEnv().extend(new ValueBinding("myNumMetadata", Util.toWyvObj(inner)))).toString(), "IntegerConstant(5)");
	}

	@Test
	public void testTrivialDSL2() throws IOException, CopperParserException {
		String input =
				"import java:wyvern.tools.tests.TrivDSLParser\n" +
						"import java:wyvern.tools.parsing.ExtParser\n" +
						"import java:wyvern.tools.parsing.HasParser\n" +
						"type MyNum\n" +
						"  def getValue():Int\n" +
						"  metadata:HasParser = new\n" +
						"    def getParser():ExtParser = TrivDSLParser.create()\n" +
						"val n:MyNum = { 5 }\n" +
						"n.getValue()";

		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");

		Assert.assertEquals(res.typecheck(Globals.getStandardEnv(), Optional.empty()), Int.getInstance());
		res = new DSLTransformer().transform(res);
		Assert.assertEquals(res.evaluate(Globals.getStandardEvalEnv()).toString(), "IntegerConstant(5)");
	}

	@Test
	public void testTrivialDSL3() throws IOException, CopperParserException {
		String input =
				"module A\n" +
				"import java:wyvern.tools.tests.TrivDSLParser\n" +
				"import java:wyvern.tools.parsing.ExtParser\n" +
				"import java:wyvern.tools.parsing.HasParser\n" +
				"type MyNum\n" +
				"  def getValue():Int\n" +
				"  metadata:HasParser = new\n" +
				"    def getParser():ExtParser = TrivDSLParser.create()\n";
		String in2 = "import wyv:in1\n" +
					 "val n:A.MyNum = { 5 }\n" +
					 "n.getValue()";
		WyvernResolver.clearFiles();
		WyvernResolver.addFile("in1", input);
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(in2), "test input");

		Assert.assertEquals(res.typecheck(Globals.getStandardEnv(), Optional.empty()), Int.getInstance());
		res = new DSLTransformer().transform(res);
		Assert.assertEquals(res.evaluate(Globals.getStandardEvalEnv()).toString(), "IntegerConstant(5)");
	}
	@Test
	public void testTrivialDSL4() throws IOException, CopperParserException {
		String input =
				"type MyNum\n" +
						"  def getValue():Int\n" +
						"  metadata:HasParser = myNumMetadata\n" +
						"val n:MyNum = ~\n" +
						"	5\n" +
						"n.getValue()";

		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");


		Type parserType = Util.javaToWyvType(ExtParser.class);
		Type metaType = Util.javaToWyvType(HasParser.class);


		final ExtParser parseri = str -> {
			New newv = new New(new HashMap<>(), null);
			TypedAST dbody = new IntegerConstant(Integer.parseInt(str.getSrcString().trim()));
			newv.setBody(new DeclSequence(Arrays.asList(new DefDeclaration("getValue", new Arrow(Unit.getInstance(), Int.getInstance()), new ArrayList<>(), dbody, false))));
			return newv;
		};

		HasParser inner = new HasParser() {
			@Override
			public ExtParser getParser() {
				return parseri;
			}
		};

		TypeDeclaration.attrEvalEnv =EvaluationEnvironment.EMPTY.extend(new ValueBinding("myNumMetadata", Util.toWyvObj(inner)));
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv().extend(new NameBindingImpl("myNumMetadata", metaType)).extend(new TypeBinding("HasParser", metaType)), Optional.empty()), Int.getInstance());
		res = new DSLTransformer().transform(res);
		Assert.assertEquals(res.evaluate(Globals.getStandardEvalEnv().extend(new ValueBinding("myNumMetadata", Util.toWyvObj(inner)))).toString(), "IntegerConstant(5)");
	}
	@Test
	public void testImport1() throws IOException, CopperParserException {
		String input =
				"import java:java.lang.Long\n" +
				"Long.create(\"45\")";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		res.typecheck(Globals.getStandardEnv(), Optional.<Type>empty());
		Value result = res.evaluate(Globals.getStandardEvalEnv());
		Assert.assertEquals(((Long) ((JavaObj) result).getObj()).longValue(), 45);
	}

	@Test
	public void testMultiExn() throws IOException, CopperParserException {
		String input =
				"5\n6";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
	}

	@Test
	public void testModule() throws IOException, CopperParserException {
		String input =
				"module A\n" +
				"import java:java.lang.Long\n" +
				"class C\n" +
				"	def d():Long = Long.create(\"192\")\n" +
				"val k = 4\n";

		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		res.typecheck(Globals.getStandardEnv(), Optional.<Type>empty());
		EvaluationEnvironment out = ((Declaration)res).evalDecl(Globals.getStandardEvalEnv());
	}

	@Test
	public void testImport2() throws IOException, CopperParserException {
		String input1 =
				"module A\n" +
						"import java:java.lang.Long\n" +
						"class C\n" +
						"	class def create():C = new\n" +
						"	def d():Long = Long.create(\"192\")\n" +
						"val k = 4\n";

		String input2 =
				"import wyv:in1\n" +
						"val c = A.C.create()\n" +
						"c.d()\n";

		WyvernResolver.clearFiles();
		WyvernResolver.addFile("in1", input1);
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input2), "test input");
		Type result = res.typecheck(Globals.getStandardEnv(), Optional.<Type>empty());
		Value out = res.evaluate(Globals.getStandardEvalEnv());
		Long finalRes = (Long)((JavaObj)out).getObj();
		Assert.assertEquals(192, (long)finalRes);
	}

	@Test
	public void testImport3() throws IOException, CopperParserException {
		String input1 =
				"module A\n" +
				"val k = 19\n" +
				"type Tt\n" +
				"	def t():Int\n";

		String input2 =
				"module M\n" +
						"import wyv:in1\n" +
						"type Tp\n" +
						"	metadata:A.Tt = new\n" +
						"		def t():Int = A.k\n";

		String input3 =
				"import wyv:in2\n" +
						"M.Tp.t()";

		WyvernResolver.clearFiles();
		WyvernResolver.addFile("in1", input1);
		WyvernResolver.addFile("in2", input2);
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input3), "test input");
		Type result = res.typecheck(Globals.getStandardEnv(), Optional.<Type>empty());
		Value out = res.evaluate(Globals.getStandardEvalEnv());
		int finalRes = ((IntegerConstant)out).getValue();
		Assert.assertEquals(19, (int)finalRes);
	}

	@Test
	public void testSplice1() throws IOException, CopperParserException {
		TypedAST testAST = new Sequence(
				new ValDeclaration("x", new IntegerConstant(4), null),
				new Application(new TSLBlock(new Fn(Arrays.asList(new NameBindingImpl("x", Int.getInstance())),
						new SpliceExn(new Variable(new NameBindingImpl("x", Int.getInstance()), null)))), new IntegerConstant(9), null) );
		Type result = testAST.typecheck(Globals.getStandardEnv(), Optional.<Type>empty());
		Value out = testAST.evaluate(Globals.getStandardEvalEnv());
		int finalRes = ((IntegerConstant)out).getValue();
		Assert.assertEquals(4, finalRes);
	}

	@Test
	public void testRDP() throws IOException, CopperParserException {
		String tokenizer =
				"module Tokenizer\n" +
						"import java:java.lang.String\n" +
						"import java:java.io.StringReader\n" +
						"import java:java.io.StreamTokenizer\n" +
						"import java:wyvern.tools.util.LangUtil\n" +
						"val s : Str = \"2+3\"\n" +
						"type Token\n" +
						"	def typeOf():Int\n"+
						"	def getStr():String\n"+
						"	def getNum():Int\n"+
						"class StrTok\n" +
						"	class def create(s:String):StrTok = new\n" +
						"		val s:String = s\n" +
						"	val s:String\n" +
						"	def typeOf():Int = 0\n" +
						"	def getStr():String = this.s\n"+
						"	def getNum():Int = 1/0\n"+
						"class NumTok\n" +
						"	class def create(n:Int):NumTok = new\n" +
						"		val n:Int = n\n" +
						"	val n : Int\n" +
						"	def typeOf():Int = 1\n" +
						"	def getStr():String\n" +
						"		val in : Int = 1/0\n" +
						"		\"\"\n"+
						"	def getNum():Int = this.n\n"+
						"class TokenizerWrapper\n" +
						"	class def create(str:StringReader):TokenizerWrapper = new\n" +
						"		val jtok = StreamTokenizer.create(str)\n" +
						"	val jtok : StreamTokenizer\n" +
						"	def next():Bool = this.jtok.nextToken() == StreamTokenizer.TT_EOF\n" +
						"	def nextTok():Token = " +
						"(if this.jtok.ttype == StreamTokenizer.TT_NUMBER then\n" +
						"(NumTok.create(LangUtil.doubleToInt(this.jtok.nval)) : Token) \n" +
						"else \n" +
						"(if this.jtok.ttype == StreamTokenizer.TT_WORD then (StrTok.create(this.jtok.sval) : Token)\n" +
						" else (if this.jtok.ttype > 0 then (StrTok.create(LangUtil.intToStr(this.jtok.ttype)) : Token) else (NumTok.create(0-1):Token))))\n";
		String parser =
				"module CalcParser\n" +
						"import wyv:tokenizer\n" +
						"import java:java.lang.String\n" +
						"import java:java.io.StringReader\n" +
						"class CalculatorParser\n" +
						"	class def create(s:String):CalculatorParser\n" +
						"		val itkzr = Tokenizer.TokenizerWrapper.create(StringReader.create(s))\n" +
						"		itkzr.next()\n" +
						"		new\n" +
						"			val tkzr:Tokenizer.TokenizerWrapper = itkzr\n" +
						"	val tkzr : Tokenizer.TokenizerWrapper\n" +
						"	def checkNextStr(s:Str):Bool\n" +
						"		val nt = this.tkzr.nextTok()\n" +
						"		(nt.typeOf() == 0) && (s == nt.getStr())\n"+
						"	def E():Int\n" +
						"		def recurser(iv:Int):Int\n" +
						"			val nt = this.tkzr.nextTok()\n" +
						"			def ithen():Int\n" +
						"				val opstr = nt.getStr()\n" +
						"				this.tkzr.next()\n" +
						"				val t1 = this.T()\n" +
						"				if \"+\" == opstr then recurser(iv+t1) else recurser(iv-t1)\n" +
						"			if nt.typeOf() == 0 then (if (\"+\" == nt.getStr()) || (\"-\" == nt.getStr()) then ithen() else iv) else iv\n" +
						"		recurser(this.T())\n" +
						"	def T():Int\n" +
						"		def recurser(iv:Int):Int\n" +
						"			val nt = this.tkzr.nextTok()\n" +
						"			def ithen():Int\n" +
						"				val opstr = nt.getStr()\n" +
						"				this.tkzr.next()\n" +
						"				val t1 = this.P()\n" +
						"				if \"*\" == opstr then recurser(iv*t1) else recurser(iv/t1)\n" +
						"			if nt.typeOf() == 0 then (if (\"/\" == nt.getStr()) || (\"*\" == nt.getStr()) then ithen() else iv) else iv\n" +
						"		recurser(this.P())\n" +
						"	def P():Int\n" +
						"		val nt = this.tkzr.nextTok()\n" +
						"		def num():Int\n" +
						"			this.tkzr.next()\n" +
						"			nt.getNum()\n" +
						"		def paren():Int\n" +
						"			this.tkzr.next()\n" +
						"			val res = this.E()\n" +
						"			val nt2 = this.tkzr.nextTok()\n" +
						"			if nt2.typeOf() == 0 then if  \")\" == nt.getStr() then res else 1/0 else 1/0\n" +
						"		def neg():Int\n" +
						"			this.tkzr.next()\n" +
						"			0-this.P()\n" +
						"		if nt.typeOf() == 1 then " +
									"num() " +
								"else " +
									"if \"(\" == nt.getStr() then " +
										"paren() " +
									"else " +
						"				if \"-\" == nt.getStr() then neg() else 1/0\n";

		String typer = ""+
				"module CalculatorType\n" +
				"import wyv:parser\n" +
				"import java:wyvern.tools.parsing.ExtParser\n" +
				"import java:wyvern.tools.parsing.HasParser\n" +
				"import java:wyvern.tools.parsing.ParseBuffer\n" +
				"import java:wyvern.tools.typedAST.interfaces.TypedAST\n" +
				"type Calculator\n" +
				"	def eval():Int\n" +
				"	metadata:HasParser = new\n" +
				"		def getParser():ExtParser = new\n" +
				"			def parse(buf:ParseBuffer):TypedAST\n" +
				"				val oNum = CalcParser.CalculatorParser.create(buf.getSrcString()).E()\n" +
				"				~\n" +
				"					new\n" +
				"						def eval():Int = $oNum\n";

		String user = "" +
				"import wyv:typer\n" +
				"val calc:CalculatorType.Calculator = { 2 + 3*2 + 5 }\n" +
				"calc.eval()\n";

		WyvernResolver.clearFiles();
		WyvernResolver.addFile("tokenizer", tokenizer);
		WyvernResolver.addFile("parser", parser);
		WyvernResolver.addFile("typer", typer);

		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(user), "test input");
		Type result = res.typecheck(Globals.getStandardEnv(), Optional.<Type>empty());
		res = new DSLTransformer().transform(res);
		Assert.assertEquals("Int", result.toString());

		Value out = res.evaluate(Globals.getStandardEvalEnv());
		Assert.assertEquals("IntegerConstant(13)", out.toString());
	}

	@Test
	public void lazyTSL() throws IOException, CopperParserException {
		String parser = "" +
				"module LazyParser\n" +
				"import java:wyvern.tools.parsing.ParseBuffer\n" +
				"import java:wyvern.tools.typedAST.interfaces.TypedAST\n" +
				"import java:wyvern.tools.util.LangUtil\n" +
				"class Parser\n" +
				"	class def create():Parser = new\n" +
				"	def parse(buf:ParseBuffer):TypedAST\n" +
				"		val spliced = LangUtil.splice(buf)\n" +
				"		~\n" +
				"			new\n" +
				"				def get():Int = $spliced\n";
		String supplier = "" +
				"module LazyTSL\n" +
				"import wyv:parser\n" +
				"import java:wyvern.tools.parsing.ExtParser\n" +
				"import java:wyvern.tools.parsing.HasParser\n" +
				"type Lazy\n" +
				"	def get():Int\n" +
				"	metadata:HasParser = new\n" +
				"		def getParser():ExtParser = LazyParser.Parser.create()\n";
		String client = ""+
				"import wyv:supplier\n" +
				"val x = 4\n" +
				"val test:LazyTSL.Lazy = ~\n" +
				"	4 + x\n" +
				"test.get()";
		WyvernResolver.clearFiles();
		WyvernResolver.addFile("parser", parser);
		WyvernResolver.addFile("supplier", supplier);

		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(client), "test input");
		Type result = res.typecheck(Globals.getStandardEnv(), Optional.<Type>empty());
		res = new DSLTransformer().transform(res);
		Value finalV = res.evaluate(Globals.getStandardEvalEnv());
	}
}

