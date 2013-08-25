package wyvern.targets.Java.tests;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import wyvern.stdlib.Globals;
import wyvern.targets.Java.visitors.JavaGenerator;
import wyvern.targets.JavaScript.tests.JSCodegenTest;
import wyvern.targets.util.VariableResolver;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.simpleParser.Phase1Parser;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.util.Pair;

@SuppressWarnings("unchecked")
public class JavaTests {
	
	private TypedAST doCompile(String input) {
		return doCompile(new StringReader(input), Environment.getEmptyEnvironment());
	}

	private TypedAST doCompile(Reader reader, Environment ienv) {
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Environment env = Globals.getStandardEnv();
		env = env.extend(ienv);
		TypedAST typedAST = parsedResult.accept(BodyParser.getInstance(), env);
		Type resultType = typedAST.typecheck(env);
		return typedAST;
	}
	

	@Test
	public void testClasses2() {
		String test = "class Test\n" +
					  "	val t : Int\n";
		
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		
	}
	
	@Test
	public void testClasses() {
		String test = "class Test\n" +
					  "	val t : Int\n" +
					  "	val x : Test\n";
		
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		
	}
	
	@Test
	public void testMutuallyRecursiveClasses() {
		String test = "class Test\n" +
					  "	val t : Int\n" +
					  "	val x : Test2\n" +
					  "class Test2\n" +
					  "	val x2 : Int\n" +
					  "	val y : Test\n";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
	}
	
	
	@Test
	public void testMethods() throws Exception {
		String test = "class Test\n" +
					  "	class def create() : Test = new\n" +
					  "	def s () : Int =\n" +
					  "		1";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("Test");
		Object testObj = generated.getMethod("create").invoke(null);
		Object returned = generated.getMethod("s").invoke(testObj);
		Assert.assertEquals(returned, new Integer(1));
	}
	
	@Test
	public void testMethods2() throws Exception {
		String test = "class Test\n" +
					  "	class def create() : Test = new\n" +
					  "	def s (a : Int) : Int =\n" +
					  "		1";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("Test");
		Object testObj = generated.getMethod("create").invoke(null);
		Object returned = generated.getMethod("s", int.class).invoke(testObj, 1);
		Assert.assertEquals(returned, new Integer(1));
	}

	@Test
	public void testMethods3() throws Exception {
		String test = "class Test\n" +
					  "	class def create() : Test = new\n" +
					  "	def s (a : Int, b : Int) : Int =\n" +
					  "		1";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("Test");
		Object testObj = generated.getMethod("create").invoke(null);
		Object returned = generated.getMethod("s", int.class, int.class).invoke(testObj, 1, 2);
		Assert.assertEquals(returned, new Integer(1));
	}
	


	@Test
	public void testMethods4() throws Exception {
		String test = "class Test\n" +
					  "	class def create() : Test = new\n" +
					  "	def s (a : Int, b : Int) : Int =\n" +
					  "		a+b";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("Test");
		Object testObj = generated.getMethod("create").invoke(null);
		Object returned = generated.getMethod("s", int.class, int.class).invoke(testObj, 1, 2);
		Assert.assertEquals(returned, new Integer(3));
	}
	
	@Test
	public void testPlus() throws Exception {
		String test = "class Test\n" +
					  "	class def create() : Test = new\n" +
					  "	def s () : Int =\n" +
					  "		1+2";
		

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("Test");
		
		Object testObj = generated.getMethod("create").invoke(null);
		Object returned = generated.getMethod("s").invoke(testObj);
		Assert.assertEquals(returned, new Integer(3));
	}
	@Test
	public void testPlus2() throws Exception {
		String test = "class Test\n" +
					  "	class def create() : Test = new\n" +
					  "	def s () : Int =\n" +
					  "		1+2+3";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("Test");
		Object testObj = generated.getMethod("create").invoke(null);
		Object returned = generated.getMethod("s").invoke(testObj);
		Assert.assertEquals(returned, new Integer(6));
	}

	@Test
	public void testExecution() throws Exception {
		String test = "1+2+3";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(6));
		
	}
	
	@Test
	public void testVals() throws Exception {
		String test = "val t : Int = 2\n" +
					  "t";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(2));
		
	}
	@Test
	public void testVals2() throws Exception {
		String test = "val t : Int = 2\n" +
				      "val s : Int = 2\n" +
					  "t+s";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(4));
	}
	@Test
	public void testVals3() throws Exception {
		String test = "val t : Int = 2\n" +
				      "val s : Int = 2\n" +
					  "t+s+4";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(8));
	}
	@Test
	public void testVals4() throws Exception {
		String test = "class Test\n" +
					  "	class def create() : Test = new\n" +
					  "	def s () : Int =\n" +
					  "		val t : Int = 3\n" +
					  "		t";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object testObj = generated.getMethod("create").invoke(null);
		Object returned = generated.getMethod("s").invoke(testObj);
		Assert.assertEquals(returned, new Integer(3));
	}
	@Test
	public void testVals5() throws Exception {
		String test = "class Test\n" +
					  "	class def create() : Test = new\n" +
					  "	def s (a : Int) : Int =\n" +
					  "		val t : Int = 3\n" +
					  "		t+a";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("Test");
		Object testObj = generated.getMethod("create").invoke(null);
		Object returned = generated.getMethod("s", int.class).invoke(testObj,3);
		Assert.assertEquals(returned, new Integer(6));
	}
	@Test
	public void testMeths() throws Exception {
		String test = "class Test6\n" +
				  "	class def create() : Test6 = new\n" +
				  "	def n() : Int = 1\n" +
				  "val x : Test6 = Test6.create()\n" +
				  "x.n()";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(1));
	}

	@Test
	public void testMeths2() throws Exception {
		String test = "class Test7\n" +
				  "	class def create() : Test7 = new\n" +
				  "	def n(a : Int) : Int = a+1\n" +
				  "val x : Test7 = Test7.create()\n" +
				  "x.n(10)";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(11));
	}
	
	@Test
	public void testMeths3() throws Exception {
		String test = "class Test8\n" +
				  "	class def create() : Test8 = new\n" +
				  "	def n(a : Int) : Int = a+1\n" +
				  "	def b(s : Int) : Int = this.n(s+1) + 2\n" +
				  "val x : Test8 = Test8.create()\n" +
				  "x.b(10)";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(14));
	}

	@Test
	public void testMeths4() throws Exception {
		String test = "class Test\n" +
				"	class def create() : Test = new\n" +
				"	def n(a : Int) : Int = a+1\n" +
				"	def b(s : Int) : Int = s+1\n" +
				"val x : Test = Test.create()\n" +
				"x.n(x.b(4))";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(6));
	}
	
	@Test
	public void testVarVisitor() {
		String test = "val x : Int = 2\n" +
					  "val y : Int = 3\n" +
					  "x+y";
		CoreAST ast = (CoreAST) doCompile(test);
		HashMap<String, Type> externalContext = new HashMap<String, Type>();
		externalContext.put("x", Int.getInstance());
		VariableResolver visitor = new VariableResolver(externalContext);
		((CoreAST)((Sequence)ast).getLast()).accept(visitor);
		List<Pair<String, Type>> usedVariables = visitor.getUsedVars();
		Assert.assertEquals("x", usedVariables.get(0).first);
	}
	
	@Test
	public void testVarVisitor2() {
		String test = "val x : Int = 2\n" +
					  "class Test\n" +
					  "	class def create():Test = new\n" +
					  "	def s() : Int = x";
		CoreAST ast = (CoreAST) doCompile(test);
		HashMap<String, Type> externalContext = new HashMap<String, Type>();
		externalContext.put("x", Int.getInstance());
		VariableResolver visitor = new VariableResolver(externalContext);
		((CoreAST)((DeclSequence)((Sequence)ast).getLast()).getLast()).accept(visitor);
		List<Pair<String, Type>> usedVariables = visitor.getUsedVars();
		Assert.assertEquals("x", usedVariables.get(0).first);
	}
	
	@Test
	public void testVarVisitor3() {
		String test = "val x : Int = 2\n" +
					  "def Test() : Int = x";
		CoreAST ast = (CoreAST) doCompile(test);
		HashMap<String, Type> externalContext = new HashMap<String, Type>();
		externalContext.put("x", Int.getInstance());
		VariableResolver visitor = new VariableResolver(externalContext);
		((CoreAST)((DeclSequence)((Sequence)ast).getLast()).getLast()).accept(visitor);
		List<Pair<String, Type>> usedVariables = visitor.getUsedVars();
		Assert.assertEquals("x", usedVariables.get(0).first);
	}
	
	@Test
	public void testVarVisitor4() {
		String test = "val x : Int = 2\n" +
					  "fn y : Int => x";
		CoreAST ast = (CoreAST) doCompile(test);
		HashMap<String, Type> externalContext = new HashMap<String, Type>();
		externalContext.put("x", Int.getInstance());
		VariableResolver visitor = new VariableResolver(externalContext);
		((CoreAST)((Sequence)ast).getLast()).accept(visitor);
		List<Pair<String, Type>> usedVariables = visitor.getUsedVars();
		Assert.assertEquals("x", usedVariables.get(0).first);
	}
	
	@Test
	public void testClassClosure() throws Exception {
		String test = "val n : Int = 2\n" +
					  "class Test9\n" +
					  "	class def create():Test9 = new\n" +
					  "	def test():Int = n\n" +
					  "Test9.create().test()";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
        Object returned = generated.getMethod("main").invoke(null);
        Assert.assertEquals(returned, new Integer(2));
    }
    @Test
    public void testClassClosure2() throws Exception {
        String test =
                "val n : Int = 2\n" +
                "val t : Int = 3\n" +
                "class Test2\n" +
                "	class def create():Test2 = new\n" +
                "	def test():Int = n+t\n" +
                "Test2.create().test()";

        ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
        Class generated = generatedLoader.loadClass("wycCode");
        Object returned = generated.getMethod("main").invoke(null);
        Assert.assertEquals(returned, new Integer(5));
    }

    @Test
    public void testClassClosure3() throws Exception {
        String test =
                        "type T\n" +
                        "	def test():Int\n" +
                        "class Tester\n" +
                        "	class def create():Tester = new\n" +
						"	def test(e : Int):T =\n" +
                        "		class Inner\n" +
                        "			implements T\n" +
						"			class def create():Inner = new\n" +
                        "			def test():Int = e\n" +
						"		Inner.create()\n" +
                        "Tester.create().test(2).test() + Tester.create().test(3).test()";

        ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
        Class generated = generatedLoader.loadClass("wycCode");
        Object returned = generated.getMethod("main").invoke(null);
        Assert.assertEquals(returned, new Integer(5));
    }

    @Test
    public void testShadowing() throws Exception {
        String test =
                "val n : Int = 2\n" +
                "val n : Int = 3\n" +
                "n";

        ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
        Class generated = generatedLoader.loadClass("wycCode");
        Object returned = generated.getMethod("main").invoke(null);
        Assert.assertEquals(returned, new Integer(3));
    }

    @Test
    public void testClassShadowing() throws Exception {
        String test =
                "class Test4\n" +
                "   class def create():Test4 = new\n" +
                "   def a() : Int = 1\n" +
                "val y : Test4 = Test4.create()\n"+
                "class Test4\n" +
                "   class def create():Test4 = new\n" +
        		"   def a(x : Int):Int = 2+x\n" +
                "val x : Test4 = Test4.create()\n" +
                "x.a(2) + y.a()";
        ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
        Class generated = generatedLoader.loadClass("wycCode");
        Object returned = generated.getMethod("main").invoke(null);
        Assert.assertEquals(returned, 5);
    }

    @Test
    public void testClassShadowing2() throws Exception {
        String test =
                "val x : Int = 1\n" +
                "class Test\n" +
                "   class def create():Test = new\n" +
                "   def a(i : Int) : Int =\n" +
                "       class Inner\n" +
                "           class def create() : Test = new\n" +
                "           def a() : Int = i\n" +
                "Test.create().a()";
    }

	@Test
	public void testTypeMeths1() throws Throwable {
		String test = 
				  "type T\n" +
				  "	def b(s:Int):Int\n" +
				  "class Test3\n" +
				  "	implements T\n" +
				  "	class def create() : Test3 = new\n" +
				  "	def n(a : Int) : Int = a+1\n" +
				  "	def b(s : Int) : Int = this.n(s+1) + 2\n" +
				  "val x : T = Test3.create()\n" +
				  "x.b(10)";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));

		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(14));
	}

	@Test
	public void testFields1() throws Exception {
		String test =
				"class Test\n" +
				"	class def create():Test = new\n" +
				"	val x : Int = 2\n" +
				"val t : Test = Test.create()\n" +
				"t.x";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(2));
	}

	@Test
	public void testVars1() throws Exception {
		String test = "var x : Int = 1\n" +
				"x = 4\n" +
				"x";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(4));
	}
	

	@Test
	public void testInlineMeths() throws Exception {
		String test = 
				"def a (s : Int) :Int = s\n" +
				"a(0)";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(0));
	}
	@Test
	public void testInlineMeths2() throws Exception {
		String test =
				"val y : Int = 2\n" +
				"def a (s : Int) :Int = s + y\n" +
				"a(0)";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(2));
	}
	
	@Test
	public void testInlineFuncs() throws Exception {
		String test = "val applyTwice : (Int -> Int) -> (Int -> Int) = fn f : Int -> Int => fn x : Int => f(f(x))\n"
				+"val addOne : Int -> Int = fn x : Int => x + 1\n"
				+"applyTwice(addOne)(1)";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(3));
	}

	@Test
	public void testNewInitalizer() throws Exception {
		String test =
				"class Test\n" +
				"	val t : Int\n" +
				"	class def create(n : Int) : Test =\n" +
				"		new\n" +
				"			t=n\n" +
				"val ins : Test = Test.create(10)\n" +
				"ins.t";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(10));
	}

	@Test
	public void testClosures4() throws Exception {
		String test =
				"class Test\n" +
						"	val t : Int\n" +
						"	def getFn() : Int -> Int =\n" +
						"		fn x : Int => x + this.t\n" +
						"	class def create(n : Int) : Test =\n" +
						"		new\n" +
						"			t=n\n" +
						"val ins : Test = Test.create(10)\n" +
						"ins.getFn()(5)";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(15));
	}

	@Test
	public void testMeths5() throws Exception {
		String test =
				"def mult(n:Int,m:Int):Int = n+5*m\n"
						+"mult(3,2)\n";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(13));
	}

	@Test
	public void testMeths6() throws Exception {
		String test =
				"def double(n:Int):Int = n*2\n"
						+"def doublePlusOne(n:Int):Int = double(n) + 1\n"
						+"doublePlusOne(5)\n";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(11));
	}

	@Test
	public void testClassAndField() throws Exception {
		String test =
				"class Hello\n"
						+"    class def make():Hello\n"
						+"    \tnew\n"
						+"    val hiString : Str = \"hello\"\n"
						+"\n"
						+"val h : Hello = Hello.make()\n"//hiString: \"hi\")\n"
						+"h.hiString";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, "hello");
	}

	@Test
	public void testMethodClosures() throws Exception {
		String test =
				"def outer(n:Int):Int -> Int\n"
						+"    def nested(m:Int):Int = n+m\n"
						+"    fn x : Int => nested(x+1)\n"
						+"val f1 : Int -> Int = outer(1)\n"
						+"val f2 : Int -> Int = outer(2)\n"
						+"f2(6)";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(9));
	}

	@Test
	public void testClassAndMethods() throws Exception {
		String test =
				"class Hello\n"
						+"    class def make():Hello = new\n"
						+"    def get5():Int = 5\n"
						+"\n"
						+"val h:Hello = Hello.make()\n"
						+"h.get5()";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(5));
	}

	@Test
	public void testClassAndMethods2() throws Exception {
		String test =
				"class Hello\n"
						+"	class def make():Hello = new\n"
						+"	def get4():Int = 4\n"
						+"	def get5():Int = 5\n"
						+"	def getP():Int = this.get4()+this.get5()\n"
						+"\n"
						+"val h:Hello = Hello.make()\n"
						+"h.getP()";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(9));
	}

	@Test
	public void testClassMethodsVals() throws Exception {
		String test =
				"class Hello\n"
						+"	class def make():Hello = new\n"
						+"	val testVal:Int = 5\n"
						+"	def getVal():Int = this.testVal\n"
						+"	def getP():Int = this.getVal()\n"
						+"\n"
						+"val h:Hello = Hello.make()\n"
						+"h.getP()";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(5));
	}

	@Test
	public void testClassMethodsVals2() throws Exception {
		String test =
				"class Hello\n"
						+"	class def make():Hello = new\n"
						+"	val testVal:Int = 5\n"
						+"	val testVal2:Int = 15\n"
						+"	val testVal3:Int = 25\n"
						+"	def getVal():Int = this.testVal + this.testVal3/this.testVal2\n"
						+"	def getP():Int = this.getVal()\n"
						+"\n"
						+"val h : Hello = Hello.make()\n"
						+"h.getP()";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(6));
	}

	@Test
	public void testVarDecls() throws Exception {
		String test =
				"var x : Int = 1\nx";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(1));
	}

	@Test
	public void testVarDecls2() throws Exception {
		String test =
				"var x:Int = 1\nx=2\nx";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(2));
	}

	@Test
	public void testVarDecls3() throws Exception {
		String test =
				"var x:Int = 1\nx=2\nvar y:Int = 3\ny=4\nx=y\nx";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(4));
	}

	@Test
	public void testVarAssignmentInClass() throws Exception {
		String test =
				"class Hello\n"
						+"	class def make():Hello = new\n"
						+"	var testVal:Int = 5\n"
						+"	def setV(n : Int):Unit = this.testVal = n\n"
						+"	def getV():Int = this.testVal\n"
						+"val h:Hello = Hello.make()\n"
						+"h.setV(10)\n"
						+"h.getV()";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(10));
	}

	@Test
	public void testClassMethods() throws Exception {
		String test =
				"class Hello\n" +
						"   val testVal:Int\n" +
						"   class def NewHello(v:Int):Hello = \n" +
						"       val output:Hello = new\n" +
						"           testVal = v\n" +
						"       output\n" +
						"   def getTest():Int = this.testVal\n" +
						"val h:Hello = Hello.NewHello(10)\n" +
						"h.getTest()";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(10));
	}

	@Test
	public void testIf() throws Exception {
		String test =
				"if true\n" +
				"	then\n" +
				"		1\n" +
				"	else\n" +
				"		2\n";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(1));
	}

	@Test
	public void testWhile() throws Exception {
		String test =
				"var x:Int = 5\n" +
				"var y:Int = 0\n" +
				"while x > 0\n" +
				"	x = x-1\n" +
				"	y = y+1\n" +
				"y";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("wycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(5));
	}

	/*
	TODO: Implement self-recursive functions (post ECOOP)

	@Test
	public void testRecursion() throws Exception {
		String test =
				"def dummy():Int = 2\n" +
				"def a(i : Int):Int = \n" +
				"	if (i > 0)\n" +
				"		then\n" +
				"			a(i-1)\n" +
				"		else\n" +
				"			i\n";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("CLASSwycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(5));
	}


	@Test
	public void testNQueens() throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		String testFileName;
		URL url;

		testFileName = "wyvern/targets/JavaScript/tests/files/nqueens.wyv";
		url = JSCodegenTest.class.getClassLoader().getResource(testFileName);
		if (url == null) {
			Assert.fail("Unable to open " + testFileName + " file.");
			return;
		}
		InputStream is = url.openStream();
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(new InputStreamReader(is), Environment.getEmptyEnvironment()));
		is.close();
		Class generated = generatedLoader.loadClass("CLASSwycCode");
		Object returned = generated.getMethod("main").invoke(null);
	}
	 */
}
