package wyvern.tools.tests;

import java.io.Reader;
import java.io.StringReader;

import junit.framework.Assert;

import org.junit.Test;

import wyvern.stdlib.Globals;
import wyvern.tools.parsing.CoreParser;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.simpleParser.Phase1Parser;
import wyvern.tools.typedAST.CoreAST;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.Value;
import wyvern.tools.typedAST.visitors.JSCodegenVisitor;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Int;

public class JSCodegenTest {
	
	private TypedAST doCompile(String input) {
		Reader reader = new StringReader(input);
		RawAST parsedResult = Phase1Parser.parse(reader);
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Type resultType = typedAST.typecheck(env);
		return typedAST;
	}
	
	@Test
	public void testValDecl() {
		TypedAST typedAST = doCompile("val x = 5\nx");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertEquals("var x = 5;\nreturn x;", visitor.getCode());
	}
	
	@Test
	public void testArithmetic() {
		TypedAST typedAST = doCompile("3*4+5*6");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertEquals("3 * 4 + 5 * 6", visitor.getCode());
	}
	
	@Test
	public void testLambdaCall() {
		TypedAST typedAST = doCompile("(fn x : Int => x)(1)");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertEquals("(function(x) { return x; })(1)", visitor.getCode());
	}
	
	@Test
	public void testLambdaCallAdd() {
		TypedAST typedAST = doCompile("(fn x : Int => x + 1)(1)");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertEquals("(function(x) { return x + 1; })(1)", visitor.getCode());
	}
	
	
	@Test
	public void testHigherOrder() {
		TypedAST typedAST = doCompile("fn f : Int -> Int => fn x : Int => f(f(x))");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertEquals("function(f) { return function(x) { return (f)((f)(x)); }; }", visitor.getCode());
	}
	
	@Test
	public void testHigherOrderApplication() {
		TypedAST typedAST = doCompile("val applyTwice = fn f : Int -> Int => fn x : Int => f(f(x))\n"
				+"val addOne = fn x : Int => x + 1\n"
				+"applyTwice(addOne)(1)");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertEquals("var applyTwice = function(f) { return function(x) { return (f)((f)(x)); }; };\n"+
							"var addOne = function(x) { return x + 1; };\n"+
							"return ((applyTwice)(addOne))(1);", visitor.getCode());
	}
	

	@Test
	public void testMethod() {
		TypedAST typedAST = doCompile("meth double(n:Int):Int = n*2\n"
									 +"double(5)\n");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertEquals("function double(n) {\n"+
				"\treturn n * 2;\n}\n"+
				"return (double)(5);", visitor.getCode());
	}
	
	@Test
	public void testMethod2() {
		TypedAST typedAST = doCompile("meth num():Int = 5\n"
									 +"num()\n");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertEquals("function num() {\n"+
				"\treturn 5;\n}\n"+
				"return (num)();", visitor.getCode());
	}
	
	@Test
	public void testMutuallyRecursiveMethods() {
		TypedAST typedAST = doCompile("meth doublePlusOne(n:Int):Int = double(n) + 1\n"
										+"meth double(n:Int):Int = n*2\n"
										+"doublePlusOne(5)\n");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertEquals("function doublePlusOne(n) {\n"+
				"\treturn (double)(n) + 1;\n}\n"+
				"function double(n) {\n"+
				"\treturn n * 2;\n}\n"+
				"return (doublePlusOne)(5);", visitor.getCode());
		
	}
	
	@Test
	public void testClassAndField() {
		TypedAST typedAST = doCompile("class Hello\n"
										+"    val hiString = \"hello\"\n"
										+"\n"
										+"val h = new Hello()\n"//hiString: \"hi\")\n"
										+"h.hiString");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertEquals("function Hello() {\n"+
        		"\tthis.hiString = \"hello\";\n"+
				"}\n"+
				"var h = new Hello();\nreturn h.hiString;", visitor.getCode());
	}

	@Test
	public void testNestedMethods() {
		TypedAST typedAST = doCompile(
				"meth outer(n:Int):Int -> Int\n"
				+"    meth nested(m:Int):Int = n+m\n"
				+"    fn x : Int => nested(x + 1)\n"
				+"val f1 = outer(1)\n"
				+"val f2 = outer(2)\n"
				+"f2(6)");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertEquals(
				"function outer(n) {\n"+
				"\tfunction nested(m) {\n"+
        		"\t\treturn n + m;\n"+
        		"\t}\n"+
        		"\treturn function(x) { return (nested)(x + 1); };\n"+
				"}\n"+
				"var f1 = (outer)(1);\n" +
				"var f2 = (outer)(2);\n" +
				"return (f2)(6);", visitor.getCode());
	}
	
	@Test
	public void testClassAndMethods() {
		TypedAST typedAST = doCompile(
				"class Hello\n"
				+"    meth get5():Int = 5\n"
				+"\n"
				+"val h = new Hello()\n"
				+"h.get5()");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertEquals("function Hello() {\n" +
				"\tthis.get5 = function() {\n" +
				"\t\treturn 5;" +
				"\n\t}\n" +
				"}\n" +
				"var h = new Hello();\n" +
				"return (h.get5)();", visitor.getCode());
	}
}
