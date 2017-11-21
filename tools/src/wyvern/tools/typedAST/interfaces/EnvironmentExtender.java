package wyvern.tools.typedAST.interfaces;

import wyvern.tools.types.Environment;
import wyvern.tools.util.EvaluationEnvironment;

public interface EnvironmentExtender extends TypedAST {
	/**
	 * Extend the environment with later-initalized type names (to allow mutually-recursive types)
	 * @param env The environment to extend
	 * @param against The environment to check against
	 * @return The new environment with bound names
	 */
	public default Environment extendType(Environment env, Environment against) {
	    throw new RuntimeException("deprecated");
	}


	/**
	 * Extend the environment with later-initalized method names (for mutually recursive methods)
	 * @param env The environment to extend
	 * @param against The environment to typecheck with
	 * @return The new environment with bound names
	 */
	public default Environment extendName(Environment env, Environment against) {
	    throw new RuntimeException("deprecated");
	}


	public Environment extend(Environment env, Environment against);

	public EvaluationEnvironment evalDecl(EvaluationEnvironment env);
}
