package wyvern.tools.tests;

import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import wyvern.DSL.DSL;
import wyvern.stdlib.Compiler;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.ContParser;
import wyvern.tools.parsing.RecordTypeParser;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.util.Pair;

public class ModuleTests {
	@Test
	public void testSimpleModule() {
		ArrayList<String> strs = new ArrayList<>();
        strs.add(
				"import \"input:1\" as MI2\n" +
				"class C1\n" +
				"	class def t() : MI2.C2 = MI2.C2.create()\n" +
				"C1.t()");
        strs.add("" +
				"class C2\n" +
				"	class def create() : C2 = new\n" +
                "	def n():Int = 3\n");
        TypedAST pair = Compiler.compileSources("in1", strs, new ArrayList<DSL>());
		Compiler.flush();
		pair.typecheck(Environment.getEmptyEnvironment());
		pair.evaluate(Environment.getEmptyEnvironment());
	}

	@Test(expected = ToolError.class)
	public void testMutualRecusionFail() {
		ArrayList<String> strs = new ArrayList<>();
        strs.add(
				"import \"input:1\" as MI2\n" +
				"class C1\n" +
				"	class def t() : MI2.C2 = MI2.C2.create()");
        strs.add("" +
				"import \"input:0\" as MI1\n" +
				"class C2\n" +
				"	class def create() : C2 = new\n" +
                "	def n():Int = 3\n");
        TypedAST pair = Compiler.compileSources("in1", strs, new ArrayList<DSL>());
		Compiler.flush();
		pair.typecheck(Environment.getEmptyEnvironment());
	}

}
