package wyvern.tools.util;

import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.types.Environment;

/**
 * Created by Ben Chung on 8/8/13.
 */
public class CompilationContext {
	public CompilationContext(ExpressionSequence f, Environment s) {
		first = f;
		second = s;
	}

	public ExpressionSequence first;
	public Environment second;

	public String toString() {
		return "<" + first + "," + second + ">";
	}
}
