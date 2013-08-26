package wyvern.tools.tests;

import junit.framework.Assert;
import org.junit.Test;
import wyvern.DSL.DSL;
import wyvern.stdlib.Compiler;
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
}
