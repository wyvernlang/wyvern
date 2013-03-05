package wyvern.tools.tests;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.io.StringReader;
import java.net.URL;
import java.util.Scanner;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import wyvern.stdlib.Globals;
import wyvern.targets.JavaScript.visitors.JSCodegenVisitor;
import wyvern.tools.lexer.Lexer;
import wyvern.tools.lexer.Token;
import wyvern.tools.parsing.CoreParser;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.simpleParser.Phase1Parser;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.Value;
import wyvern.tools.typedAST.extensions.declarations.ValDeclaration;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.Bool;
import wyvern.tools.types.extensions.Str;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.typedAST.CoreAST;

import static wyvern.tools.types.TypeUtils.*;

public class ParsingTestPhase2 {
	@Rule
    public ExpectedException thrown= ExpectedException.none();

	@Test
	public void testValDecl() {
		Reader reader = new StringReader("val x = 5\n"
										+"x");
		RawAST parsedResult = Phase1Parser.parse(reader);		
		Assert.assertEquals("{$I {$L val x = 5 $L} {$L x $L} $I}", parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Assert.assertEquals("LetExpr(ValDeclaration(\"x\", IntegerConstant(5)), Variable(\"x\"))", typedAST.toString());		
		Type resultType = typedAST.typecheck(env);
		Assert.assertEquals(Int.getInstance(), resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("IntegerConstant(5)", resultValue.toString());
	}

	@Test
	public void testLambdaCall() {
		Reader reader = new StringReader("(fn x : Int => x)(1)");
		RawAST parsedResult = Phase1Parser.parse(reader);		
		Assert.assertEquals("{$I {$L (fn x : Int => x) (1) $L} $I}", parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Assert.assertEquals("Application(Fn([NameBindingImpl(\"x\", Int())], Variable(\"x\")), IntegerConstant(1))", typedAST.toString());
		Type resultType = typedAST.typecheck(env);
		Assert.assertEquals(Int.getInstance(), resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("IntegerConstant(1)", resultValue.toString());
	}
	
	@Test
	public void testLambdaCallWithAdd() {
		Reader reader = new StringReader("(fn x : Int => x + 1)(3)");
		RawAST parsedResult = Phase1Parser.parse(reader);		
		Assert.assertEquals("{$I {$L (fn x : Int => x + 1) (3) $L} $I}", parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Assert.assertEquals("Application(Fn([NameBindingImpl(\"x\", Int())], Invocation(Variable(\"x\"), \"+\", IntegerConstant(1))), IntegerConstant(3))", typedAST.toString());		
		Type resultType = typedAST.typecheck(env);
		Assert.assertEquals(Int.getInstance(), resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("IntegerConstant(4)", resultValue.toString());
	}
	
	@Test
	public void testArithmetic() {
		Reader reader = new StringReader("3*4+5*6");
		RawAST parsedResult = Phase1Parser.parse(reader);		
		Assert.assertEquals("{$I {$L 3 * 4 + 5 * 6 $L} $I}", parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Assert.assertEquals("Invocation(Invocation(IntegerConstant(3), \"*\", IntegerConstant(4)), \"+\", Invocation(IntegerConstant(5), \"*\", IntegerConstant(6)))", typedAST.toString());		
		Type resultType = typedAST.typecheck(env);
		Assert.assertEquals(Int.getInstance(), resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("IntegerConstant(42)", resultValue.toString());
	}
	
	@Test
	public void testPrint() {
		Reader reader = new StringReader("print(\"Testing printing.\")");
		RawAST parsedResult = Phase1Parser.parse(reader);		
		Assert.assertEquals("{$I {$L print (\"Testing printing.\") $L} $I}", parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Assert.assertEquals("Application(ExternalFunction(), StringConstant(\"Testing printing.\"))", typedAST.toString());		
		Type resultType = typedAST.typecheck(env);
		Assert.assertEquals(Unit.getInstance(), resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("()", resultValue.toString());
	}
	
	@Test
	public void testHigherOrderTypes() {
		Reader reader = new StringReader("fn f : Int -> Int => fn x : Int => f(f(x))");
		RawAST parsedResult = Phase1Parser.parse(reader);		
		Assert.assertEquals("{$I {$L fn f : Int -> Int => fn x : Int => f (f (x)) $L} $I}", parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Assert.assertEquals("Fn([NameBindingImpl(\"f\", Arrow(Int(), Int()))], Fn([NameBindingImpl(\"x\", Int())], Application(Variable(\"f\"), Application(Variable(\"f\"), Variable(\"x\")))))", typedAST.toString());		
		Type resultType = typedAST.typecheck(env);
		Assert.assertEquals(arrow(arrow(integer, integer),arrow(integer,integer)), resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("Closure(Fn([NameBindingImpl(\"f\", Arrow(Int(), Int()))], Fn([NameBindingImpl(\"x\", Int())], Application(Variable(\"f\"), Application(Variable(\"f\"), Variable(\"x\"))))), Environment())", resultValue.toString());
	}
	
	@Test
	public void testHigherOrderApplication() {
		Reader reader = new StringReader("val applyTwice = fn f : Int -> Int => fn x : Int => f(f(x))\n"
										+"val addOne = fn x : Int => x + 1\n"
										+"applyTwice(addOne)(1)");
		RawAST parsedResult = Phase1Parser.parse(reader);		
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Assert.assertEquals("LetExpr(ValDeclaration(\"applyTwice\", Fn([NameBindingImpl(\"f\", Arrow(Int(), Int()))], Fn([NameBindingImpl(\"x\", Int())], Application(Variable(\"f\"), Application(Variable(\"f\"), Variable(\"x\")))))), Application(Application(Variable(\"applyTwice\"), Variable(\"addOne\")), IntegerConstant(1)))", typedAST.toString());		
		Type resultType = typedAST.typecheck(env);
		Assert.assertEquals(integer, resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("IntegerConstant(3)", resultValue.toString());
	}
	
	@Test
	public void testMethod() {
		Reader reader = new StringReader("meth double(n:Int):Int = n*2\n"
										+"double(5)\n");
		RawAST parsedResult = Phase1Parser.parse(reader);
		Assert.assertEquals("{$I {$L meth double (n : Int) : Int = n * 2 $L} {$L double (5) $L} $I}", parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Assert.assertEquals("LetExpr(Meth(), Application(Variable(\"double\"), IntegerConstant(5)))", typedAST.toString());
		Type resultType = typedAST.typecheck(env);
		Assert.assertEquals(Int.getInstance(), resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("IntegerConstant(10)", resultValue.toString());
	}
	
	@Test
	public void testTupleMethodCalls() {
		Reader reader = new StringReader("meth mult(n:Int,m:Int):Int = n+5*m\n"
				+"mult(3,2)\n");
		RawAST parsedResult = Phase1Parser.parse(reader);
		Assert.assertEquals("{$I {$L meth mult (n : Int , m : Int) : Int = n + 5 * m $L} {$L mult (3 , 2) $L} $I}", parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Assert.assertEquals("LetExpr(Meth(), Application(Variable(\"mult\"), TupleObject(IntegerConstant(3), IntegerConstant(2))))", typedAST.toString());
		Type resultType = typedAST.typecheck(env);
		Assert.assertEquals(Int.getInstance(), resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("IntegerConstant(13)", resultValue.toString());
	}
	
	@Test
	public void testMutuallyRecursiveMethods() {
		Reader reader = new StringReader("meth doublePlusOne(n:Int):Int = double(n) + 1\n"
										+"meth double(n:Int):Int = n*2\n"
										+"doublePlusOne(5)\n");
		RawAST parsedResult = Phase1Parser.parse(reader);
		Assert.assertEquals("{$I {$L meth doublePlusOne (n : Int) : Int = double (n) + 1 $L} {$L meth double (n : Int) : Int = n * 2 $L} {$L doublePlusOne (5) $L} $I}", parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Assert.assertEquals("LetExpr(Meth(), Application(Variable(\"doublePlusOne\"), IntegerConstant(5)))", typedAST.toString());
		Type resultType = typedAST.typecheck(env);
		Assert.assertEquals(Int.getInstance(), resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("IntegerConstant(11)", resultValue.toString());
	}
	
	@Test
	public void testClassAndField() {
		Reader reader = new StringReader("class Hello\n"
										+"    val hiString = \"hello\"\n"
										+"\n"
										+"val h = new Hello()\n"//hiString: \"hi\")\n"
										+"h.hiString");
		RawAST parsedResult = Phase1Parser.parse(reader);
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		typedAST.typecheck(env);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("StringConstant(\"hello\")", resultValue.toString());
	}
	
	// TODO: test scoping for lambdas and methods
	@Test
	public void testMethodClosures() {
		Reader reader = new StringReader("meth outer(n:Int):Int -> Int\n"
										+"    meth nested(m:Int):Int = n+m\n"
										+"    fn x : Int => nested(x+1)\n"
										+"val f1 = outer(1)\n"
										+"val f2 = outer(2)\n"
										+"f2(6)"); // result should be 9
		RawAST parsedResult = Phase1Parser.parse(reader);
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Type resultType = typedAST.typecheck(env);
		Assert.assertEquals(Int.getInstance(), resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("IntegerConstant(9)", resultValue.toString());
	}

	/* The plan:
	 * X get methods on objects working
	 * X make val a declaration
	 * X get fields on objects working
	 * (D) get field initialization in constructor working, fields read actual values, methods take this
	 * (E) get interfaces for objects working (with prop = val)
	 * (F) introduce var decls - for methods, and for objects
	 * (G) support methods for implementing prop
	 */
	@Test
	public void testClassAndMethods() {
		Reader reader = new StringReader("class Hello\n"
										+"    meth get5():Int = 5\n"
										+"\n"
										+"val h = new Hello()\n"
										+"h.get5()");
		RawAST parsedResult = Phase1Parser.parse(reader);
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Type resultType = typedAST.typecheck(env);
		Assert.assertEquals(Int.getInstance(), resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("IntegerConstant(5)", resultValue.toString());
	}
	
	@Test
	public void testClassAndMethods2() {
		Reader reader = new StringReader("class Hello\n"
										+"	meth get4():Int = 4\n"
										+"	meth get5():Int = 5\n"
										+"	meth getP():Int = this.get4()+this.get5()\n"
										+"\n"
										+"val h = new Hello()\n"
										+"h.getP()");
		RawAST parsedResult = Phase1Parser.parse(reader);
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Type resultType = typedAST.typecheck(env);
		Assert.assertEquals(Int.getInstance(), resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("IntegerConstant(9)", resultValue.toString());
	}
	
	@Test
	public void testClassMethodsVals() {
		Reader reader = new StringReader("class Hello\n"
										+"	val testVal = 5\n"
										+"	meth getVal():Int = this.testVal\n"
										+"	meth getP():Int = this.getVal()\n"
										+"\n"
										+"val h = new Hello()\n"
										+"h.getP()");
		RawAST parsedResult = Phase1Parser.parse(reader);
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Type resultType = typedAST.typecheck(env);
		Assert.assertEquals(Int.getInstance(), resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("IntegerConstant(5)", resultValue.toString());
	}
	
	@Test
	public void testClassMethodsVals3() {
		Reader reader = new StringReader("class Hello\n"
										+"	val testVal = 5\n"
										+"	val testVal2 = 15\n"
										+"	val testVal3 = 25\n"
										+"	meth getVal():Int = this.testVal + this.testVal3/this.testVal2\n"
										+"	meth getP():Int = this.getVal()\n"
										+"\n"
										+"val h = new Hello()\n"
										+"h.getP()");
		RawAST parsedResult = Phase1Parser.parse(reader);
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Type resultType = typedAST.typecheck(env);
		Assert.assertEquals(Int.getInstance(), resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("IntegerConstant(6)", resultValue.toString());
	}
	
	@Test
	public void testVarDecls() {
		Reader reader = new StringReader("var x = 1\nx");
		RawAST parsedResult = Phase1Parser.parse(reader);
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Type resultType = typedAST.typecheck(env);
		Assert.assertEquals(Int.getInstance(), resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("IntegerConstant(1)", resultValue.toString());
	}

	@Test
	public void testVarAssignment() {
		Reader reader = new StringReader("var x = 1\nx=2\nx");
		RawAST parsedResult = Phase1Parser.parse(reader);
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Type resultType = typedAST.typecheck(env);
		Assert.assertEquals(Int.getInstance(), resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("IntegerConstant(2)", resultValue.toString());
	}
	@Test
	public void testVarAssignment2() {
		Reader reader = new StringReader("var x = 1\nx=2\nvar y = 3\ny=4\nx=y\nx");
		RawAST parsedResult = Phase1Parser.parse(reader);
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Type resultType = typedAST.typecheck(env);
		Assert.assertEquals(Int.getInstance(), resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("IntegerConstant(4)", resultValue.toString());
	}
	

	@Test
	public void testVarAssignmentInClass() {
		Reader reader = new StringReader("class Hello\n"
				+"	var testVal = 5\n"
				+"	meth setV(n : Int):Int = this.testVal = n\n"
				+"	meth getV():Int = this.testVal\n"
				+"val h = new Hello()\n"
				+"val a = h.setV(10)\n" 
				+"h.getV()");
		RawAST parsedResult = Phase1Parser.parse(reader);
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Type resultType = typedAST.typecheck(env);
		Assert.assertEquals(Int.getInstance(), resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("IntegerConstant(10)", resultValue.toString());
	}
	

	//Ben: Testing class methods. Broken currently, current priority.
	/*
	@Test
	public void testClassMethods() {
		Reader reader = new StringReader("class Hello\n"
				+"\tval testVal:Int\n"
				+"\tclass meth NewHello(v:Int) = \n" +
				"\t\tval output = new Hello()\n" +
				"\t\t\ttestVal = v\n" +
				"\t\toutput\n"
				+"\tmeth getTest():Int = testVal\n"
				+"\n"
				+"val h = Hello.NewHello(10)\n"
				+"h.getP()");
			RawAST parsedResult = Phase1Parser.parse(reader);
			
			Environment env = Globals.getStandardEnv();
			TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
			Type resultType = typedAST.typecheck(env);
			Assert.assertEquals(Int.getInstance(), resultType);
			Value resultValue = typedAST.evaluate(env);
			Assert.assertEquals("IntegerConstant(10)", resultValue.toString());
	}
	*/
	
	@Test
	public void testSequences() throws IOException {
		String testFileName;
		URL url;
		
		testFileName = "wyvern/tools/tests/samples/testSequences.wyv";
		url = ClassTypeCheckerTests.class.getClassLoader().getResource(testFileName);
		if (url == null) {
			Assert.fail("Unable to open " + testFileName + " file.");
			return;
		}
		InputStream is = url.openStream();
		Reader reader = new InputStreamReader(is);

		// testFileName = "wyvern/tools/tests/samples/parsedSimpleClass.prsd";
		// url = ClassTypeCheckerTests.class.getClassLoader().getResource(testFileName);
		Scanner s = new Scanner(new File(url.getFile()));
		// String parsedTestFileAsString = s.nextLine();
		s.close();
		
		RawAST parsedResult = Phase1Parser.parse(reader);
		// Assert.assertEquals(parsedTestFileAsString, parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Assert.assertEquals("Meth()", typedAST.toString());		

		Type resultType = typedAST.typecheck(env);
		Assert.assertEquals("Unit -> null", resultType.toString());
		
		// Value resultValue = typedAST.evaluate(env);
		// Assert.assertEquals("()", resultValue.toString());
	}
}
