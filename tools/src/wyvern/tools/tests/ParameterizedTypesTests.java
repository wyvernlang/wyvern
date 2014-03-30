package wyvern.tools.tests;

import org.junit.Test;
import wyvern.DSL.DSL;
import wyvern.stdlib.Compiler;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by Ben Chung on 1/8/14.
 */
public class ParameterizedTypesTests {

	private void compile(String src) {
		ArrayList<String> strs = new ArrayList<>();
		strs.add(src);
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs, new ArrayList<DSL>());
		Compiler.flush();
		pair.typecheck(Environment.getEmptyEnvironment(), Optional.empty());
		Value evaluate = pair.evaluate(Environment.getEmptyEnvironment());
	}

	@Test
	public void testSimpleParam() {
		String test = "class T[V]\n" +
				"2";
		compile(test);
	}
}
