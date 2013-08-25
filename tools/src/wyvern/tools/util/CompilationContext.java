package wyvern.tools.util;

import wyvern.stdlib.*;
import wyvern.stdlib.Compiler;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.typedAST.extensions.DSLDummy;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Tuple;

/**
 * Created by Ben Chung on 8/8/13.
 */
public class CompilationContext {
	private DSLDummy DSLToken;
	private CompilationContext globalCtx;
	private ExpressionSequence tokens;
	private Environment env;

	private Type expected = null;
	private Tuple expectedTuple = null;
	private Compiler.ImportCompileResolver resolver;

	public CompilationContext(CompilationContext globalCtx, ExpressionSequence f, Environment s) {
		setTokens(f);
		setEnv(s);
		this.globalCtx = globalCtx;
		if (globalCtx != null) {
			setExpected(globalCtx.expected);
			setExpectedTuple(globalCtx.getExpectedTuple());
			resolver = globalCtx.getResolver();
		} else {
			resolver = new Compiler.ImportCompileResolver();
		}
	}


	public String toString() {
		return "<" + getTokens() + "," + getEnv() + ">";
	}

	public ExpressionSequence getTokens() {
		return tokens;
	}

	public void setTokens(ExpressionSequence tokens) {
		this.tokens = tokens;
	}

	public Environment getEnv() {
		return env;
	}

	public void setEnv(Environment env) {
		this.env = env;
	}

	public CompilationContext copyAndClear() {
		CompilationContext newCtx = new CompilationContext(this, tokens, env);
		tokens = null;
		return newCtx;
	}

	public CompilationContext copyTokens(Environment newEnvironment) {
		return new CompilationContext(this, tokens, newEnvironment);
	}

	public CompilationContext copyEnv(ExpressionSequence tokens) {
		return new CompilationContext(this, tokens, env);
	}

	public RawAST popToken() {
		RawAST result = tokens.getFirst();
		tokens = tokens.getRest();
		return result;
	}

	public Type getExpected() {
		return expected;
	}

	public void setExpected(Type expected) {
		this.expected = expected;
	}

	public void setDSLToken(DSLDummy DSLToken) {
		this.DSLToken = DSLToken;
		if (globalCtx != null)
			globalCtx.setDSLToken(DSLToken);
	}

	public DSLDummy getDSLToken() {
		return DSLToken;
	}

	public Tuple getExpectedTuple() {
		return expectedTuple;
	}

	public void setExpectedTuple(Tuple expectedTuple) {
		this.expectedTuple = expectedTuple;
	}

	public Compiler.ImportCompileResolver getResolver() {
		return resolver;
	}
}
