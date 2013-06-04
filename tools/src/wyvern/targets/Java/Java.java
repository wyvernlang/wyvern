package wyvern.targets.Java;

import wyvern.targets.Java.visitors.JavaGenerator;
import wyvern.targets.Target;
import wyvern.tools.typedAST.interfaces.TypedAST;

import java.io.IOException;

public class Java implements Target {
	@Override
	public void compile(TypedAST input, String outputDir) throws IOException {
		JavaGenerator.generateFiles(input, outputDir);
	}
}
