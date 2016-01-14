package wyvern.tools;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.stdlib.Globals;
import wyvern.tools.parsing.Wyvern;
import wyvern.tools.parsing.transformers.DSLTransformer;
import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.util.TreeWriter;

public class OldInterpreter {

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("usage: wyvern <filename>");
			System.exit(-1);
		}

		String filename = args[0];
		Path file = Paths.get(filename);
		if (!Files.isReadable(file)) {
			System.err.println("Cannot read file " + filename);
			System.exit(-1);
		}

		try {
			StringReader reader = new StringReader(new String(Files.readAllBytes(file), Charset.forName("UTF-8")) + "\n");
			TypedAST res = (TypedAST) new Wyvern().parse(reader, filename);
			res.typecheck(Globals.getStandardEnv(), Optional.empty());
			// System.out.println("Result 1 = " + res.evaluate(Globals.getStandardEnv()));
			// res = new DSLTransformer().transform(res); // FIXME: To make to work!
			Value finalV = res.evaluate(Globals.getStandardEvalEnv());
			// System.out.println("Result 2 = " + finalV);
			TreeWriter t = new TreeWriter();
			finalV.writeArgsToTree(t);
			System.out.println(t.getResult());
		} catch (IOException e) {
			System.err.println("Error reading file " + filename);
		} catch (CopperParserException e) {
			System.err.print("Parsing error: ");
			e.printStackTrace();
		}
	}

}
