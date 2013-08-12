package wyvern.tools.util;

import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.rawAST.StringLiteral;
import wyvern.tools.types.Environment;

/**
 * Created by Ben Chung on 8/8/13.
 */
public class CompilationContext {
	public CompilationContext(ExpressionSequence f, Environment s) {
		setTokens(f);
		setEnv(s);
	}

	private ExpressionSequence tokens;
	private Environment env;

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
		CompilationContext newCtx = new CompilationContext(tokens, env);
		tokens = null;
		return newCtx;
	}

	public CompilationContext copyTokens(Environment newEnvironment) {
		return new CompilationContext(tokens, newEnvironment);
	}

	public CompilationContext copyEnv(ExpressionSequence tokens) {
		return new CompilationContext(tokens, env);
	}

	public RawAST popToken() {
		RawAST result = tokens.getFirst();
		tokens = tokens.getRest();
		return result;
	}
}
