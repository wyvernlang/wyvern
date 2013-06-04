package wyvern.targets;

import wyvern.tools.typedAST.interfaces.TypedAST;

import java.io.IOException;

public interface Target {
	public void compile(TypedAST input, String outputDir) throws IOException;
}
