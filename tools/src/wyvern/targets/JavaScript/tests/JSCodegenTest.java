package wyvern.targets.JavaScript.tests;

import static wyvern.tools.types.TypeUtils.arrow;
import static wyvern.tools.types.TypeUtils.integer;
import static wyvern.tools.types.TypeUtils.unit;

import java.io.Reader;
import java.io.StringReader;

import junit.framework.Assert;

import org.junit.Test;

import wyvern.stdlib.Globals;
import wyvern.targets.JavaScript.typedAST.JSFunction;
import wyvern.targets.JavaScript.types.JSObjectType;
import wyvern.targets.JavaScript.visitors.JSCodegenVisitor;
import wyvern.tools.parsing.CoreParser;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.simpleParser.Phase1Parser;
import wyvern.tools.typedAST.CoreAST;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.Value;
import wyvern.tools.typedAST.binding.TypeBinding;
import wyvern.tools.typedAST.binding.ValueBinding;
import wyvern.tools.typedAST.extensions.Executor;
import wyvern.tools.typedAST.extensions.ExternalFunction;
import wyvern.tools.typedAST.extensions.IntegerConstant;
import wyvern.tools.typedAST.extensions.UnitVal;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.types.extensions.Str;


import org.mozilla.javascript.*;

public class JSCodegenTest {
	
	private TypedAST doCompile(String input) {
		Reader reader = new StringReader(input);
		RawAST parsedResult = Phase1Parser.parse(reader);
		Environment env = Globals.getStandardEnv();
		env = env.extend(new ValueBinding("require", new JSFunction(arrow(Str.getInstance(),JSObjectType.getInstance()),"require")));
		env = env.extend(new TypeBinding("JSObject", JSObjectType.getInstance()));
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Type resultType = typedAST.typecheck(env);
		return typedAST;
	}
	
	private Object execJS(String source) {
		Context cx = Context.enter();
		try {
			Scriptable scope = cx.initStandardObjects();
			
			return cx.evaluateString(scope, source, "", 1, null);
		} finally {
			Context.exit();
		}
	}
	
	private Object wrapInvoke(String source) {
		return execJS("function test() { "+source + "}; test()");
	}
	
	@Test
	public void testValDecl() {
		TypedAST typedAST = doCompile("val x = 5\nx");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		String source = visitor.getCode();
		Assert.assertEquals("var x = 5;\nreturn x;", source);
		Assert.assertEquals(new Integer(5),wrapInvoke(source));
	}
	
	@Test
	public void testArithmetic() {
		TypedAST typedAST = doCompile("3*4+5*6");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		String source = visitor.getCode();
		Assert.assertEquals("3 * 4 + 5 * 6", source);
		Assert.assertEquals(new Integer(3*4+5*6),wrapInvoke("return "+source));
	}	
	@Test
	public void testLambdaCall() {
		TypedAST typedAST = doCompile("(fn x : Int => x)(1)");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		String source = visitor.getCode();
		Assert.assertEquals("(function(x) { return x; })(1)", source);
		Assert.assertTrue((Double)wrapInvoke("return "+source) - 1.0 < .001);
	}
	
	@Test
	public void testLambdaCallAdd() {
		TypedAST typedAST = doCompile("(fn x : Int => x + 1)(1)");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		String source = visitor.getCode();
		Assert.assertEquals("(function(x) { return x + 1; })(1)", source);
		Assert.assertTrue((Double)wrapInvoke("return "+source) - 2.0 < .001);
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
		String source = visitor.getCode();
		Assert.assertEquals("var applyTwice = function(f) { return function(x) { return (f)((f)(x)); }; };\n"+
							"var addOne = function(x) { return x + 1; };\n"+
							"return ((applyTwice)(addOne))(1);", source);

		Assert.assertTrue((Double)wrapInvoke(source) - 3.0 < .001);
	}
	

	@Test
	public void testMethod() {
		TypedAST typedAST = doCompile("meth double(n:Int):Int = n*2\n"
									 +"double(5)\n");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		String source = visitor.getCode();
		Assert.assertEquals("function double(n) {\n"+
				"\treturn n * 2;\n}\n"+
				"return (double)(5);", source);
		Assert.assertTrue((Double)wrapInvoke(source) - 10.0 < .001);
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
	public void testTupleMethodCalls() {
		TypedAST typedAST = doCompile("meth mult(n:Int,m:Int):Int = n+5*m\n"
				+"mult(3,2)\n");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertTrue((Double)wrapInvoke(visitor.getCode()) - 15.0 < .001);
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
	
	@Test
	public void testExternalMethodGeneration() {
		TypedAST typedAST = doCompile("val http = require(\"http\")\n" +
									  "meth doServer(req : JSObject, resp : JSObject):Unit = resp.write(\"test\")\n" +
									  "http.createServer(doServer)");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		
	}
	
	@Test
	public void testClassAndMethods2() {
		TypedAST typedAST = doCompile("class Hello\n"
										+"	meth get4():Int = 4\n"
										+"	meth get5():Int = 5\n"
										+"	meth getP():Int = this.get4()+this.get5()\n"
										+"\n"
										+"val h = new Hello()\n"
										+"h.getP()");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertTrue((Double)wrapInvoke(visitor.getCode()) - 9.0 < .001);
	}
	
	@Test
	public void testClassMethodsVals() {
		TypedAST typedAST = doCompile("class Hello\n"
										+"	val testVal = 5\n"
										+"	meth getVal():Int = this.testVal\n"
										+"	meth getP():Int = this.getVal()\n"
										+"\n"
										+"val h = new Hello()\n"
										+"h.getP()");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertTrue((Integer)wrapInvoke(visitor.getCode()) == 5);
	}
	
	@Test
	public void testJSFunction() {
		TypedAST typedAST = doCompile("val http = require(\"http\")\n" +
									  "http.bad()");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
	}
}
