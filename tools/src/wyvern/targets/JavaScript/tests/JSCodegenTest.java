package wyvern.targets.JavaScript.tests;

import static wyvern.tools.types.TypeUtils.arrow;

import java.io.*;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;

import wyvern.DSL.html.Html;
import wyvern.stdlib.Globals;
import wyvern.targets.JavaScript.parsers.JSCastParser;
import wyvern.targets.JavaScript.parsers.JSLoadParser;
import wyvern.targets.JavaScript.parsers.JSVarParser;
import wyvern.targets.JavaScript.typedAST.JSFunction;
import wyvern.targets.JavaScript.types.JSObjectType;
import wyvern.targets.JavaScript.visitors.JSCodegenVisitor;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.simpleParser.Phase1Parser;
import wyvern.tools.typedAST.core.Keyword;
import wyvern.tools.typedAST.core.binding.KeywordNameBinding;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.core.binding.ValueBinding;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Str;


import org.mozilla.javascript.*;

public class JSCodegenTest {
	
	private TypedAST doCompile(String input) {
		return doCompile(input, Environment.getEmptyEnvironment());
	}

	private TypedAST doCompile(String input, Environment ienv) {
		Reader reader = new StringReader(input);
		return getTypedAST(reader, ienv);
	}

	private TypedAST getTypedAST(Reader reader, Environment ienv) {
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Environment env = Globals.getStandardEnv();
		env = env.extend(new ValueBinding("require", new JSFunction(arrow(Str.getInstance(), JSObjectType.getInstance()),"require")));
		env = env.extend(new KeywordNameBinding("load", new Keyword(new JSLoadParser())));
		env = env.extend(new KeywordNameBinding("cast", new Keyword(new JSCastParser())));
		env = env.extend(new KeywordNameBinding("JSvar", new Keyword(new JSVarParser())));
		env = env.extend(new TypeBinding("JSObject", JSObjectType.getInstance()));
		env = env.extend(ienv);
		TypedAST typedAST = parsedResult.accept(BodyParser.getInstance(), env);
		Type resultType = typedAST.typecheck(env);
		return typedAST;
	}

	private String getJS(String wyvern) {
		TypedAST typedAST = doCompile(wyvern);
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		return visitor.getCode();
	}
	
	private Object execJS(String source) {
		Context cx = Context.enter();
		try {
			Scriptable scope = cx.initStandardObjects();

			Object res = cx.evaluateString(scope, source, "", 1, null);
			return res;
		} finally {
			Context.exit();
		}
	}
	
	private Object wrapInvoke(String source) {
		return execJS("function test() { "+source + "}; test()");
	}
	
	@Test
	public void testValDecl() {
		TypedAST typedAST = doCompile("val x : Int = 5\nx");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		String source = visitor.getCode();
		Assert.assertEquals("var x = 5;\nreturn x;\n", source);
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
		TypedAST typedAST = doCompile("val applyTwice : (Int -> Int) -> (Int -> Int) = fn f : Int -> Int => fn x : Int => f(f(x))\n"
				+"val addOne : Int -> Int = fn x : Int => x + 1\n"
				+"applyTwice(addOne)(1)");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		String source = visitor.getCode();
		Assert.assertEquals("var applyTwice = function(f) { return function(x) { return (f)((f)(x)); }; };\n"+
							"var addOne = function(x) { return x + 1; };\n"+
							"return ((applyTwice)(addOne))(1);\n", source);

		Assert.assertTrue((Double)wrapInvoke(source) - 3.0 < .001);
	}
	

	@Test
	public void testMethod() {
		TypedAST typedAST = doCompile("def double(n:Int):Int = n*2\n"
									 +"double(5)\n");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		String source = visitor.getCode();
		Assert.assertEquals("function double(n) {\n"+
				"\treturn n * 2;\n}\n"+
				"return (double)(5);\n", source);
		Assert.assertTrue((Double)wrapInvoke(source) - 10.0 < .001);
	}
	
	@Test
	public void testMethod2() {
		TypedAST typedAST = doCompile("def num():Int = 5\n"
									 +"num()\n");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertEquals("function num() {\n"+
				"\treturn 5;\n}\n"+
				"return (num)();\n", visitor.getCode());
	}
	
	@Test
	public void testMutuallyRecursiveMethods() {
		TypedAST typedAST = doCompile("def double(n:Int):Int = n*2\n"
										+"def doublePlusOne(n:Int):Int = double(n) + 1\n"
										+"doublePlusOne(5)\n");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertEquals("function double(n) {\n"+
				"\treturn n * 2;\n}\n"+
				"function doublePlusOne(n) {\n"+
				"\treturn (double)(n) + 1;\n}\n"+
				"return (doublePlusOne)(5);\n", visitor.getCode());
		
	}
	
	@Test
	public void testClassAndField() {
		TypedAST typedAST = doCompile("class Hello\n"
										+"	class def NewHello():Hello = new\n"
										+"	val hiString : Str= \"hello\"\n"
										+"\n"
										+"val h : Hello = Hello.NewHello()\n"//hiString: \"hi\")\n"
										+"h.hiString");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertEquals("function Hello() {}\n"+
							"Hello.NewHello = function() {\n"+
							"	return new Hello();\n"+
							"}\n"+
							"Hello.prototype.hiString = \"hello\";\n"+
							"var h = (Hello.NewHello)();\n"+
							"return h.hiString;\n", visitor.getCode());
	}

	@Test
	public void testNestedMethods() {
		TypedAST typedAST = doCompile(
				"def outer(n:Int):Int -> Int\n"
				+"    def nested(m:Int):Int = n+m\n"
				+"    fn x : Int => nested(x + 1)\n"
				+"val f1 : Int->Int = outer(1)\n"
				+"val f2 : Int->Int = outer(2)\n"
				+"f2(6)");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertEquals(
				"function outer(n) {\n" +
						"\tvar nested = (function (m) {\n" +
						"\t\treturn n + m;\n" +
						"\t}).bind(this);\n" +
						"\treturn function(x) { return (nested)(x + 1); };\n" +
						"\t\n" +
						"}\n" +
						"var f1 = (outer)(1);\n" +
						"var f2 = (outer)(2);\n" +
						"return (f2)(6);\n", visitor.getCode());
	}
	
	@Test
	public void testTupleMethodCalls() {
		TypedAST typedAST = doCompile("def mult(n:Int,m:Int):Int = n+5*m\n"
				+"mult(3,2)\n");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertTrue((Double)wrapInvoke(visitor.getCode()) - 15.0 < .001);
	}
	
	@Test
	public void testClassAndMethods() {
		TypedAST typedAST = doCompile(
				"class Hello\n"
				+"	class def make() : Hello = new\n"
				+"	def get5():Int = 5\n"
				+"\n"
				+"val h : Hello = Hello.make()\n"
				+"h.get5()");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertEquals(
				"function Hello() {}\n" +
				"Hello.make = function() {\n" +
				"	return new Hello();\n" +
				"}\n" +
				"Hello.prototype.get5 = function() {\n" +
				"	return 5;\n" +
				"}\n" +
				"var h = (Hello.make)();\n" +
				"return (h.get5)();\n", visitor.getCode());
	}
	
	@Test
	public void testExternalMethodGeneration() {
		TypedAST typedAST = doCompile("val http : JSObject = require(\"http\")\n" +
									  "def doServer(req : JSObject, resp : JSObject):JSObject = resp.write(\"test\")\n" +
									  "http.createServer(doServer)");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		
	}
	
	@Test
	public void testClassAndMethods2() {
		TypedAST typedAST = doCompile("class Hello\n"
										+"	class def make():Hello = new\n"
										+"	def get4():Int = 4\n"
										+"	def get5():Int = 5\n"
										+"	def getP():Int = this.get4()+this.get5()\n"
										+"\n"
										+"val h:Hello = Hello.make()\n"
										+"h.getP()");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertTrue((Double)wrapInvoke(visitor.getCode()) - 9.0 < .001);
	}
	
	@Test
	public void testClassMethodsVals() {
		TypedAST typedAST = doCompile("class Hello\n"
										+"	val testVal:Int = 5\n"
										+"	class def make():Hello = new\n"
										+"	def getVal():Int = this.testVal\n"
										+"	def getP():Int = this.getVal()\n"
										+"\n"
										+"val h:Hello = Hello.make()\n"
										+"h.getP()");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertTrue((Integer)wrapInvoke(visitor.getCode()) == 5);
	}
	
	@Test
	public void testJSFunction() {
		TypedAST typedAST = doCompile("val http : JSObject = require(\"http\")\n" +
									  "http.bad()");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
	}
	
	@Test
	public void testVar() {
		TypedAST typedAST = doCompile(
				  "var x : Int = 1\n" +
				  "x = x + 1\n" +
				  "x");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertTrue((Double)wrapInvoke(visitor.getCode()) - 2 < 0.001);
	}
	

	@Test
	public void testClassMethArgs() {
		TypedAST typedAST = doCompile("class Hello\n"
				+"	val testVa:Int\n"
				+"	var testVr:Int\n"
				+"	class def make(n : Int):Hello = new\n" +
				"		testVa = n\n" +
				"		testVr = n+1\n"
				+"	def getVa():Int = this.testVa\n"
				+"val h:Hello = Hello.make(10)\n"
				+"h.getVa()");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertTrue((Integer)wrapInvoke(visitor.getCode()) == 10);
	}
	
	@Test
	public void testHtmlDSLAndJS() {
		String testStr = 
				 "val htmlv : Str = html\n" +
				 "	body\n" +
				 "		div\n" +
				 "			attrs\n" +
				 "				id=\"main\"\n" +
				 "				class=\"test\"\n" +
				 "			\"Hi, this was served by Node.js running compiled Wyvern code!\"\n" +
				 "			br\n" +
				 "			button\n" +
				 "				\"Press me!\"\n" +
				 "val http : JSObject = require(\"http\")\n" +
				 "def doServer(req : JSObject, resp : JSObject):JSObject = resp.end(htmlv)\n" +
				 "http.createServer(doServer).listen(8081, \"127.0.0.1\")";
		
		TypedAST typedAST = doCompile(testStr, Html.extend(Environment.getEmptyEnvironment()));
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		String result = visitor.getCode();
	}
	
	@Test
	public void testLoad() {
		String test = "type T\n" +
					  "\tdef asString(from:JSObject):Str\n" +
					  "val t : T = load T in \"./test.js\"\n";
		TypedAST typedAST = doCompile(test, Html.extend(Environment.getEmptyEnvironment()));
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		String result = visitor.getCode();
		
	}
	
	@Test
	public void testIf() {
		String test = "if true\n" +
					  "	then\n" +
					  "		1\n" +
					  "	else\n" +
					  "		2\n";
		TypedAST typedAST = doCompile(test, Html.extend(Environment.getEmptyEnvironment()));
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		String result = visitor.getCode();
		
	}
	
	@Test
	public void testWhile() {
		String test = "while true\n" +
						  "	1";
		TypedAST typedAST = doCompile(test, Html.extend(Environment.getEmptyEnvironment()));
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		String result = visitor.getCode();
	}

	@Test
	public void testCast() {
		String test =
				"type T\n" +
				"	def a(i : Int) : Int\n" +
				"val t : T = cast T in require(\"test\")\n" +
				"t.a(15)";
		TypedAST typedAST = doCompile(test);
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		String result = visitor.getCode();
	}

	@Test
	public void testCastAndVar() {
		String test =
				"type T\n" +
						"	def a(i : Int) : Int\n" +
						"val t : T = cast T in JSvar external\n" +
						"t.a(15)";
		TypedAST typedAST = doCompile(test);
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		String result = visitor.getCode();
	}

	@Test
	public void testIntList() throws IOException {
		String testFileName;
		URL url;

		testFileName = "wyvern/targets/JavaScript/tests/files/IntList.wyv";
		url = JSCodegenTest.class.getClassLoader().getResource(testFileName);
		if (url == null) {
			Assert.fail("Unable to open " + testFileName + " file.");
			return;
		}
		InputStream is = url.openStream();
		TypedAST parsed = getTypedAST(new InputStreamReader(is), Environment.getEmptyEnvironment());
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)parsed).accept(visitor);
		String result = visitor.getCode();
		is.close();
		Assert.assertEquals(3.0, wrapInvoke(result));
	}

	@Test
	public void testNQueens() throws IOException {
		String testFileName;
		URL url;

		testFileName = "wyvern/targets/JavaScript/tests/files/nqueens.wyv";
		url = JSCodegenTest.class.getClassLoader().getResource(testFileName);
		if (url == null) {
			Assert.fail("Unable to open " + testFileName + " file.");
			return;
		}
		InputStream is = url.openStream();
		TypedAST parsed = getTypedAST(new InputStreamReader(is), Environment.getEmptyEnvironment());
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)parsed).accept(visitor);
		String result = visitor.getCode();
		is.close();

		Assert.assertEquals(352.0, wrapInvoke(result)); // Used to be 2680.0 for call with 11 size.
	}

	@Test
	public void testGenericNew() {
		String test = (
				"val test = new");

		TypedAST typedAST = doCompile(test);
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		String result = visitor.getCode();
	}

	@Test
	public void testGenericNew2() {
		String test = (
				"val test = new\n" +
						"	x = 2\n");
		TypedAST typedAST = doCompile(test);
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		String result = visitor.getCode();
	}
	@Test
	public void testGenericNew3() {
		String test = (
				"val test = new\n" +
						"	x = 2\n" +
						"test.x");
		TypedAST typedAST = doCompile(test);
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		String result = visitor.getCode();
		Assert.assertEquals(2, wrapInvoke(result));
	}
}
