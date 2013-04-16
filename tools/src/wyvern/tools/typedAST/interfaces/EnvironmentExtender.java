package wyvern.tools.typedAST.interfaces;

import wyvern.tools.types.Environment;

public interface EnvironmentExtender {
	public Environment extend(Environment env);
	public Environment evalDecl(Environment env);
}
