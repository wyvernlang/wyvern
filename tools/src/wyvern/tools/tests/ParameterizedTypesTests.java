package wyvern.tools.tests;

import org.junit.Test;
import wyvern.DSL.DSL;
import wyvern.stdlib.*;
import wyvern.stdlib.Compiler;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.simpleParser.Phase1Parser;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by Ben Chung on 1/8/14.
 */
public class ParameterizedTypesTests {

	private void compile(String src) {
		ArrayList<String> strs = new ArrayList<>();
		strs.add(src);
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs, new ArrayList<DSL>());
		Compiler.flush();
		pair.typecheck(Environment.getEmptyEnvironment());
		Value evaluate = pair.evaluate(Environment.getEmptyEnvironment());
	}

	@Test
	public void testSimpleParam() {
		String test = "class T[V]\n" +
				"2";
		compile(test);
	}
}
