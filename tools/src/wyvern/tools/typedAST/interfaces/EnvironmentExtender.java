package wyvern.tools.typedAST.interfaces;

import wyvern.tools.types.Environment;

public interface EnvironmentExtender extends TypedAST {
	public Environment extendTypes(Environment env);
	public Environment extendNames(Environment env);

	public Environment extend(Environment env);

	public Environment evalDecl(Environment env);
}
