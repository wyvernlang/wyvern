package wyvern.tools.tests;

import junit.framework.Assert;
import org.junit.Test;
import wyvern.DSL.DSL;
import wyvern.stdlib.Globals;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.simpleParser.Phase1Parser;
import wyvern.tools.typedAST.core.Keyword;
import wyvern.tools.typedAST.core.binding.KeywordNameBinding;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.typedAST.extensions.interop.java.parsers.JImportParser;
import wyvern.tools.typedAST.extensions.interop.java.parsers.JNullParser;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class JavaInteropTests {

	private TypedAST doCompile(String input) {
		Reader reader = new StringReader(input);
		Environment env = Globals.getStandardEnv();
		return getTypedAST(reader, env);
	}

	private TypedAST getTypedAST(Reader reader, Environment env) {
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		env = env.extend(new KeywordNameBinding("JImport", new Keyword(new JImportParser())));
		env = env.extend(new KeywordNameBinding("JNull", new Keyword(new JNullParser())));
		TypedAST typedAST = parsedResult.accept(new BodyParser(), env);
		Type resultType = typedAST.typecheck(env);
		return typedAST;
	}

	public interface Tester {
		public int a();
	}

	@Test
	public void testSimple() {
		String test = "class Test\n" +
				"	class def create() : Test = new\n" +
				"	def a() : Int = 2\n" +
				"Test.create()";
		Value result = doCompile(test).evaluate(Environment.getEmptyEnvironment());
		Tester cast = Util.toJavaClass((Obj) result, Tester.class);
		Assert.assertEquals(cast.a(), 2);
	}

	public interface Tester2 {
		public int a(int x);
	}

	@Test
	public void testArgs() {
		String test = "class Test\n" +
				"	class def create() : Test = new\n" +
				"	def a(x : Int) : Int = 2 + x\n" +
				"Test.create()";
		Value result = doCompile(test).evaluate(Environment.getEmptyEnvironment());
		Tester2 cast = Util.toJavaClass((Obj)result, Tester2.class);
		Assert.assertEquals(cast.a(2), 4);
	}

	public interface Tester3 {
		public int a(Tester2 in);
	}

	@Test
	public void testArgObj() {
		String test =
				"type T\n" +
				"	def a(x : Int) : Int\n" +
				"class Test\n" +
				"	class def create() : Test = new\n" +
				"	def a(x : T) : Int = 2 + x.a(2)\n" +
				"Test.create()";
		Value result = doCompile(test).evaluate(Environment.getEmptyEnvironment());
		Tester3 cast = Util.toJavaClass((Obj)result, Tester3.class);
		Assert.assertEquals(cast.a(new MyTester2()), 4);
	}

	public interface Tester4 {
		public int a(int a, int b);
	}

	@Test
	public void testMultiArgs() {
		String test =
				"class Test\n" +
				"	class def create() : Test = new\n" +
				"	def a(a : Int, b : Int) : Int = a + b\n" +
				"Test.create()";
		Value result = doCompile(test).evaluate(Environment.getEmptyEnvironment());
		Tester4 cast = Util.toJavaClass((Obj)result, Tester4.class);
		Assert.assertEquals(cast.a(2,3), 5);
	}

	public interface Tester5 {
		public int t(Tester4 inp);
	}

	@Test
	public void testJImport() {
		String test =
				"JImport wyvern.tools.tests.JavaInteropTests$Tester4 as Tester4\n" +
				"class Test1\n" +
				"	class def create() : Test1 = new\n" +
				"	def t(in : Tester4) : Int = in.a(2,3)\n"+
				"Test1.create()";

		Value result = doCompile(test).evaluate(Environment.getEmptyEnvironment());
		Tester5 rec = Util.toJavaClass((Obj)result, Tester5.class);


		test =
				"class Test\n" +
				"	class def create() : Test = new\n" +
				"	def a(a : Int, b : Int) : Int = a + b\n" +
				"Test.create()";
		result = doCompile(test).evaluate(Environment.getEmptyEnvironment());
		Tester4 val = Util.toJavaClass((Obj)result, Tester4.class);

		Assert.assertEquals(rec.t(val), 5);
	}

	@Test
	public void testTestCast() {
		String test = "class Test\n" +
				"	class def create() : Test = new\n" +
				"	def a() : Int = 2\n" +
				"Test.create()";
		Value result = doCompile(test).evaluate(Environment.getEmptyEnvironment());
		Assert.assertTrue(Util.checkCast((Obj)result, Tester.class));
		String test2 = "class Test\n" +
				"	class def create() : Test = new\n" +
				"	def b() : Int = 2\n" +
				"Test.create()";
		result = doCompile(test2).evaluate(Environment.getEmptyEnvironment());
		Assert.assertFalse(Util.checkCast((Obj)result, Tester.class));
	}

	@Test
	public void testJavaImportInterop() {
		List<String> testFiles  = new ArrayList<>();
		testFiles.add(
			"import \"java:java.lang.Integer\" as JInt\n" +
			"class Test\n" +
			"	class def create() : Test = new\n" +
			"	def a() : JInt.Integer = JInt.Integer.valueOf(\"10\", 10)\n" +
			"Test.create().a()");
		TypedAST result = wyvern.stdlib.Compiler.compileSources("test", testFiles, new LinkedList<DSL>());
		Value output = result.evaluate(Environment.getEmptyEnvironment());
	}

	private static class MyTester2 implements Tester2 {
		@Override
		public int a(int x) {
			return x;
		}
	}
}
