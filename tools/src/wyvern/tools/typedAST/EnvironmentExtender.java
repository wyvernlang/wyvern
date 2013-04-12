package wyvern.tools.typedAST;

import wyvern.tools.types.Environment;

public interface EnvironmentExtender {
	public Environment extend(Environment env);
	public Environment evalDecl(Environment env);
}
