package wyvern.tools.tests;

import junit.framework.Assert;
import org.junit.Test;
import wyvern.stdlib.Globals;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.simpleParser.Phase1Parser;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;

import java.io.Reader;
import java.io.StringReader;

public class JavaInteropTests {

	private TypedAST doCompile(String input) {
		Reader reader = new StringReader(input);
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(BodyParser.getInstance(), env);
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
		Assert.assertEquals(cast.a(new Tester2() {
			@Override
			public int a(int x) {
				return x;
			}
		}), 4);
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

}
