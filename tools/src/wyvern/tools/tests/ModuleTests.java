package wyvern.tools.tests;

import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import wyvern.DSL.DSL;
import wyvern.stdlib.Compiler;
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
				"module M1\n" +
				"	import input:1\n" +
				"	class C1\n" +
				"		class def t() : M2.C2 = M2.C2.create()");
        strs.add("" +
				"module M2\n" +
				"	class C2\n" +
				"		class def create() : C2 = new\n" +
                "		def n():Int = 3\n");
        TypedAST pair = Compiler.compileSources("in1", strs, new ArrayList<DSL>());
		pair.typecheck(Environment.getEmptyEnvironment());
	}
	@Test
	public void testMutualRecursion() {

		ArrayList<String> strs = new ArrayList<>();
		strs.add(
				"module M1\n" +
						"	import input:1\n" +
						"	class C1\n" +
						"		class def create() : C1 = new\n" +
						"		class def t() : M2.C2 = M2.C2.create()");
		strs.add("" +
				"module M2\n" +
				"	import input:0\n" +
				"	class C2\n" +
				"		class def create() : C2 = new\n" +
				"		class def t() : M1.C1 = M1.C1.create()\n"+
				"		def n():Int = 3\n");
		TypedAST pair = Compiler.compileSources("in1", strs, new ArrayList<DSL>());
		pair.typecheck(Environment.getEmptyEnvironment());
	}
}
