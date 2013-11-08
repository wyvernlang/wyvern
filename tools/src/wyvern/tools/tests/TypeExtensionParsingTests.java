package wyvern.tools.tests;

import org.junit.Assert;
import org.junit.Test;
import wyvern.DSL.DSL;
import wyvern.stdlib.Compiler;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.core.expressions.Variable;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;

import java.util.ArrayList;

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
	public void testSelfExn() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("import \"input:1\" as In\n" +
				"val test : In.T = ~ \n" +
				"	5\n" +
				"test");
		strs.add("" +
				"import \"java:wyvern.tools.typedAST.interfaces.TypedAST\" as AST\n" +
				"import \"java:wyvern.tools.typedAST.core.Application\" as App\n" +
				"import \"java:wyvern.tools.typedAST.core.Invocation\" as Inv\n" +
				"import \"java:wyvern.tools.typedAST.core.expressions.Variable\" as Var\n" +
				"import \"java:wyvern.tools.errors.FileLocation\" as FL\n" +
				"import \"java:wyvern.tools.parsing.ParseUtils\" as PU\n" +
                "import \"java:wyvern.tools.util.CompilationContext\" as CC\n" +
                "import \"java:wyvern.tools.typedAST.core.binding.NameBinding\" as NB\n" +
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
				"			App.Application.new( Inv.Invocation.new(Var.Variable.new(\"TImpl\",FL.FileLocation.UNKNOWN),\".\",\"create\",FL.FileLocation.UNKNOWN), " +
							"PU.ParseUtils.parseExpr(ctx), FL.FileLocation.UNKNOWN)\n");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs, new ArrayList<DSL>());
		Compiler.flush();
		pair.typecheck(Environment.getEmptyEnvironment());
		Assert.assertEquals(pair.evaluate(Environment.getEmptyEnvironment()), new IntegerConstant(6));

	}
}
