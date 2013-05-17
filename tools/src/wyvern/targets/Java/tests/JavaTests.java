package wyvern.targets.Java.tests;

import static wyvern.tools.types.TypeUtils.arrow;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import wyvern.stdlib.Globals;
import wyvern.targets.Java.visitors.JavaGenerator;
import wyvern.targets.Java.visitors.VariableResolver;
import wyvern.targets.JavaScript.parsers.JSLoadParser;
import wyvern.targets.JavaScript.typedAST.JSFunction;
import wyvern.targets.JavaScript.types.JSObjectType;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.simpleParser.Phase1Parser;
import wyvern.tools.typedAST.core.Keyword;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.KeywordNameBinding;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.core.binding.ValueBinding;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.types.extensions.Str;
import wyvern.tools.util.Pair;

@SuppressWarnings("unchecked")
public class JavaTests {
	
	private TypedAST doCompile(String input) {
		return doCompile(input, Environment.getEmptyEnvironment());
	}

	private TypedAST doCompile(String input, Environment ienv) {
		Reader reader = new StringReader(input);
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Environment env = Globals.getStandardEnv();
		env = env.extend(new ValueBinding("require", new JSFunction(arrow(Str.getInstance(),JSObjectType.getInstance()),"require")));
		env = env.extend(new KeywordNameBinding("load", new Keyword(new JSLoadParser())));
		env = env.extend(new TypeBinding("JSObject", JSObjectType.getInstance()));
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
					  "	class meth create() : Test = new\n" +
					  "	meth s () : Int =\n" +
					  "		1";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("CLASSTest");
		Object testObj = generated.getMethod("create").invoke(null);
		Object returned = generated.getMethod("s").invoke(testObj);
		Assert.assertEquals(returned, new Integer(1));
	}
	
	@Test
	public void testMethods2() throws Exception {
		String test = "class Test\n" +
					  "	class meth create() : Test = new\n" +
					  "	meth s (a : Int) : Int =\n" +
					  "		1";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("CLASSTest");
		Object testObj = generated.getMethod("create").invoke(null);
		Object returned = generated.getMethod("s", int.class).invoke(testObj, 1);
		Assert.assertEquals(returned, new Integer(1));
	}

	@Test
	public void testMethods3() throws Exception {
		String test = "class Test\n" +
					  "	class meth create() : Test = new\n" +
					  "	meth s (a : Int, b : Int) : Int =\n" +
					  "		1";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("CLASSTest");
		Object testObj = generated.getMethod("create").invoke(null);
		Object returned = generated.getMethod("s", int.class, int.class).invoke(testObj, 1, 2);
		Assert.assertEquals(returned, new Integer(1));
	}
	


	@Test
	public void testMethods4() throws Exception {
		String test = "class Test\n" +
					  "	class meth create() : Test = new\n" +
					  "	meth s (a : Int, b : Int) : Int =\n" +
					  "		a+b";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("CLASSTest");
		Object testObj = generated.getMethod("create").invoke(null);
		Object returned = generated.getMethod("s", int.class, int.class).invoke(testObj, 1, 2);
		Assert.assertEquals(returned, new Integer(3));
	}
	
	@Test
	public void testPlus() throws Exception {
		String test = "class Test\n" +
					  "	class meth create() : Test = new\n" +
					  "	meth s () : Int =\n" +
					  "		1+2";
		

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("CLASSTest");
		
		Object testObj = generated.getMethod("create").invoke(null);
		Object returned = generated.getMethod("s").invoke(testObj);
		Assert.assertEquals(returned, new Integer(3));
	}
	@Test
	public void testPlus2() throws Exception {
		String test = "class Test\n" +
					  "	class meth create() : Test = new\n" +
					  "	meth s () : Int =\n" +
					  "		1+2+3";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("CLASSTest");
		Object testObj = generated.getMethod("create").invoke(null);
		Object returned = generated.getMethod("s").invoke(testObj);
		Assert.assertEquals(returned, new Integer(6));
	}

	@Test
	public void testExecution() throws Exception {
		String test = "1+2+3";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("CLASSwycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(6));
		
	}
	
	@Test
	public void testVals() throws Exception {
		String test = "val t : Int = 2\n" +
					  "t";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("CLASSwycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(2));
		
	}
	@Test
	public void testVals2() throws Exception {
		String test = "val t : Int = 2\n" +
				      "val s : Int = 2\n" +
					  "t+s";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("CLASSwycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(4));
	}
	@Test
	public void testVals3() throws Exception {
		String test = "val t : Int = 2\n" +
				      "val s : Int = 2\n" +
					  "t+s+4";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("CLASSwycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(8));
	}
	@Test
	public void testVals4() throws Exception {
		String test = "class Test\n" +
					  "	class meth create() : Test = new\n" +
					  "	meth s () : Int =\n" +
					  "		val t : Int = 3\n" +
					  "		t";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("CLASSTest");
		Object testObj = generated.getMethod("create").invoke(null);
		Object returned = generated.getMethod("s").invoke(testObj);
		Assert.assertEquals(returned, new Integer(3));
	}
	@Test
	public void testVals5() throws Exception {
		String test = "class Test\n" +
					  "	class meth create() : Test = new\n" +
					  "	meth s (a : Int) : Int =\n" +
					  "		val t : Int = 3\n" +
					  "		t+a";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("CLASSTest");
		Object testObj = generated.getMethod("create").invoke(null);
		Object returned = generated.getMethod("s", int.class).invoke(testObj,3);
		Assert.assertEquals(returned, new Integer(6));
	}
	@Test
	public void testMeths() throws Exception {
		String test = "class Test\n" +
				  "	class meth create() : Test = new\n" +
				  "	meth n() : Int = 1\n" +
				  "val x : Test = Test.create()\n" +
				  "x.n()";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("CLASSwycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(1));
	}

	@Test
	public void testMeths2() throws Exception {
		String test = "class Test\n" +
				  "	class meth create() : Test = new\n" +
				  "	meth n(a : Int) : Int = a+1\n" +
				  "val x : Test = Test.create()\n" +
				  "x.n(10)";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("CLASSwycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(11));
	}
	
	@Test
	public void testMeths3() throws Exception {
		String test = "class Test\n" +
				  "	class meth create() : Test = new\n" +
				  "	meth n(a : Int) : Int = a+1\n" +
				  "	meth b(s : Int) : Int = this.n(s+1) + 2\n" +
				  "val x : Test = Test.create()\n" +
				  "x.b(10)";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("CLASSwycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(14));
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
					  "	class meth create() = new\n" +
					  "	meth s() : Int = x";
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
					  "meth Test() : Int = x";
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
					  "class Test\n" +
					  "	class meth create():Test = new\n" +
					  "	meth test():Int = n\n" +
					  "Test.create().test()";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("CLASSwycCode");
        Object returned = generated.getMethod("main").invoke(null);
        Assert.assertEquals(returned, new Integer(2));
    }
    @Test
    public void testClassClosure2() throws Exception {
        String test =
                "val n : Int = 2\n" +
                "val t : Int = 3\n" +
                "class Test\n" +
                "	class meth create():Test = new\n" +
                "	meth test():Int = n+t\n" +
                "Test.create().test()";

        ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
        Class generated = generatedLoader.loadClass("CLASSwycCode");
        Object returned = generated.getMethod("main").invoke(null);
        Assert.assertEquals(returned, new Integer(5));
    }

    @Test
    public void testClassClosure3() throws Exception {
        String test =
                        "type T\n" +
                        "	meth test():Int\n" +
                        "class Tester\n" +
                        "	class meth create():Tester = new\n" +
						"	meth test(e : Int):T =\n" +
                        "		class Inner\n" +
                        "			implements T\n" +
						"			class meth create():Inner = new\n" +
                        "			meth test():Int = e\n" +
						"		Inner.create()\n" +
                        "Tester.create().test(2).test() + Tester.create().test(3).test()";

        ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
        Class generated = generatedLoader.loadClass("CLASSwycCode");
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
        Class generated = generatedLoader.loadClass("CLASSwycCode");
        Object returned = generated.getMethod("main").invoke(null);
        Assert.assertEquals(returned, new Integer(3));
    }

    @Test
    public void testClassShadowing() throws Exception {
        String test =
                "class Test\n" +
                "   class meth create():Test = new\n" +
                "   meth a() : Int = 1\n" +
                "val y : Test = Test.create()\n"+
                "class Test\n" +
                "   class meth create():Test = new\n" +
                "   meth a():Int = 2\n" +
                "val x : Test = Test.create()\n" +
                "x.a() + y.a()";
        ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
        Class generated = generatedLoader.loadClass("CLASSwycCode");
        Object returned = generated.getMethod("main").invoke(null);
        Assert.assertEquals(returned, 3);
    }

    @Test
    public void testClassShadowing2() throws Exception {
        String test =
                "val x : Int = 1\n" +
                "class Test\n" +
                "   class meth create():Test = new\n" +
                "   meth a(i : Int) : Int =\n" +
                "       class Inner\n" +
                "           class meth create() : Test = new\n" +
                "           meth a() : Int = i\n" +
                "Test.create().a()";
    }

	@Test
	public void testTypeMeths1() throws Exception {
		String test = 
				  "type T\n" +
				  "	meth b(s:Int):Int\n" +
				  "class Test\n" +
				  "	implements T\n" +
				  "	class meth create() : Test = new\n" +
				  "	meth n(a : Int) : Int = a+1\n" +
				  "	meth b(s : Int) : Int = this.n(s+1) + 2\n" +
				  "val x : T = Test.create()\n" +
				  "x.b(10)";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("CLASSwycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(14));
	}
	
	
	//TODO: Dependent on closures
	@Test
	public void testInlineMeths() throws Exception {
		String test = 
				"meth a (s : Int) :Int = s\n" +
				"a(0)";

		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("CLASSwycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(0));
	}
	
	@Test
	public void testInlineFuncs() throws Exception {
		String test = "(fn (a : Int) = a+1)(2)";
		ClassLoader generatedLoader = JavaGenerator.GenerateBytecode(doCompile(test));
		Class generated = generatedLoader.loadClass("CLASSwycCode");
		Object returned = generated.getMethod("main").invoke(null);
		Assert.assertEquals(returned, new Integer(3));
	}
}
