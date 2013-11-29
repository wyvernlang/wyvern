package wyvern.tools.tests;

import org.junit.Assert;
import org.junit.Test;
import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Arrays;
import wyvern.DSL.DSL;
import wyvern.stdlib.Compiler;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.parsing.extensions.VerbParser;
import wyvern.tools.typedAST.core.Keyword;
import wyvern.tools.typedAST.core.binding.KeywordNameBinding;
import wyvern.tools.typedAST.core.expressions.Variable;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class TypeExtensionParsingTests {
	@Test
	public void testSimpleExtension() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("" +
				"type T\n" +
				"	def x():Int\n" +
				"	attributes\n" +
				"		val t : Int = 4\n" +
				"T.t\n");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs, new ArrayList<DSL>());
		Compiler.flush();
		pair.typecheck(Environment.getEmptyEnvironment());
		Assert.assertEquals(pair.evaluate(Environment.getEmptyEnvironment()), new IntegerConstant(4));
	}

	@Test
	public void testSimpleExtension2() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("" +
				"type T\n" +
				"	def x():Int\n" +
				"	attributes\n" +
				"		val t : Int = 4\n" +
				"		def x(n : Int) : Int = n+2\n" +
				"T.x(4)\n");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs, new ArrayList<DSL>());
		Compiler.flush();
		pair.typecheck(Environment.getEmptyEnvironment());
		Assert.assertEquals(pair.evaluate(Environment.getEmptyEnvironment()), new IntegerConstant(6));
	}

	@Test
	public void testSelfExn() throws InterruptedException {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("import \"input:1\" as In\n" +
				"val test : In.T = ~ \n" +
				"	4\n" +
				"test.x()");
		strs.add("" +
				"import \"java:wyvern.tools.typedAST.interfaces.TypedAST\" as AST\n" +
				"import \"java:wyvern.tools.typedAST.core.Application\" as App\n" +
				"import \"java:wyvern.tools.typedAST.core.Invocation\" as Inv\n" +
				"import \"java:wyvern.tools.typedAST.core.expressions.Variable\" as Var\n" +
				"import \"java:wyvern.tools.errors.FileLocation\" as FL\n" +
				"import \"java:wyvern.tools.parsing.ParseUtils\" as PU\n" +
                "import \"java:wyvern.tools.util.CompilationContext\" as CC\n" +
                "import \"java:wyvern.tools.typedAST.core.binding.NameBinding\" as NB\n" +
                "import \"java:wyvern.tools.typedAST.core.binding.NameBindingImpl\" as NBI\n" +
				"class TImpl\n" +
				"	implements T\n" +
				"	val xi : Int\n" +
				"	class def create(v : Int):TImpl\n" +
				"		new\n" +
				"			xi = v\n" +
				"	def x():Int\n" +
				"		this.xi\n" +
				"type T\n" +
				"	def x():Int\n" +
				"	attributes\n" +
				"		def parse(first : AST.TypedAST, ctx : CC.CompilationContext) : AST.TypedAST\n" +
				"			val value = PU.ParseUtils.parseExpr(ctx)\n" +
                "			quote\n" +
				"				In.TImpl.create(4)\n");
        DSL verb = new DSL() {
            @Override
            public Environment addToEnv(Environment in) {
                return in.extend(new KeywordNameBinding("quote", new Keyword(new VerbParser())));
            }
        };
		//Thread.sleep(10000);
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs, Arrays.asList(new DSL[] { verb }));
		Compiler.flush();
		pair.typecheck(Environment.getEmptyEnvironment());
		Value evaluate = pair.evaluate(Environment.getEmptyEnvironment());
		Assert.assertEquals(evaluate, new IntegerConstant(4));
	}

	@Test
	public void testList() throws IOException {
		String testFileName;
		URL url;

		testFileName = "wyvern/tools/tests/samples/list.wyv";
		url = TypeExtensionParsingTests.class.getClassLoader().getResource(testFileName);
		if (url == null) {
			Assert.fail("Unable to open " + testFileName + " file.");
			return;
		}

		InputStream is = url.openStream();
		Scanner reader = new Scanner(new InputStreamReader(is));
		ArrayList<String> strs = new ArrayList<>();
		strs.add(reader.useDelimiter("\\A").next());
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs, new ArrayList<DSL>());
		Compiler.flush();
		pair.typecheck(Environment.getEmptyEnvironment());
		Assert.assertEquals(pair.evaluate(Environment.getEmptyEnvironment()), new IntegerConstant(4950));
		is.close();
	}
}
