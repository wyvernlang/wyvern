package wyvern.tools.typedAST.interfaces;

import wyvern.tools.types.Environment;

public interface EnvironmentExtender extends TypedAST {
	/**
	 * Extend the environment with later-initalized type names (to allow mutually-recursive types)
	 * @param env The environment to extend
	 * @return The new environment with bound names
	 */
	public Environment extendType(Environment env);

	/**
	 * Extend the environment with later-initalized method names (for mutually recursive methods)
	 * @param env The environment to extend
	 * @return The new environment with bound names
	 */
	public Environment extendName(Environment env);


	public Environment extend(Environment env);
	public Environment evalDecl(Environment env);
}
