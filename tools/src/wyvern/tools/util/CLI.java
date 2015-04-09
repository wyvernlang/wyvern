package wyvern.tools.util;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.stdlib.Globals;
import wyvern.tools.parsing.Wyvern;
import wyvern.tools.parsing.transformers.DSLTransformer;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Type;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class CLI {
	public static void main(String[] args) throws IOException, CopperParserException {
		if (args.length < 1) {
			throw new RuntimeException("Usage: wyvern <filename>");
		}

		File file = new File(args[0]);
		if (!file.canRead())
			throw new RuntimeException("Cannot read main file!");

		// For test.. by Stanley
		//Path path = Paths.get(args[0]);
		//Files.lines(path).forEach(s -> System.out.println(s));
		
		try (FileInputStream fis = new FileInputStream(file)) {
			TypedAST res = (TypedAST)new Wyvern().parse(new InputStreamReader(fis), "test input");
			Type checkedType = res.typecheck(Globals.getStandardEnv(), Optional.empty());
			System.out.println("Result type: "+checkedType);
			res = new DSLTransformer().transform(res);
			Value finalV = res.evaluate(Globals.getStandardEvalEnv());
			System.out.println("Result: "+finalV);
		}
	}
}